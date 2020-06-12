package vn.candicode.payloads.requests;

import lombok.Getter;
import lombok.Setter;
import vn.candicode.payloads.GenericRequest;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class RemoveTestcasesRequest extends GenericRequest {
    @NotNull(message = "Field 'testcaseIds' is required but not be given")
    @Size(min = 1, message = "Must contain at least 1 testcase")
    private List<Long> testcaseIds;
}
