package vn.candicode.payload.request;

import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

@Setter
public class NewTutorialRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    private List<String> categories;

    private Set<String> tags;

    private MultipartFile banner;

    @NotBlank
    private String content;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getCategories() {
        return categories;
    }

    public Set<String> getTags() {
        return tags;
    }

    public MultipartFile getBanner() {
        return banner;
    }

    public String getContent() {
        return content;
    }
}
