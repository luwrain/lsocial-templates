@Service
public class PasswordResetTokenService {
    
    @Autowired
    private PasswordResetTokenRepository tokenRepository;
    
    @Value("${password.reset.token.expiration}")
    private int expirationHours;

    public String createToken(User user) {
        String token = UUID.randomUUID().toString();
        
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(calculateExpiryDate());
        
        tokenRepository.save(resetToken);
        return token;
    }
    
    public boolean isValid(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
            .orElse(null);
            
        return resetToken != null && !isExpired(resetToken);
    }
    
    public User validateToken(String token) throws InvalidTokenException {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
            .orElseThrow(() -> new InvalidTokenException("Invalid token"));
            
        if (isExpired(resetToken)) {
            throw new InvalidTokenException("Token expired");
        }
        
        return resetToken.getUser();
    }
    
    private Date calculateExpiryDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, expirationHours);
        return cal.getTime();
    }
    
    private boolean isExpired(PasswordResetToken token) {
        return token.getExpiryDate().before(new Date());
    }
}
