package vn.candicode.payloads.validators;

import org.springframework.util.StringUtils;
import vn.candicode.payloads.requests.HasPassword;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ConfirmPasswordValidator extends GenericValidator implements ConstraintValidator<ConfirmPassword, HasPassword> {
    @Override
    public boolean isValid(HasPassword payload, ConstraintValidatorContext constraintValidatorContext) {
        if (!StringUtils.hasText(payload.getPassword())) {
            return NO_NEED_VALIDATE;
        }

        return payload.getPassword().equals(payload.getConfirmPassword());
    }
}
