package com.example.spa.servicesImpl;

import com.example.spa.dto.PaymentDTO;
import com.example.spa.entities.Payment;
import com.example.spa.repositories.PaymentRepository;
import com.example.spa.services.PaymentVNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;



import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Service
public class PaymentVNPayImpl implements PaymentVNPayService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Value("${vnp_TmnCode}")
    private String vnp_TmnCode;
    
    @Value("${vnp_HashSecret}")
    private String vnp_HashSecret;
    
    @Value("${vnp_ReturnUrl}")
    private String vnp_ReturnUrl;
    
    @Value("${vnp_Url}")
    private String paymentUrl;

    @Override
    public Payment createPayment(Long userId, Long amount, String paymentMethod, String status) {
        Payment payment_Infor = new Payment();
        payment_Infor.setPaymentId(userId);
        payment_Infor.setAmount(BigDecimal.valueOf(amount));
        payment_Infor.setPaymentMethod(paymentMethod);
        payment_Infor.setStatus(status);
        payment_Infor.setTransactionTime(LocalDateTime.now());

        return paymentRepository.save(payment_Infor);
    }

    @Override
    public String createVNPayPayment(Long userId, Long amount) {
        Payment payment = createPayment(userId, amount, "VNPay", "PENDING");

        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_OrderInfo", payment.getPaymentId().toString());
        params.put("vnp_OrderType", "other"); 
        params.put("vnp_TxnRef", String.valueOf(payment.getPaymentId())); // ID thanh toán
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

//    public static String hmacSHA512(String key, String data) {
//        try {
//            Mac mac = Mac.getInstance("HmacSHA512");
//            SecretKeySpec secretKey = new SecretKeySpec(
//                key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
//            mac.init(secretKey);
//            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
//            StringBuilder hexString = new StringBuilder();
//            for (byte b : hash) {
//                String hex = Integer.toHexString(0xff & b);
//                if (hex.length() == 1) hexString.append('0');
//                hexString.append(hex);
//            }
//            return hexString.toString();
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to calculate HMAC-SHA512", e);
//        }
//    }
    
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
    

    @Override
    public PaymentDTO handleVPNPaymentCallback(Map<String, String> params) throws UnsupportedEncodingException {
        // Lấy chữ ký từ phản hồi của VNPay
        String secureHash = params.get("vnp_SecureHash");
        System.out.println("Secure hash: " + secureHash);
        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        // Sắp xếp các tham số theo thứ tự từ a-z
        SortedMap<String, String> sortedParams = new TreeMap<>(params);
        System.out.println("Sorted params: " + sortedParams);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        // Tạo chuỗi tham số để tính toán chữ ký
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                hashData.append(entry.getKey()).append('=').append(entry.getValue()).append('&');
            }
        }

        // Loại bỏ dấu "&" cuối cùng
        if (hashData.length() > 0) {
            hashData.deleteCharAt(hashData.length() - 1);
        }

        String queryUrl = query.toString();
        
        // Tính toán chữ ký từ chuỗi hash
       // String generatedHash = hmacSHA512(vnp_HashSecret, hashData.toString());

        List<String> fieldNames = new ArrayList<String>(sortedParams.keySet());
        Collections.sort(fieldNames);
    
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) sortedParams.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        
        
        Long pamentId = Long.valueOf(params.get("vnp_TxnRef"));
        Optional<Payment> optionalPayment = paymentRepository.findById(pamentId);
        
        if (optionalPayment.isPresent()) {
			Payment payment_Infor = optionalPayment.get();
			
			String responseCode = sortedParams.get("vnp_ResponseCode");
			
			if ("00".equals(responseCode)) {
				payment_Infor.setStatus("SUCCESS");
				payment_Infor.setBankCode(params.get("vnp_BankCode"));
				payment_Infor.setTransactionTime(LocalDateTime.now());
			} else {
				payment_Infor.setStatus("FAILED");
			}
			
			paymentRepository.save(payment_Infor);
		}
        
        
        
        String vnp_SecureHash = hmacSHA512(vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        
        // So sánh chữ ký tính toán được và chữ ký từ VNPay
        
        return new PaymentDTO(sortedParams.get("vnp_ResponseCode"),
        		sortedParams.get("vnp_TransactionNo"),
        		sortedParams.get("vnp_Amount"),
        		sortedParams.get("vnp_BankCode"),
        		sortedParams.get("vnp_BankTranNo"),
        		paymentUrl + "?" + queryUrl);
    }



    @Override
    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id);
    }
}