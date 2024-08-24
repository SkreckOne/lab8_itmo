package common.transfer;

import java.io.Serializable;

public class Session implements Serializable {
    private final String username;
    private final String password;
    private Integer userId;

    public Session(String username, String password) {
        this.username = username;
        this.password = password;
        this.userId = null;
    }

    public Integer getUserId() {return userId;}
    public void setUserId(Integer userId) {this.userId = userId;}
    public String getUsername() {return username;}
    public String getPassword() {return password;}
}
