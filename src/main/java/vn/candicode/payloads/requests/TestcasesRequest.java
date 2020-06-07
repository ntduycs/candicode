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
public class TestcasesRequest extends GenericRequest {
    private Double timeout;

    @NotNull(message = "Must contain at least one testcase")
    @Size(min = 1, message = "Must contain at least one testcase")
    @Valid
    private List<TestcaseRequest> testcases;
}
