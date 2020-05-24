package vn.candicode.payloads.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class TestcaseRequest extends BaseRequest {
    @NotNull(message = "Field 'testcases' is required but not be given")
    @Size(min = 1, message = "Must contain at least 1 testcase")
    private List<Testcase> testcases;
}
