package vn.candicode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.entity.PaymentEntity;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {
    Optional<PaymentEntity> findByPaymentId(String paymentId);
}
