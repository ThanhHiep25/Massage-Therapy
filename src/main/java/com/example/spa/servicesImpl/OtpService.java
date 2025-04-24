package com.example.spa.servicesImpl;

import com.example.spa.dto.request.UserRegisterRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private JavaMailSender mailSender;

    private static final long OTP_EXPIRATION_TIME = 3 * 60 * 1000;  // 3 phút

    // Lưu thông tin đăng ký tạm thời
    private final Map<String, UserRegisterRequest> pendingUsers = new HashMap<>();

    // Lưu OTP và thời gian hết hạn
    private final Map<String, OtpData> otpStorage = new HashMap<>();

    // Sinh OTP ngẫu nhiên
    public String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));  // OTP 6 chữ số
    }

    // Gửi OTP qua email
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("🔒 Xác nhận OTP của bạn");

            // 📨 Nội dung email có HTML đẹp hơn
            String emailContent = """
                <div style="font-family: Arial, sans-serif; max-width: 500px; padding: 20px; border: 1px solid #ddd; border-radius: 8px; background-color: #f9f9f9;">
                    <h2 style="color: #4CAF50; text-align: center;">🔑 Mã OTP của bạn</h2>
                    <p style="font-size: 16px; color: #333; text-align: center;">
                        Mã xác nhận của bạn là: <br>
                        <strong style="font-size: 24px; color: #d9534f;">%s</strong>
                    </p>
                    <p style="text-align: center; font-size: 14px; color: #666;">
                        Mã này sẽ hết hạn sau 3 phút. Nếu bạn không yêu cầu, vui lòng bỏ qua email này.
                    </p>
                    <div style="text-align: center; margin-top: 20px;">
                        <a href="#" style="background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; font-size: 16px;">
                            Xác nhận ngay
                        </a>
                    </div>
                </div>
            """.formatted(otp);

            helper.setText(emailContent, true); // Set nội dung email là HTML
            mailSender.send(message);

            // Lưu OTP vào bộ nhớ với thời gian hết hạn
            otpStorage.put(toEmail, new OtpData(otp, System.currentTimeMillis() + OTP_EXPIRATION_TIME));

        } catch (MessagingException e) {
            throw new RuntimeException("Lỗi gửi OTP: " + e.getMessage());
        }
    }


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
        private final String otp;
        private final long expirationTime;

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
