package ar.meetus.tasky.dto;

public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private String name;
    private String email;

    // Constructors
    public AuthResponse() {}

    public AuthResponse(String accessToken, String name, String email) {
        this.accessToken = accessToken;
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}