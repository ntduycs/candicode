package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UpdateUserProfileRequest extends Request {
    private String firstName;
    private String lastName;
    private String slogan;
    private String facebook;
    private String github;
    private String linkedin;
    private String location;
    private String company;
    private String university;
    private MultipartFile avatar;
}
