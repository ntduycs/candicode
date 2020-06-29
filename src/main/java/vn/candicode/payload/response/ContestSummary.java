package vn.candicode.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
public class ContestSummary implements Serializable {
    private Long contestId;
    private String title;
    private String description;
    private Set<String> tags;
    private String registrationDeadline;
    private String banner;
    private Integer maxRegister;

    /**
     * Can be: upcoming, ongoing, finished
     */
    private String status;

    private String slug;

    private String author;

    private Boolean available;
}
