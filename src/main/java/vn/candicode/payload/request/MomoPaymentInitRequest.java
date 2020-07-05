package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class MomoPaymentInitRequest extends Request {
    @NotBlank(message = "Field 'plan' is required but not be given")
    private String plan;
}
