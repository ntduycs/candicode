package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPaginatedRequest extends PaginatedRequest {
    private String firstName;
    private String lastName;
    private String type;
}
