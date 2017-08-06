package ir.company.app.service.dto;

/**
 * A DTO representing a user's credentials
 */
public class ForgetPasswordDTO {

    private String password;
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginDTO{" +
            "password='" + password + '\'' +
            '}';
    }
}
