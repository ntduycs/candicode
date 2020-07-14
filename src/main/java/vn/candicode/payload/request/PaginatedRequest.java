package vn.candicode.payload.request;

import lombok.Setter;

import java.util.List;

@Setter
public class PaginatedRequest extends Request {
    private Integer page;
    private Integer size;
    private String sort;
    private String direction;

    private Boolean contestChallenge;

    public Integer getPage() {
        return page == null || page <= 0 ? 1 : page;
    }

    public Integer getSize() {
        return size == null || size <= 0 ? 10 : size;
    }

    public String getSort() {
        return sort == null ? "createdAt" : sort;
    }

    public String getDirection() {
        return direction == null || !validDirection.contains(direction) ? "desc" : direction;
    }

    public Boolean getContestChallenge() {
        return contestChallenge != null ? contestChallenge : false;
    }

    protected static final List<String> validDirection = List.of("desc", "asc");
}
