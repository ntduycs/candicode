package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginatedRequest extends Request {
    private int page = 1;
    private int size = 10;
    private String sort = "createdAt";
    private String direction = "desc";
}
