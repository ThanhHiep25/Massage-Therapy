package com.example.spa.servicesImpl;

import com.example.spa.dto.request.UserRegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private JavaMailSender mailSender;

    private static final long OTP_EXPIRATION_TIME = 60 * 1000;  // 5 phút
    // Lưu thông tin đăng ký tạm thời
    private Map<String, UserRegisterRequest> pendingUsers = new HashMap<>();

    // Lưu OTP và thời gian hết hạn
    private Map<String, OtpData> otpStorage = new HashMap<>();

    // Sinh OTP ngẫu nhiên
    public String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));  // OTP 6 chữ số
    }

    // Gửi OTP qua email
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Your OTP for Registration");
            message.setText("Your OTP code is: " + otp);
            mailSender.send(message);

            // Lưu OTP vào bộ nhớ với thời gian hết hạn
            otpStorage.put(toEmail, new OtpData(otp, Instant.now().toEpochMilli() + OTP_EXPIRATION_TIME));
        } catch (Exception e) {
            // Log lỗi nếu có sự cố khi gửi email
            e.printStackTrace();
            throw new RuntimeException("Error sending OTP email: " + e.getMessage());
        }
    }


//    public String verifyOtp(String email, String inputOtp) {
//        OtpData otpData = otpStorage.get(email);
//        if (otpData == null) {
//            return "OTP không tồn tại"; // OTP không tồn tại
//        }
//
//        long currentTime = Instant.now().toEpochMilli();
//        System.out.println("Thời gian hiện tại: " + currentTime);
//        System.out.println("Thời gian hết hạn: " + otpData.getExpirationTime());
//        System.out.println("Còn lại: " + (otpData.getExpirationTime() - currentTime) + " ms");
//
//
//        if (currentTime >= otpData.getExpirationTime()) {
//            otpStorage.remove(email);  // Xóa OTP đã hết hạn
//            return "OTP hết hạn"; // OTP hết hạn
//        }
//
//        // In ra OTP để kiểm tra
//        System.out.println("Stored OTP: " + otpData.getOtp());
//        System.out.println("Input OTP: " + inputOtp);
//
//        if (otpData.getOtp().equals(inputOtp)) {
//            otpStorage.remove(email);  // Xóa OTP sau khi xác thực thành công
//            return "Xác thực thành công"; // OTP hợp lệ
//        } else {
//            return "OTP không khớp"; // OTP không chính xác
//        }
//    }

    public String verifyOtp(String email, String inputOtp) {
        OtpData otpData = otpStorage.get(email);
        if (otpData == null) {
            return "OTP không tồn tại";
        }

        long currentTime = Instant.now().toEpochMilli();
        long expirationTime = otpData.getExpirationTime();
        long timeLeft = expirationTime - currentTime;

        System.out.println("Thời gian hiện tại: " + currentTime);
        System.out.println("Thời gian hết hạn: " + expirationTime);
        System.out.println("Còn lại: " + timeLeft + " ms");

        // Kiểm tra hết hạn
        if (timeLeft <= 0) {  // Kiểm tra điều kiện âm hoặc hết hạn
            otpStorage.remove(email);
            System.out.println("OTP đã hết hạn và bị xóa.");
            return "OTP hết hạn";
        }

        // Kiểm tra OTP
        if (otpData.getOtp().equals(inputOtp)) {
            otpStorage.remove(email);  // Xóa OTP sau khi dùng thành công
            System.out.println("Xác thực thành công và OTP đã bị xóa.");
            return "Xác thực thành công";
        } else {
            return "OTP không khớp";
        }
    }



    // Lưu thông tin người dùng tạm thời
    public void savePendingUser(UserRegisterRequest request) {
        pendingUsers.put(request.getEmail(), request);
    }

    // Lấy thông tin đăng ký tạm thời
    public UserRegisterRequest getPendingUser(String email) {
        return pendingUsers.get(email);
    }

    // Xóa thông tin người dùng tạm thời
    public void clearPendingUser(String email) {
        otpStorage.remove(email);
        pendingUsers.remove(email);
    }

    // Lớp phụ để lưu OTP và thời gian hết hạn
    private static class OtpData {
        private String otp;
        private long expirationTime;

        public OtpData(String otp, long expirationTime) {
            this.otp = otp;
            this.expirationTime = expirationTime;
        }

        public String getOtp() {
            return otp;
        }

        public long getExpirationTime() {
            return expirationTime;
        }
    }
}
