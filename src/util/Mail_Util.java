package util;

import java.util.Properties;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class Mail_Util {

    private static final String FROM_EMAIL = "anh277160@gmail.com";
    private static final String APP_PASSWORD = "gqvo pmva pyzn ewdf";

    public static boolean guiMaQuenMatKhau(String toEmail, String ma) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Ma xac nhan doi mat khau - Hy Vong Restaurant");

            message.setText(
                    "Ma xac nhan doi mat khau cua ban la: " + ma +
                    "\nMa co hieu luc trong 5 phut." +
                    "\nNeu ban khong yeu cau doi mat khau, vui long bo qua email nay."
            );

            Transport.send(message);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}