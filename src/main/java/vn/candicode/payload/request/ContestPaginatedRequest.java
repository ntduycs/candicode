package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContestPaginatedRequest extends PaginatedRequest {
    private String author;
    private String title;
    private String tag;
    private Integer start;
    private Integer end;
    private String status;
}
