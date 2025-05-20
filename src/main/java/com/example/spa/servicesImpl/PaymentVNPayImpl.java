package com.example.spa.servicesImpl;

import com.example.spa.dto.PaymentDTO;
import com.example.spa.dto.response.AppointmentResponse;
import com.example.spa.dto.response.PaymentResponse;
import com.example.spa.entities.Appointment;
import com.example.spa.entities.Payment;
import com.example.spa.enums.AppointmentStatus;
import com.example.spa.enums.PaymentStatus;
import com.example.spa.exception.AppException;
import com.example.spa.exception.ErrorCode;
import com.example.spa.repositories.AppointmentRepository;
import com.example.spa.repositories.PaymentRepository;
import com.example.spa.services.MailService;
import com.example.spa.services.PaymentVNPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Service
@RequiredArgsConstructor
public class PaymentVNPayImpl implements PaymentVNPayService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AppointmentRepository appointmentRepository; // Inject AppointmentRepository

    @Value("${vnp_TmnCode}")
    private String vnp_TmnCode;

    @Value("${vnp_HashSecret}")
    private String vnp_HashSecret;

    @Value("${vnp_ReturnUrl}")
    private String vnp_ReturnUrl;

    @Value("${vnp_Url}")
    private String paymentUrl;

    private final MailService mailService;

    // Tạo lịch hẹn
    @Override
    public Payment createPayment(AppointmentResponse appointmentResponse, Long amount, String paymentMethod, String status) {
        // Lấy đối tượng Appointment từ database dựa trên ID trong AppointmentResponse
        Appointment appointment = appointmentRepository.findById(appointmentResponse.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn với ID: " + appointmentResponse.getId()));

        Payment payment = new Payment();
        payment.setAppointment(appointment); // Thiết lập đối tượng Appointment thực tế
        payment.setAmount(BigDecimal.valueOf(amount));
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionTime(LocalDateTime.now());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    @Override
    public Payment createCashPaymentForAppointment(Appointment appointment) {
        if (appointment == null) {
            throw new IllegalArgumentException("Order cannot be null for cash payment.");
        }
        if (appointment.getTotalPrice() == null) {
            throw new IllegalArgumentException("Order total amount cannot be null.");
        }

        Payment payment = new Payment();
        payment.setAppointment(appointment);
        payment.setAmount(appointment.getTotalPrice());
        payment.setPaymentMethod("Tiền mặt");
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionTime(LocalDateTime.now());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        payment.setTransactionId("CASH-" + appointment.getAppointmentId() + "-" + UUID.randomUUID().toString().substring(0, 8)); // Simple unique ID for cash
        payment.setIsDeposit(true); // Typically, a cash payment is a full payment/deposit
        Payment savedPayment = paymentRepository.save(payment);


        return savedPayment;
    }

    @Override
    public Payment createPaymentGooglePay(AppointmentResponse appointmentResponse, Long amount, String paymentMethod) {
        // Lấy đối tượng Appointment từ database dựa trên ID trong AppointmentResponse
        Appointment appointment = appointmentRepository.findById(appointmentResponse.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn với ID: " + appointmentResponse.getId()));

        Payment payment = new Payment();
        payment.setAppointment(appointment); // Thiết lập đối tượng Appointment thực tế
        payment.setAmount(BigDecimal.valueOf(amount));
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionTime(LocalDateTime.now());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    // Tạo URL thanh toán VNPay
    @Override
    public String createVNPayPayment(AppointmentResponse appointmentResponse, Long amount) {
        Payment payment = createPayment(appointmentResponse, amount, "VNPay", "PENDING");
        String txnRef = UUID.randomUUID().toString().replace("-", ""); // Tạo UUID làm vnp_TxnRef
        payment.setTransactionId(txnRef); // Lưu vnp_TxnRef vào cột transactionId
        paymentRepository.save(payment); // Lưu lại payment với transactionId mới

        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_OrderInfo", payment.getPaymentId().toString());
        params.put("vnp_OrderType", "other");
        params.put("vnp_TxnRef", txnRef); // ID thanh toán
        params.put("vnp_IpAddr", "127.0.0.1"); // Thực tế cần IP người dùng
        params.put("vnp_TmnCode", vnp_TmnCode);
        params.put("vnp_Amount", String.valueOf(amount * 100)); // VNPay tính bằng đồng
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        params.put("vnp_Locale", "vn");
        params.put("vnp_CreateDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        params.put("vnp_ExpireDate", LocalDateTime.now().plusMinutes(15).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        String paymentUrl = buildVNPayUrl(params);
        System.out.println("VNPay Payment URL created: " + paymentUrl);

        return paymentUrl;
    }

    private String buildVNPayUrl(Map<String, String> params) {
        try {
            SortedMap<String, String> sortedParams = new TreeMap<>(params);
            StringBuilder hashData = new StringBuilder();

            for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    hashData.append(entry.getKey())
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                            .append("&");
                }
            }

            if (hashData.length() > 0) {
                hashData.deleteCharAt(hashData.length() - 1);
            }

            String secureHash = hmacSHA512(vnp_HashSecret, hashData.toString());
            return paymentUrl + "?" + hashData + "&vnp_SecureHash=" + secureHash;

        } catch (Exception e) {
            throw new RuntimeException("Failed to build VNPay URL", e);
        }
    }

//    public static String hmacSHA512(final String key, final String data) {
//        try {
//            if (key == null || data == null) {
//                throw new NullPointerException();
//            }
//            final Mac hmac512 = Mac.getInstance("HmacSHA512");
//            byte[] hmacKeyBytes = key.getBytes();
//            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
//            hmac512.init(secretKey);
//            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
//            byte[] result = hmac512.doFinal(dataBytes);
//            StringBuilder sb = new StringBuilder(2 * result.length);
//            for (byte b : result) {
//                sb.append(String.format("%02x", b & 0xff));
//            }
//            return sb.toString();
//        } catch (Exception ex) {
//            return "";
//        }
//    }

    // HMAC-SHA512 (SHA-512 hash with a key)
    public static String hmacSHA512(final String key, final String data) {
        try {

            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }


    // Xây dựng URL thanh toán VNPay
    @Override
    public PaymentDTO handleVPNPaymentCallback(Map<String, String> params) throws UnsupportedEncodingException {
        String secureHash = params.get("vnp_SecureHash");
        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        SortedMap<String, String> sortedParams = new TreeMap<>(params);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                hashData.append(entry.getKey()).append('=').append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString())).append('&');
            }
        }
        if (hashData.length() > 0) {
            hashData.deleteCharAt(hashData.length() - 1);
        }

        List<String> fieldNames = new ArrayList<>(sortedParams.keySet());
        Collections.sort(fieldNames);
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = sortedParams.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString())).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String vnpTxnRef = params.get("vnp_TxnRef"); // Lấy vnp_TxnRef (UUID)
        Optional<Payment> optionalPayment = paymentRepository.findByTransactionId(vnpTxnRef); // Tìm kiếm theo transactionId

        if (optionalPayment.isPresent()) {
            Payment paymentInfo = optionalPayment.get();
            String responseCode = sortedParams.get("vnp_ResponseCode");

            if ("00".equals(responseCode)) {
                paymentInfo.setStatus(PaymentStatus.SUCCESS);
                paymentInfo.setBankCode(params.get("vnp_BankCode"));
                paymentInfo.setTransactionId(params.get("vnp_TransactionNo"));
                paymentInfo.setTransactionTime(LocalDateTime.now());
                paymentInfo.setPaymentDate(LocalDateTime.now()); // Cập nhật paymentDate khi thành công
                paymentInfo.setIsDeposit(true);
                paymentInfo.setUpdatedAt(LocalDateTime.now());
            } else {
                paymentInfo.setStatus(PaymentStatus.FAILED);
            }
            paymentRepository.save(paymentInfo);


            Appointment appointment = paymentInfo.getAppointment();
            if (appointment != null) {
                appointment.setStatus(AppointmentStatus.PAID);
                appointmentRepository.save(appointment);
            }

            // Lấy thông tin email người dùng và gửi email
            String userEmail = "";
            if (paymentInfo.getAppointment().getUser() != null) {
                userEmail = paymentInfo.getAppointment().getUser().getEmail();
            } else {
                // Xử lý trường hợp khách vãng lai (nếu có thông tin email)
                userEmail = "email_khach_vang_lai@example.com"; // Cần lấy email từ thông tin đặt lịch nếu có
            }
            if (!userEmail.isEmpty()) {
                mailService.sendMailPAIDPAYM(userEmail, paymentInfo);
                System.out.println("Đã gửi email thông báo thanh toán thành công đến: " + userEmail);
            } else {
                System.out.println("Không tìm thấy email người dùng để gửi thông báo thanh toán.");
            }
        }

        String vnp_SecureHashCalculated = hmacSHA512(vnp_HashSecret, hashData.toString());
        String vnp_SecureHashReceived = secureHash;

        System.out.println("Received Secure Hash: " + vnp_SecureHashReceived);
        System.out.println("Calculated Secure Hash: " + vnp_SecureHashCalculated);

//        if (!vnp_SecureHashReceived.equals(vnp_SecureHashCalculated)) {
//            System.out.println("WARNING: Chữ ký không hợp lệ từ VNPay!");
//            // Có thể ném exception hoặc trả về một DTO báo lỗi
//        }

        String queryUrl = paymentUrl + "?" + query.toString() + "&vnp_SecureHash=" + vnp_SecureHashCalculated;

        return new PaymentDTO(sortedParams.get("vnp_ResponseCode"),
                sortedParams.get("vnp_TransactionNo"),
                sortedParams.get("vnp_Amount"),
                sortedParams.get("vnp_BankCode"),
                sortedParams.get("vnp_BankTranNo"),
                queryUrl);
    }

    // Lấy tất cả thanh toán
    @Override
    public List<PaymentResponse> findAll() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream().map(PaymentResponse::new).collect(Collectors.toList());
    }


    // setStatus SUCCESS
    @Override
    public void setStatusSuccess(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_EXISTED));
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);
    }


    // Lấy thống tin thanh toán theo ID
    @Override
    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id);
    }


    // Thống kê thổng số lần thanh toán
    @Override
    public long countPayments() {
        return paymentRepository.countByStatus(PaymentStatus.SUCCESS);
    }

    // Thống kê thổng tiền thanh toán
    @Override
    public BigDecimal getSumAmount() {
        return paymentRepository.sumAllAmountPayment();
    }

    // Thống kê thổng tiền thanh toán theo tháng
    @Override
    public List<Map<String, Object>> getMonthlyPaymentRevenue() {
        List<Object[]> results = paymentRepository.sumPaymentAmountByMonth();
        List<Map<String, Object>> monthlyRevenue = new ArrayList<>();

        DecimalFormat defaultFormat = new DecimalFormat("#,##0.00");

        for (Object[] result : results) {
            Integer year = (Integer) result[0];
            Integer month = (Integer) result[1];
            BigDecimal totalAmount = (BigDecimal) result[2];

            Map<String, Object> monthData = new HashMap<>();
            monthData.put("year", year);
            monthData.put("month", month);
            monthData.put("totalRevenue", defaultFormat.format(totalAmount));
            monthlyRevenue.add(monthData);
        }
        return monthlyRevenue;
    }



}