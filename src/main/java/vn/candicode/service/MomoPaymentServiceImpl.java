package vn.candicode.service;

import com.google.common.hash.Hashing;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import vn.candicode.entity.*;
import vn.candicode.exception.BadRequestException;
import vn.candicode.exception.ResourceNotFoundException;
import vn.candicode.payload.request.MomoPaymentConfirmation;
import vn.candicode.payload.request.MomoPaymentInitRequest;
import vn.candicode.payload.request.MomoPaymentRequest;
import vn.candicode.payload.response.MomoPaymentInitResponse;
import vn.candicode.repository.PaymentRepository;
import vn.candicode.repository.StudentRepository;
import vn.candicode.security.UserPrincipal;
import vn.candicode.util.DatetimeUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;
import static vn.candicode.service.CommonService.Role.*;

@Service
@Log4j2
public class MomoPaymentServiceImpl implements PaymentService {
    private static final String MOMO_PARTNER_CODE = "MOMOSWDF20200708";
    private static final String MOMO_ACCESS_KEY = "9Q4GWGQpEGz3N46B";
    private static final String MOMO_SECRET_KEY = "6Fd30surzrCdvFZUobyEID0SvP9ZzJlj";
    private static final String MOMO_REQUEST_TYPE = "captureMoMoWallet";
    private static final String MOMO_RETURN_URL = "https://candicode.d1ta5379515jc6.amplifyapp.com/profile";
    private static final String MOMO_NOTIFY_URL = "https://candicode.ap-southeast-1.elasticbeanstalk.com/api/plans/confirm";
    private static final String MOMO_PAYMENT_ENDPOINT = "https://test-payment.momo.vn/gw_payment/transactionProcessor";

    private final CommonService commonService;

    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;

    public MomoPaymentServiceImpl(CommonService commonService, PaymentRepository paymentRepository, StudentRepository userRepository) {
        this.commonService = commonService;
        this.paymentRepository = paymentRepository;
        this.studentRepository = userRepository;
    }

    @Override
    public MomoPaymentInitResponse initPaymentTransaction(MomoPaymentInitRequest payload, UserPrincipal me) {
        final String planName = payload.getPlan().trim().toLowerCase();
        if (planName.equals("basic")) {
            throw new BadRequestException("Cannot upgrade to BASIC plan");
        }

        StudentPlanEntity plan = commonService.getPlans().get(planName);

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
            .extraData(me.getUserId() + "," + planName)
            .signature(Hashing.hmacSha256(MOMO_SECRET_KEY.getBytes()).hashString(generatePaymentInfo(paymentId, paymentInfo, plan.getPrice(), me.getUserId(), planName), StandardCharsets.UTF_8).toString())
            .build();

        RestTemplate httpClient = new RestTemplate();
        HttpEntity<MomoPaymentRequest> request = new HttpEntity<>(paymentRequest);
        ResponseEntity<?> response = httpClient.exchange(MOMO_PAYMENT_ENDPOINT, POST, request, Object.class);

        if (response.getStatusCode() == OK && response.getBody() != null) {
            @SuppressWarnings("unchecked")
            LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) response.getBody();

            if (body.get("errorCode").equals(0)) {
                return new MomoPaymentInitResponse((String) body.get("payUrl"));
            }
        }

        return null;
    }

    @Override
    @Transactional
    public void processPaymentConfirmedTransaction(MomoPaymentConfirmation payload) {
        PaymentEntity payment = new PaymentEntity();

        payment.setPaymentId(payload.getOrderId());
        payment.setPaymentInfo(payload.getOrderInfo());
        payment.setPaymentType(payload.getOrderType());
        payment.setAmount(Long.valueOf(payload.getAmount()));
        payment.setCompletedAt(LocalDateTime.parse(payload.getResponseTime() + ".000", DatetimeUtils.JSON_DATETIME_FORMAT));
        payment.setStatus(payload.getErrorCode());

        String[] transactionPayload = payload.getExtraData().split(",");
        Long ownerId = Long.valueOf(transactionPayload[0]);
        String planName = transactionPayload[1];

        StudentEntity owner = studentRepository.findById(ownerId)
            .orElseThrow(() -> new BadRequestException("Cannot determine owner of this transaction"));

        payment.setOwner(owner);

        owner.setStudentPlan(commonService.getPlans().get(planName));

        if (planName.equals("standard")) {
            List<RoleEntity> currentRoles = owner.getRoles().stream()
                .map(UserRoleEntity::getRole)
                .collect(Collectors.toList());

            if (!currentRoles.contains(commonService.getStudentRoles().get(CHALLENGE_CREATOR))) {
                owner.addRole(commonService.getStudentRoles().get(CHALLENGE_CREATOR));
            }
            if (!currentRoles.contains(commonService.getStudentRoles().get(TUTORIAL_CREATOR))) {
                owner.addRole(commonService.getStudentRoles().get(TUTORIAL_CREATOR));
            }
            if (!currentRoles.contains(commonService.getStudentRoles().get(CONTEST_CREATOR))) {
                owner.addRole(commonService.getStudentRoles().get(CONTEST_CREATOR));
            }
        } else if (planName.equals("premium")) {

        }

        studentRepository.save(owner);
        paymentRepository.save(payment);
    }

    private String generatePaymentInfo(String paymentId, String paymentInfo, Integer amount, Long userId, String planName) {
        StringBuilder info = new StringBuilder();

        info.append("partnerCode=").append(MOMO_PARTNER_CODE)
            .append("&accessKey=").append(MOMO_ACCESS_KEY)
            .append("&requestId=").append(paymentId)
            .append("&amount=").append(amount)
            .append("&orderId=").append(paymentId)
            .append("&orderInfo=").append(paymentInfo)
            .append("&returnUrl=").append(MOMO_RETURN_URL)
            .append("&notifyUrl=").append(MOMO_NOTIFY_URL)
            .append("&extraData=").append(userId + "," + planName);

        return info.toString();
    }
}
