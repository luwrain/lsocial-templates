@Controller
@RequestMapping("/oauth/yandex")
public class YandexAuthController {
    @Autowired
    private YandexAuthService yandexAuthService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthService authService;

    @GetMapping
    public String handleYandexAuth(
        @RequestParam String code,
        @RequestParam String state,
        HttpSession session,
        Model model) {
        
        String sessionState = (String) session.getAttribute("oauth_state");
        if (sessionState == null || !sessionState.equals(state)) {
            model.addAttribute("message", "Неверный state-параметр");
            return "login";
        }

        try {
            String userData = yandexAuthService.getUserData(code);
            JsonNode json = new ObjectMapper().readTree(userData);
            
            String email = json.path("default_email").asText();
            User user = userService.registerOrLoginOAuth(
                "yandex",
                json.path("id").asText(),
                email,
                json.path("real_name").asText()
            );
            
            authService.authenticate(user);
            return "redirect:/profile";
        } catch (IOException e) {
            model.addAttribute("message", "Ошибка связи с Яндекс");
            return "login";
        }
    }
}