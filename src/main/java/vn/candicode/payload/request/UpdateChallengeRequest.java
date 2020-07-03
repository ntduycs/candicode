package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Getter
@Setter
public class UpdateChallengeRequest extends Request {
    private String title;
    private String level;
    private String description;
    private MultipartFile banner;
    private Set<String> tags;
    private Set<String> categories;
    private Boolean contestChallenge;
}
