package vn.candicode.payloads.requests;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.models.enums.ChallengeLevel;
import vn.candicode.payloads.validators.Enum;
import vn.candicode.payloads.validators.File;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@RequiredArgsConstructor
public class ChallengeMetadataRequest extends ChallengeMultipartRequest {

    @NotBlank(message = "Field 'title' is required but not be given")
    @NonNull
    private String title;

    @NotBlank(message = "Field 'level' is required but not be given")
    @Enum(target = ChallengeLevel.class)
    @NonNull
    private String level;

    @NotBlank(message = "Field 'description is required but not be given")
    @NonNull
    private String description;

    @File(mimes = {"image/jpeg", "image/png"}, required = false)
    private MultipartFile banner;

}
