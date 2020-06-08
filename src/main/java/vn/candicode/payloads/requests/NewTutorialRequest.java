package vn.candicode.payloads.requests;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.payloads.GenericRequest;

import java.util.List;

@Getter
@Setter
public class NewTutorialRequest extends GenericRequest {
    private String title;
    private String introduction;
    private List<String> categories;
    private MultipartFile banner;
    private String content;
}
