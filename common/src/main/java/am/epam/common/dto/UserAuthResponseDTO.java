package am.epam.common.dto;

public class UserAuthResponseDTO {
    private String token;
    private UserDTO userDTO;

    public UserAuthResponseDTO() {
    }

    public UserAuthResponseDTO(String token, UserDTO userDTO) {
        this.token = token;
        this.userDTO = userDTO;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }
}
