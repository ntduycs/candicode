package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
public class UpdateAdminRoleRequest extends Request {
    @NotNull
    @Size(min = 1)
    private Set<Long> roles;
}
