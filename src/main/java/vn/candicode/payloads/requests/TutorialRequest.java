package vn.candicode.payloads.requests;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.payloads.GenericRequest;

import java.util.List;

@Getter
@Setter
public class TutorialRequest extends GenericRequest {
    private String title;
    private List<String> tags;
    private MultipartFile banner;
    private String content;
    private String description;
}
