@Controller
@RequestMapping("/oauth/google")
public class GoogleAuthController {
    @Autowired
    private GoogleAuthService googleAuthService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthService authService;

    @GetMapping
    public String handleGoogleAuth(
        @RequestParam(required = false) String code,
        @RequestParam(required = false) String error,
        @RequestParam(required = false) String state,
        HttpSession session,
        Model model) {
        
        if (error != null) {
            model.addAttribute("message", "Доступ запрещен");
            return "login";
        }
        
        String sessionState = (String) session.getAttribute("oauth_state");
        if (sessionState == null || !sessionState.equals(state)) {
            model.addAttribute("message", "Неверный state-параметр");
            return "login";
        }
        
        try {
            OAuthUserInfo userInfo = googleAuthService.getUserData(code);
            User user = userService.registerOrLoginOAuth(
                "google", 
                userInfo.getId(), 
                userInfo.getEmail(), 
                userInfo.getName()
            );
            authService.authenticate(user);
            return "redirect:/profile";
        } catch (IOException e) {
            model.addAttribute("message", "Ошибка подключения к Google");
            return "login";
        }
    }
}