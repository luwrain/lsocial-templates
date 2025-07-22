@Service
public class CaptchaService {
    
    @Value("${recaptcha.secret-key}")
    private String secretKey;
    
    @Value("${recaptcha.site-key}")
    private String siteKey;
    
    @Autowired
    private RestTemplate restTemplate;

    public boolean validate(String captchaResponse) {
        if (captchaResponse == null || captchaResponse.isEmpty()) {
            return false;
        }
        
        String url = "https://www.google.com/recaptcha/api/siteverify";
        String params = "secret=" + secretKey + "&response=" + captchaResponse;
        
        try {
            String response = restTemplate.postForObject(url, params, String.class);
            JsonNode json = new ObjectMapper().readTree(response);
            return json.path("success").asBoolean();
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getSiteKey() {
        return siteKey;
    }
}
