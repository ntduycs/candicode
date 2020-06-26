package vn.candicode.payload.request;

import lombok.Setter;

import java.util.List;

@Setter
public class PaginatedRequest extends Request {
    private Integer page = 1;
    private Integer size = 10;
    private String sort = "createdAt";
    private String direction = "desc";

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

    private static final List<String> validDirection = List.of("desc", "asc");
}
