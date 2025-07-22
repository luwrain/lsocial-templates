@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean loginExists(String login) {
        return userRepository.existsByLogin(login);
    }
    
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public User createUser(String login, String email, String password, boolean marketing) {
        User user = new User();
        user.setLogin(login);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setMarketingOptIn(marketing);
        user.setVerificationToken(generateToken());
        user.setStatus(UserStatus.PENDING_VERIFICATION);
        return userRepository.save(user);
    }
    
    public User registerOrLoginOAuth(String provider, String providerId, String email, String name) {
        return userRepository.findByOauthId(providerId)
            .orElseGet(() -> {
                User newUser = new User();
                newUser.setLogin(generateUniqueLogin(name));
                newUser.setEmail(email);
                newUser.setOauthProvider(provider);
                newUser.setOauthId(providerId);
                newUser.setStatus(UserStatus.ACTIVE);
                return userRepository.save(newUser);
            });
    }
    
    private String generateToken() {
        return UUID.randomUUID().toString();
    }
    
    private String generateUniqueLogin(String name) {
        String base = name.toLowerCase()
                       .replaceAll("[^a-z0-9]", "")
                       .substring(0, Math.min(20, name.length()));
        
        String login = base;
        int counter = 1;
        
        while (loginExists(login)) {
            login = base + counter++;
        }
        
        return login;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}