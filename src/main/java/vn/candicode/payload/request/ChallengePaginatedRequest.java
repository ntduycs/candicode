package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChallengePaginatedRequest extends PaginatedRequest {
    private String author;
    private String title;
    private String tag;
    private String language;
    private Integer start;
    private Integer end;
    private String level;
    private Boolean contestChallenge;
    private String category;
}
