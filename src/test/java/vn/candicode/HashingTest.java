package vn.candicode;

import com.google.common.hash.Hashing;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

public class HashingTest {
    @Test
    void hash() {
        String signature = Hashing.hmacSha256("63EclHR1gKfVSlAfylue0npTkOkaFGsk".getBytes()).hashString("binh duy", StandardCharsets.UTF_8).toString();

        Assertions.assertEquals("45e802b2cca6895bb76fc611add3501d5c84960c8512dca2fe911e3f3de64bfe", signature);

//        MomoPaymentRequest request = MomoPaymentRequest.builder()
//            .partnerCode("MOMOXQPA20200705")
//            .accessKey("BPTBjabbbIuCldLl")
//            .requestId("3add82e6-99a3-49e3-bf6e-c99b1919e68b")
//            .amount(String.valueOf(80000))
//            .requestType("captureMoMoWallet")
//            .extraData("nothing")
//            .notifyUrl("https://77bd96b4e2cf.ap.ngrok.io/api/plans/confirm")
//            .orderId("abc")
//            .orderInfo("Upgrade student plan using Momo payment gateway")
//            .returnUrl("https://www.google.com/")
//            .signature()


    }
}
