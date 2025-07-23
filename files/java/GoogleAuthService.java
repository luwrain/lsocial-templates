@Service
public class GoogleAuthService {
    @Value("${google.client.id}")
    private String clientId;
    
    @Value("${google.client.secret}")
    private String clientSecret;
    
    @Value("${google.redirect.uri}")
    private String redirectUri;

    public String getAuthUrl(String state) {
        return "https://accounts.google.com/o/oauth2/v2/auth?" +
               "response_type=code" +
               "&client_id=" + clientId +
               "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
               "&scope=openid%20email%20profile" +
               "&state=" + state;
    }

    public OAuthUserInfo getUserData(String code) throws IOException {
        // 1. Получение токена
        String tokenUrl = "https://oauth2.googleapis.com/token";
        String params = "code=" + code +
                       "&client_id=" + clientId +
                       "&client_secret=" + clientSecret +
                       "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                       "&grant_type=authorization_code";
        
        String tokenResponse = HttpClient.newHttpClient().send(
            HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl))
                .POST(HttpRequest.BodyPublishers.ofString(params))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build(),
            HttpResponse.BodyHandlers.ofString()
        ).body();
        
        JsonNode tokenJson = new ObjectMapper().readTree(tokenResponse);
        String accessToken = tokenJson.path("access_token").asText();
        
        // 2. Получение данных пользователя
        String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";
        String userInfo = HttpClient.newHttpClient().send(
            HttpRequest.newBuilder()
                .uri(URI.create(userInfoUrl))
                .header("Authorization", "Bearer " + accessToken)
                .build(),
            HttpResponse.BodyHandlers.ofString()
        ).body();
        
        JsonNode userJson = new ObjectMapper().readTree(userInfo);
        return new OAuthUserInfo(
            userJson.path("id").asText(),
            userJson.path("email").asText(),
            userJson.path("name").asText()
        );
    }
}