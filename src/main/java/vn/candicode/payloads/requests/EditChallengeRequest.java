package vn.candicode.payloads.requests;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.models.enums.ChallengeLevel;
import vn.candicode.payloads.GenericRequest;
import vn.candicode.payloads.validators.Belong2Enum;
import vn.candicode.payloads.validators.FileTypeAcceptable;

import javax.validation.constraints.NotBlank;
import java.util.List;

import static vn.candicode.common.filesystem.FileType.*;

@Getter
@Setter
public class EditChallengeRequest extends GenericRequest {
    @NotBlank(message = "Field 'title' is required but not be given")
    private String title;

    @NotBlank(message = "Field 'level' is required but not be given")
    @Belong2Enum(target = ChallengeLevel.class)
    private String level;

    @NotBlank(message = "Field 'description' is required but not be given")
    private String description;

    @FileTypeAcceptable(value = {PNG, JPEG, JPG})
    private MultipartFile banner;

    private List<String> tags;
}
