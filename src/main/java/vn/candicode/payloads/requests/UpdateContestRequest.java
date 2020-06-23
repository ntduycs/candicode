package vn.candicode.payloads.requests;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.payloads.GenericRequest;

import java.util.Set;

@Getter
@Setter
public class UpdateContestRequest extends GenericRequest {
    private String title;
    private Set<String> tags;
    private MultipartFile banner;
    private String description;
    //    private List<ContestRound> rounds;
    private String registrationDeadline;
    private Integer maxRegister;
    private String content;
}
