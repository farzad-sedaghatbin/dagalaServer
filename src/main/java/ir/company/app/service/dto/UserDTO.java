package ir.company.app.service.dto;


/**
 * A DTO representing a user's credentials
 */
public class UserDTO {

    private String username;
    private String password;
    private String name;
    private String tempUser;
    private String mobile;
    private String avatar;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTempUser() {
        return tempUser;
    }

    public void setTempUser(String tempUser) {
        this.tempUser = tempUser;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

}
