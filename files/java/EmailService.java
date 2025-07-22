@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendPasswordResetEmail(String toEmail, String resetUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Восстановление пароля LUWRAN Social");
        message.setText(
            "Для сброса пароля перейдите по ссылке:\n" + 
            resetUrl + "\n\n" +
            "Если вы не запрашивали сброс пароля, проигнорируйте это письмо."
        );
        
        mailSender.send(message);
    }
}
