package vn.candicode.payload.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Getter
@RequiredArgsConstructor
public class MomoPaymentInitResponse implements Serializable {
    private final String payUrl;
}
