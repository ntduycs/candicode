package vn.candicode.payload.response;

import lombok.Getter;
import lombok.Setter;
import vn.candicode.payload.response.sub.Leader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ContestDetails implements Serializable {
    private Long contestId;
    private String title;
    private String description;
    private Set<String> tags;
    private String registrationDeadline;
    private String banner;
    private String content;
    private Integer maxRegister;

    /**
     * Can be: upcoming, ongoing, finished
     */
    private String status;

    private String slug;

    private String author;

    private Boolean available;

    private List<ContestRound> rounds = new ArrayList<>();

    private Boolean enrolled;

    private List<Leader> leaders = new ArrayList<>();

    private String createdAt;
}
