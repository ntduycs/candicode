package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * After user has already confirmed his invoice, Momo will send this to Candicode for notification purpose
 */
@Getter
@Setter
public class MomoPaymentConfirmation extends Request {
    @NotNull
    private String partnerCode;
    @NotNull
    private String accessKey;
    @NotNull
    private String requestId;
    @NotNull
    private String amount;
    @NotNull
    private String orderId;
    @NotNull
    private String orderInfo;
    @NotNull
    private String orderType; // must be momo_wallet
    @NotNull
    private String transId;
    @NotNull
    private Integer errorCode;
    @NotNull
    private String message;
    @NotNull
    private String localMessage;
    @NotNull
    private String payType; // must be web or qr
    @NotNull
    private String responseTime; // in form of YYYY-MM-DD HH:mm:ss at GMT+00:07
    private String extraData;
    @NotNull
    private String signature;

    @Override
    public String toString() {
        return "MomoPaymentConfirmation{" +
            "partnerCode='" + partnerCode + '\'' +
            ", accessKey='" + accessKey + '\'' +
            ", requestId='" + requestId + '\'' +
            ", amount='" + amount + '\'' +
            ", orderId='" + orderId + '\'' +
            ", orderInfo='" + orderInfo + '\'' +
            ", orderType='" + orderType + '\'' +
            ", transId='" + transId + '\'' +
            ", errorCode=" + errorCode +
            ", message='" + message + '\'' +
            ", localMessage='" + localMessage + '\'' +
            ", payType='" + payType + '\'' +
            ", responseTime='" + responseTime + '\'' +
            ", extraData='" + extraData + '\'' +
            ", signature='" + signature + '\'' +
            '}';
    }
}
