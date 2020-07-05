package vn.candicode.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MomoPaymentResponse extends MomoResponse {
    private String requestId; // as in payment request
    private String requestType; // always captureMomoWallet
    private String payUrl;
    private String qrCodeUrl;
    private String deepLink;
    private String deepLinkWebInApp;
    private String signature;
}
