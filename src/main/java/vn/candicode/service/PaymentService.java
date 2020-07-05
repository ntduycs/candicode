package vn.candicode.service;

import vn.candicode.payload.request.MomoPaymentConfirmation;
import vn.candicode.payload.request.MomoPaymentInitRequest;
import vn.candicode.payload.response.MomoPaymentInitResponse;
import vn.candicode.security.UserPrincipal;

public interface PaymentService {
    MomoPaymentInitResponse initPaymentTransaction(MomoPaymentInitRequest payload, UserPrincipal me);

    void processPaymentConfirmedTransaction(MomoPaymentConfirmation payload);

    String generatePaymentInfo(String paymentId, String paymentInfo, Integer amount);
}
