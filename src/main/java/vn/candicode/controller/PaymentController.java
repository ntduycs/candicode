package vn.candicode.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vn.candicode.payload.ResponseFactory;
import vn.candicode.payload.request.MomoPaymentConfirmation;
import vn.candicode.payload.request.MomoPaymentInitRequest;
import vn.candicode.payload.response.MomoPaymentInitResponse;
import vn.candicode.security.CurrentUser;
import vn.candicode.security.UserPrincipal;
import vn.candicode.service.PaymentService;

import javax.validation.Valid;

@RestController
@Log4j2
public class PaymentController extends Controller {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    protected String getPath() {
        return "payments";
    }

    @PostMapping(path = "plans")
    public ResponseEntity<?> initUpgradeStudentPlanProcess(@RequestBody @Valid MomoPaymentInitRequest payload, @CurrentUser UserPrincipal me) {
        MomoPaymentInitResponse response = paymentService.initPaymentTransaction(payload, me);

        return ResponseEntity.ok(ResponseFactory.build(response));
    }

    @PostMapping(path = "plans/confirm", consumes = {"application/x-www-form-urlencoded"})
    public void momoConfirm(@ModelAttribute @Valid MomoPaymentConfirmation payload) {
        log.info(payload);

        paymentService.processPaymentConfirmedTransaction(payload);
    }
}
