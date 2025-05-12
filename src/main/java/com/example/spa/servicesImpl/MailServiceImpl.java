package com.example.spa.servicesImpl;

import com.example.spa.dto.response.AppointmentResponse;
import com.example.spa.dto.response.ServiceSpaResponse;
import com.example.spa.entities.Payment;
import com.example.spa.services.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendMailSCHEDULED(String toEmail, AppointmentResponse appointment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("Spa Booking Notification - 🍃");

//            String servicesHtml = appointment.getServiceIds().stream()
//                    .map(service -> "<li>• " + service.getName() + "</li>")
//                    .collect(Collectors.joining());

            StringBuilder servicesHtml = new StringBuilder();
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
            symbols.setGroupingSeparator('.');
            DecimalFormat formatter = new DecimalFormat("#,###", symbols);

            for (ServiceSpaResponse service : appointment.getServiceIds()) {
                String imageUrl = (service.getImages() != null && !service.getImages().isEmpty())
                        ? service.getImages().get(0)
                        : "https://via.placeholder.com/100"; // Ảnh mặc định nếu không có ảnh

                String price = service.getPrice() != null
                        ? formatter.format(service.getPrice()) + "đ"
                        : "0đ";

                servicesHtml.append(String.format("""
                                   <li style="display: flex; align-items: center; gap: 16px; margin-bottom: 16px; background-color: #f7f7f7; padding: 12px; border-radius: 10px;">
                                                             <img src="%s" alt="%s" style="width: 80px; height: 80px; object-fit: cover; border-radius: 10px; border: 1px solid #ccc;">
                                                             <div style="flex: 1; margin-left: 10px;">
                                                                 <div style="font-size: 16px; font-weight: bold; color: #333;">%s</div>
                                                                 <div style="color: #4CAF50; margin: 4px 0;"> %s VND</div>
                                                             </div>
                                                         </li>
                                """,
                        imageUrl,
                        service.getName(),
                        service.getName(),
                        price
                ));


            }
            String totalFormatted = formatter.format(appointment.getTotalPrice()) + "đ";

            DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("HH:mm - EEEE, dd/MM/yyyy", new Locale("vi", "VN"));
            String formattedDateTime = appointment.getAppointmentDateTime().format(formatterDate);


            String emailContent = String.format("""
                                 <div style="font-family: Arial, sans-serif; padding: 24px; border: 1px solid #e0e0e0; border-radius: 8px; background-color: #fefefe;">
                                                  <div style="text-align: center;">
                                                      <img src="https://res.cloudinary.com/dokp7ig0u/image/upload/v1745136375/samples/seennn.jpg" alt="Spa Banner" style="max-width: 100%%; border-radius: 12px; margin-bottom: 20px;">
                                                      <h2 style="color: #4CAF50;">🍃 Đã xác nhận lịch hẹn!</h2>
                                                  </div>
                                                  <p style="font-size: 16px; color: #333; line-height: 1.5;">
                                                      Xin chào
                                                      <strong>%s</strong>,
                                                      <br>
                                                      Cảm ơn bạn đã đặt lịch hẹn tại spa của chúng tôi. Dưới đây là thông tin chi tiết:
                                                  </p>
                                                  <div style="background-color: #f7f7f7; padding: 12px; border-radius: 10px;">
                                                      <h3 style="color: #333;">Thông tin lịch hẹn ✨</h3>
                                                      <ul style="font-size: 15px; color: #444; line-height: 1.6; list-style-type: none; padding-left: 0;">
                                                          <li>
                                                              <strong>Khách hàng:</strong> %s
                                                          </li>
                                                          <li>
                                                              <strong>Email:</strong> %s
                                                          </li>
                                                          <li>
                                                              <strong>Số điện thoại:</strong> %s
                                                          </li>
                                                          <li>
                                                              <strong>Thời gian hẹn:</strong> %s
                                                          </li>
                                                          <li>
                                                              <strong>Ghi chú:</strong> %s
                                                          </li>
                                                          <li>
                                                              <strong>Trạng thái:</strong> %s
                                                          </li>
                                                          <li>
                                                              <strong>Tổng tiền:</strong> %s VND
                                                          </li>
                                                      </ul>
                                                  </div>
                                                  <hr style="border: none; border-top: 1px solid #e0e0e0; margin: 24px 0;">
                                                  <h3 style="color: #333;"> Dịch vụ đã đặt ✨</h3>
                                                  <ul style="padding: 0; list-style: none;">%s
                                                  </ul>
                                                  <hr style="border: none; border-top: 1px solid #e0e0e0; margin: 24px 0;">
                                                  <div style="font-size: 14px; color: #777; margin-top: 20px; line-height: 1.5;">
                                                      <p>Địa chỉ SPA: 123 Đường Spa, Quận Relax, Thành phố Chill</p>
                                                      <p>Email hỗ trợ : ocmd56@gmail.com</p>
                                                      <p>Hotline: 0123 456 789</p>
                                                      <p>Website: https://example.com</p>
                                                  </div>
                                                  <div style="text-align: center; margin-top: 24px;">
                                                      <a href="#" style="background-color: #82c785; color: white; padding: 10px 20px; border-radius: 5px; text-decoration: none; font-size: 12px;">
                                                          Truy cập để xem chi tiết
                                                      </a>
                                                  </div>
                                              </div>
                            """,
                    appointment.getUserId() != null ? appointment.getUserId().getName() : appointment.getGustName(),
                    appointment.getUserId() != null ? appointment.getUserId().getName() : appointment.getGustName(),
                    appointment.getUserId() != null ? appointment.getUserId().getEmail() : "N/A",
                    appointment.getUserId() != null ? appointment.getUserId().getPhone() : "N/A",
                    formattedDateTime,
                    appointment.getNotes() != null ? appointment.getNotes() : "(Không có)",
                    appointment.getStatus().name(),
                    totalFormatted,
                    servicesHtml
            );


            helper.setText(emailContent, true); // Set nội dung email là HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Lỗi gửi MAil xác nhận: " + e.getMessage());
        }

    }

    @Override
    public void sendMailPAIDPAYM(String email, Payment payment) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
            symbols.setGroupingSeparator('.');
            DecimalFormat formatter = new DecimalFormat("#,###", symbols);
            String totalFormatted = formatter.format(payment.getAmount()) + "đ";
            helper.setTo(email);
            helper.setSubject("THANH TOÁN THÀNH CÔNG");
            String emailPayment = String.format("""
                                 <div style="font-family: Arial, sans-serif; padding: 24px; border: 1px solid #e0e0e0; border-radius: 8px; background-color: #fefefe;">
                                                                <div style="text-align: center;">
                                                                    <img src="https://res.cloudinary.com/dokp7ig0u/image/upload/v1745136375/samples/seennn.jpg" alt="Spa Banner" style="max-width: 100%%; border-radius: 12px; margin-bottom: 20px;">
                                                                </div>
                                                                <div style="font-family: Arial, sans-serif; padding: 24px; border: 1px solid #e0e0e0; border-radius: 8px; background-color: #fefefe;">
                                                                    <h2 style="color: #4CAF50; text-align: center;">THANH TOÁN THÀNH CÔNG</h2>
                                                                    <p style="font-size: 16px; color: #333; text-align: center;">
                                                                        Cảm ơn quý khách hàng đã sử dụng dịch vụ tại chúng tôi.
                                                                    </p>
                                                                    <hr style="border: none; border-top: 1px solid #e0e0e0; margin: 24px 0;">
                                                                    <div style="font-size: 14px; color: #777; margin-top: 20px; line-height: 1.5;">
                                                                        <p>Mã hóa đơn: <span style="color: #4CAF50;">#%s</span></p>
                                                                        <p>Khách hàng: <span style="color: #4CAF50;">%s</span></p>
                                                                        <p>Ngày thanh toán: <span style="color: #4CAF50;">%s</span></p>
                                                                        <p>Phương thức thanh toán: <span style="color: #4CAF50;">%s</span></p>
                                                                        <p>Số tiền: <span style="color: #4CAF50;">%s VNĐ</span></p>
                                                                        <p>Mã giao dịch: <span style="color: #4CAF50;">%s</span></p>
                                                                    </div>
                                                                    <hr style="border: none; border-top: 1px solid #e0e0e0; margin: 24px 0;">
                                                                    <div style="font-size: 14px; color: #777; margin-top: 20px; line-height: 1.5;">
                                                                        <p>Địa chỉ: 123 Đường Spa, Quận Relax, Thành phố Chill</p>
                                                                        <p>Email hỗ trợ: ocmd56@gmail.com</p>
                                                                        <p>Hotline: 0123 456 789</p>
                                                                        <p>Website: https://example.com</p>
                                                                    </div>
                                                                </div>
                                                            </div>
                            """,
                    payment.getTransactionId(),
                    payment.getAppointment().getUser() != null ? payment.getAppointment().getUser().getName() : payment.getAppointment().getGuestName(),
                    payment.getPaymentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                    payment.getPaymentMethod(),
                    totalFormatted,
                    payment.getTransactionId() != null ? payment.getTransactionId() : "Không có"
            );

            helper.setText(emailPayment, true); // Set nội dung email là HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Lỗi gửi MAIL: " + e.getMessage());
        }
    }

    @Override
    public void CANCELAPPOINTMENT(String email) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setTo(email);
            helper.setSubject("THONG TIN LICH HEN");
            String emailContent = """
                          <div style="font-family: Arial, sans-serif; padding: 24px; border: 1px solid #e0e0e0; border-radius: 8px; background-color: #fefefe;">
                                   <div style="text-align: center;">
                                       <img src="https://res.cloudinary.com/dokp7ig0u/image/upload/v1745136375/samples/seennn.jpg" alt="Spa Banner" style="max-width: 100%%; border-radius: 12px; margin-bottom: 20px;">
                                   </div>
                                   <div style="font-family: Arial, sans-serif; padding: 24px; border: 1px solid #e0e0e0; border-radius: 8px; background-color: #fefefe;">
                                       <p style="color: white; background-color: rgba(247, 96, 96, 0.678); padding: 6px; border-radius: 10px; width: 200px;">Thông báo hủy lịch hẹn</p>
                                       <p style="font-size: 14px; color: #777; margin-top: 20px;">Chúng tôi rất tiết khi lịch hẹn đã bị hủy.</p>
                                       <p style="font-size: 14px; color: #777; margin-top: 20px;">Cảm ơn bạn đã quan tâm SPA Royal rất mong gặp lại bạn cho dịch vụ lần sau.</p>
                                       <div style="text-align: center; margin-top: 24px;">
                                           <a href="#" style="background-color: #82c785; color: white; padding: 10px 20px; border-radius: 5px; text-decoration: none; font-size: 12px;">
                                               Truy cập website
                                           </a>
                                       </div>
                                       <hr style="border: none; border-top: 1px solid #e0e0e0; margin: 24px 0;">
                                       <div style="font-size: 14px; color: #777; margin-top: 20px; line-height: 1.5;">
                                           <p>Địa_chi: 123 Đường Spa, Quận Relax, Thành phố Chill</p>
                                           <p>Email hỗ trợ : ocmd56@gmail.com</p>
                                           <p>Hotline: 0123 456 789</p>
                                           <p>Website: https://example.com</p>
                                       </div>
                                   </div>
                               </div>
                    """;

            helper.setText(emailContent, true); // Set nội dung email là HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Lỗi gửi MAIL: " + e.getMessage());
        }
    }

}
