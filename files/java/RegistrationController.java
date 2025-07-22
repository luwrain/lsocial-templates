@Controller
@RequestMapping("/register")
public class RegistrationController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private CaptchaService captchaService;
    
    @Autowired
    private EmailService emailService;

    // Отображение формы регистрации
    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("SITE_KEY", captchaService.getSiteKey());
        return "register";
    }

    // Обработка POST-запроса регистрации
    @PostMapping
    public String registerUser(
        @RequestParam String login,
        @RequestParam String email,
        @RequestParam String password,
        @RequestParam String confirmPassword,
        @RequestParam("g-recaptcha-response") String captchaResponse,
        @RequestParam(required = false) Boolean marketing,
        Model model) {
        
        // Валидация капчи
        if (!captchaService.validate(captchaResponse)) {
            model.addAttribute("message", "Пожалуйста, подтвердите, что вы не робот");
            return fillModelAndReturn(model, login, email);
        }
        
        // Проверка совпадения паролей
        if (!password.equals(confirmPassword)) {
            model.addAttribute("message", "Пароли не совпадают");
            return fillModelAndReturn(model, login, email);
        }
        
        // Проверка сложности пароля
        if (!isPasswordValid(password)) {
            model.addAttribute("message", 
                "Пароль должен содержать минимум 8 символов, включая буквы и цифры");
            return fillModelAndReturn(model, login, email);
        }
        
        // Проверка уникальности nickname
        if (userService.loginExists(login)) {
            model.addAttribute("message", "Имя пользователя уже занято");
            return fillModelAndReturn(model, null, email);
        }
        
        // Проверка уникальности email
        if (userService.emailExists(email)) {
            model.addAttribute("message", "Email уже используется");
            return fillModelAndReturn(model, login, null);
        }
        
        // Создание пользователя
        User user = userService.createUser(
            login, 
            email, 
            password, 
            Boolean.TRUE.equals(marketing)
        );
        
        // Отправка email подтверждения
        emailService.sendVerificationEmail(user);
        
        // Автоматическая авторизация
        authService.authenticate(user);
        
        // Перенаправление с сообщением об успехе
        return "redirect:/dashboard?registration_success=true";
    }
    
    // Вспомогательный метод для заполнения модели
    private String fillModelAndReturn(Model model, String login, String email) {
        model.addAttribute("login", login);
        model.addAttribute("email", email);
        model.addAttribute("SITE_KEY", captchaService.getSiteKey());
        return "register";
    }
    
    // Валидация пароля
    private boolean isPasswordValid(String password) {
        return password.length() >= 8 
            && password.matches(".*\\d.*") 
            && password.matches(".*[a-zA-Z].*");
    }
}