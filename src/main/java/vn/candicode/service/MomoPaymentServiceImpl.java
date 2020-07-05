package vn.candicode.service;

import com.google.common.hash.Hashing;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import vn.candicode.entity.StudentPlanEntity;
import vn.candicode.exception.BadRequestException;
import vn.candicode.exception.ResourceNotFoundException;
import vn.candicode.payload.request.MomoPaymentConfirmation;
import vn.candicode.payload.request.MomoPaymentInitRequest;
import vn.candicode.payload.request.MomoPaymentRequest;
import vn.candicode.payload.response.MomoPaymentInitResponse;
import vn.candicode.repository.PaymentRepository;
import vn.candicode.security.UserPrincipal;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.UUID;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;

@Service
@Log4j2
public class MomoPaymentServiceImpl implements PaymentService {
    private static final String MOMO_PARTNER_CODE = "MOMOXQPA20200705";
    private static final String MOMO_ACCESS_KEY = "BPTBjabbbIuCldLl";
    private static final String MOMO_SECRET_KEY = "63EclHR1gKfVSlAfylue0npTkOkaFGsk";
    private static final String MOMO_REQUEST_TYPE = "captureMoMoWallet";
    private static final String MOMO_RETURN_URL = "https://www.google.com/";
    private static final String MOMO_NOTIFY_URL = "https://77bd96b4e2cf.ap.ngrok.io/api/plans/confirm";
    private static final String MOMO_PAYMENT_ENDPOINT = "https://test-payment.momo.vn/gw_payment/transactionProcessor";

    private final CommonService commonService;

    private final PaymentRepository paymentRepository;

    public MomoPaymentServiceImpl(CommonService commonService, PaymentRepository paymentRepository) {
        this.commonService = commonService;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public MomoPaymentInitResponse initPaymentTransaction(MomoPaymentInitRequest payload, UserPrincipal me) {
        if (payload.getPlan().trim().toLowerCase().equals("basic")) {
            throw new BadRequestException("Cannot upgrade to BASIC plan");
        }

        StudentPlanEntity plan = commonService.getPlans().get(payload.getPlan());

        if (plan == null) {
            throw new ResourceNotFoundException(StudentPlanEntity.class, "name", payload.getPlan());
        }

        final String paymentId = UUID.randomUUID().toString();
        final String paymentInfo = "Upgrade student plan using Momo payment gateway";

        @SuppressWarnings("UnstableApiUsage")
        MomoPaymentRequest paymentRequest = MomoPaymentRequest.builder()
            .partnerCode(MOMO_PARTNER_CODE)
            .accessKey(MOMO_ACCESS_KEY)
            .requestId(paymentId)
            .requestType(MOMO_REQUEST_TYPE)
            .returnUrl(MOMO_RETURN_URL)
            .notifyUrl(MOMO_NOTIFY_URL)
            .amount(String.valueOf(plan.getPrice()))
            .orderId(paymentId)
            .orderInfo(paymentInfo)
            .extraData("nothing")
            .signature(Hashing.hmacSha256(MOMO_SECRET_KEY.getBytes()).hashString(generatePaymentInfo(paymentId, paymentInfo, plan.getPrice()), StandardCharsets.UTF_8).toString())
            .build();

        RestTemplate httpClient = new RestTemplate();
        HttpEntity<MomoPaymentRequest> request = new HttpEntity<>(paymentRequest);
        ResponseEntity<?> response = httpClient.exchange(MOMO_PAYMENT_ENDPOINT, POST, request, Object.class);

        if (response.getStatusCode() == OK && response.getBody() != null) {
            LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) response.getBody();
            if (body.get("errorCode").equals(0)) {
                return new MomoPaymentInitResponse((String) body.get("payUrl"));
            }
        }

        return null;
    }

    @Override
    public void processPaymentConfirmedTransaction(MomoPaymentConfirmation payload) {

    }

    @Override
    public String generatePaymentInfo(String paymentId, String paymentInfo, Integer amount) {
        StringBuilder info = new StringBuilder();

        info.append("partnerCode=").append(MOMO_PARTNER_CODE)
            .append("&accessKey=").append(MOMO_ACCESS_KEY)
            .append("&requestId=").append(paymentId)
            .append("&amount=").append(amount)
            .append("&orderId=").append(paymentId)
            .append("&orderInfo=").append(paymentInfo)
            .append("&returnUrl=").append(MOMO_RETURN_URL)
            .append("&notifyUrl=").append(MOMO_NOTIFY_URL)
            .append("&extraData=").append("nothing");

        return info.toString();
    }
}
