package cf.ga2mer.candy.database;

public class Account {
    private long id;
    private long userId;
    private String firstName;
    private String lastName;
    private String accessToken;
    private String secret;
    private String avatar_url;
    private String status;
    public Account(){

    }
    public Account(long id, long userId, String accessToken, String secret, String firstName, String lastName, String avatar_url, String status) {
        this.id = id;
        this.userId = userId;
        this.accessToken = accessToken;
        this.secret = secret;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatar_url = avatar_url;
        this.status = status;
    }
    public Account(long userId, String accessToken, String secret, String firstName, String lastName, String avatar_url, String status) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.secret = secret;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatar_url = avatar_url;
        this.status = status;
    }

    public long getId() {
        return id;
    }
    public void setId(long id){
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId){
        this.userId = userId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getSecret() {
        return secret;
    }

    public void setAvatarURL(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getAvatarURL() {
        return avatar_url;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
