package vn.candicode.payloads.requests;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.payloads.GenericRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class NewContestRequest extends GenericRequest {
    private String title;
    private List<String> tags;
    private MultipartFile banner;
    private String description;
    private List<ContestRound> rounds;
    private LocalDateTime registrationDeadline;
    private LocalDate startAt;
    private LocalDate endAt;
    private Integer maxAttendees;
}
