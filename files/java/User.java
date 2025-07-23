@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String login;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;

    @Column(name = "oauth_provider")
    private String oauthProvider; // "yandex", "google"

    @Column(name = "oauth_id")
    private String oauthId; // Уникальный ID провайдера
    
    private boolean marketingOptIn;
    
    private String verificationToken;
    
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    
    // Геттеры и сеттеры
}

public enum UserStatus {
    PENDING_VERIFICATION,
    ACTIVE,
    BLOCKED
}
