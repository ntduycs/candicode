package vn.candicode.payload.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MomoPaymentRequest extends Request {
    private final String partnerCode;
    private final String accessKey;
    private final String requestId;
    private final String amount; // actual: long
    private final String orderId;
    private final String orderInfo;
    private final String returnUrl;
    private final String notifyUrl;
    private final String requestType;
    private final String signature;
    private final String extraData;
}
