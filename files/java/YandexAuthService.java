@Service
public class YandexAuthService {
    @Value("${yandex.client.id}")
    private String clientId;
    
    @Value("${yandex.client.secret}")
    private String clientSecret;
    
    @Value("${yandex.redirect.uri}")
    private String redirectUri;

    public String getAuthUrl(String state) {
        return "https://oauth.yandex.ru/authorize?" +
               "response_type=code" +
               "&client_id=" + clientId +
               "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
               "&scope=login:email%20login:info" +
               "&state=" + state;
    }

    public String getUserData(String code) throws IOException {
        // 1. Получение токена
        String tokenUrl = "https://oauth.yandex.ru/token";
        String params = "grant_type=authorization_code" +
                       "&code=" + code +
                       "&client_id=" + clientId +
                       "&client_secret=" + clientSecret;
        
        String tokenResponse = HttpClient.newHttpClient().send(
            HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl))
                .POST(HttpRequest.BodyPublishers.ofString(params))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build(),
            HttpResponse.BodyHandlers.ofString()
        ).body();
        
        String accessToken = new ObjectMapper()
            .readTree(tokenResponse)
            .path("access_token")
            .asText();
        
        // 2. Получение данных пользователя
        String userInfoUrl = "https://login.yandex.ru/info?format=json";
        String userInfo = HttpClient.newHttpClient().send(
            HttpRequest.newBuilder()
                .uri(URI.create(userInfoUrl))
                .header("Authorization", "OAuth " + accessToken)
                .build(),
            HttpResponse.BodyHandlers.ofString()
        ).body();
        
        return userInfo;
    }
}