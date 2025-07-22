@Controller
public class PasswordResetController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordResetTokenService tokenService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private AuthService authService;

    // Показать форму запроса сброса
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }
    
    // Обработать запрос сброса
    @PostMapping("/forgot-password")
    public String processForgotPassword(
        @RequestParam String email,
        Model model,
        HttpServletRequest request) {
        
        try {
            User user = userService.findByEmail(email);
            if (user == null) {
                throw new UserNotFoundException("Пользователь с таким email не найден");
            }
            
            String token = tokenService.createToken(user);
            String resetUrl = generateResetUrl(request, token);
            
            emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);
            
            model.addAttribute("message", "Ссылка для сброса пароля отправлена на ваш email");
            model.addAttribute("messageType", "success");
        } catch (UserNotFoundException e) {
            model.addAttribute("message", e.getMessage());
            model.addAttribute("email", email);
        }
        
        return "forgot-password";
    }
    
    // Показать форму ввода нового пароля
    @GetMapping("/reset-password")
    public String showResetPasswordForm(
        @RequestParam String token,
        Model model) {
        
        if (!tokenService.isValid(token)) {
            model.addAttribute("message", "Недействительная или просроченная ссылка");
            return "reset-password-error";
        }
        
        model.addAttribute("token", token);
        return "reset-password";
    }
    
    // Обработать установку нового пароля
    @PostMapping("/reset-password")
    public String processResetPassword(
        @RequestParam String token,
        @RequestParam String password,
        @RequestParam String confirmPassword,
        Model model) {
        
        if (!password.equals(confirmPassword)) {
            model.addAttribute("message", "Пароли не совпадают");
            model.addAttribute("token", token);
            return "reset-password";
        }
        
        if (!isPasswordValid(password)) {
            model.addAttribute("message", 
                "Пароль должен содержать минимум 8 символов, включая буквы и цифры");
            model.addAttribute("token", token);
            return "reset-password";
        }
        
        try {
            User user = tokenService.validateToken(token);
            userService.updatePassword(user, password);
            authService.authenticate(user);
            
            model.addAttribute("message", "Пароль успешно изменен");
            model.addAttribute("messageType", "success");
            return "reset-password-success";
        } catch (InvalidTokenException e) {
            model.addAttribute("message", "Недействительная или просроченная ссылка");
            return "reset-password-error";
        }
    }
    
    private String generateResetUrl(HttpServletRequest request, String token) {
        return request.getScheme() + "://" + 
               request.getServerName() + 
               ":" + request.getServerPort() + 
               "/reset-password?token=" + token;
    }
    
    private boolean isPasswordValid(String password) {
        return password.length() >= 8 
            && password.matches(".*\\d.*") 
            && password.matches(".*[a-zA-Z].*");
    }
}
