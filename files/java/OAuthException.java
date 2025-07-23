public class OAuthException extends Exception {
    public OAuthException(String message) {
        super(message);
    }
}

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}

public class InvalidTokenException extends Exception {
    public InvalidTokenException(String message) {
        super(message);
    }
}

public class OAuth2AuthenticationException extends RuntimeException {
    public OAuth2AuthenticationException(String message) {
        super(message);
    }
}
