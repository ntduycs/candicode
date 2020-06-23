package vn.candicode.payloads.responses;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
public class ContestDetails implements Serializable {
    private Long contestId;
    private String title;
    private String author;
    private String createdAt;
    private Integer numComments;
    private Integer maxRegisters;
    private Integer currRegisters;
    private Set<String> tags;
    private String description;
    private String registrationDeadline;
    private String banner;
    private String content;
}
