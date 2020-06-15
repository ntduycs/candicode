package vn.candicode.payloads.requests;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.common.filesystem.FileType;
import vn.candicode.payloads.GenericRequest;
import vn.candicode.payloads.validators.FileTypeAcceptable;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
public class TutorialRequest extends GenericRequest {
    @NotBlank(message = "Field 'title' is required but not be given")
    private String title;

    private List<String> tags;

    @FileTypeAcceptable(value = {FileType.JPEG, FileType.JPG, FileType.PNG})
    private MultipartFile banner;

    @NotBlank(message = "Field 'content' is required but not be given")
    private String content;

    @NotBlank(message = "Field 'description' is required but not be given")
    private String description;
}
