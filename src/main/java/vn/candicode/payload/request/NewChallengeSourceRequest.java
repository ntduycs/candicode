package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.payload.request.validator.Zip;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class NewChallengeSourceRequest extends Request {
    @NotNull(message = "Field 'source' is required but not given")
    @Zip
    private MultipartFile source;
}
