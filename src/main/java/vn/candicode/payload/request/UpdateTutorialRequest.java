package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

@Setter
@Getter
public class UpdateTutorialRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    private List<String> categories;

    private Set<String> tags;

    private MultipartFile banner;

    @NotBlank
    private String content;
}
