package vn.candicode.payloads.requests;

import lombok.Getter;
import lombok.Setter;
import vn.candicode.payloads.GenericRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class UpdateTestcasesRequest extends GenericRequest {
    @NotNull(message = "Field 'testcases' is required but not be given")
    @Size(min = 1, message = "Must contain at least 1 testcase")
    @Valid
    private List<UpdatedTestcase> testcases;
}
