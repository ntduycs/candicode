package vn.candicode.payloads.requests;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.models.enums.ChallengeLevel;
import vn.candicode.payloads.GenericRequest;
import vn.candicode.payloads.services.impl.UniqueChallengeValidator;
import vn.candicode.payloads.validators.Belong2Enum;
import vn.candicode.payloads.validators.FileTypeAcceptable;
import vn.candicode.payloads.validators.Unique;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

import static vn.candicode.common.filesystem.FileType.*;

@Getter
@Setter
public class EditChallengeRequest extends GenericRequest {
    @NotBlank(message = "Field 'title' is required but not be given")
    @Unique(service = UniqueChallengeValidator.class, column = "title", message = "Given title has already been in use")
    private String title;

    @NotBlank(message = "Field 'level' is required but not be given")
    @Belong2Enum(target = ChallengeLevel.class)
    private String level;

    @NotBlank(message = "Field 'description' is required but not be given")
    private String description;

    @FileTypeAcceptable(value = {PNG, JPEG, JPG})
    private MultipartFile banner;

    @NotNull(message = "Field 'tcInputFormat' is required but not be given")
    @Size(min = 1, message = "Field 'tcInputFormat' must contain at least 1 element")
    private List<String> tcInputFormat;

    @NotNull(message = "Field 'tcOutputFormat' is required but not be given")
    @Size(min = 1, message = "Field 'tcOutputFormat' must contain at least 1 element")
    private List<String> tcOutputFormat;
}
