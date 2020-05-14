package vn.candicode.payloads.validators;

import vn.candicode.payloads.requests.ShouldConfirmPassword;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ConfirmedPasswordValidator implements ConstraintValidator<ConfirmedPassword, ShouldConfirmPassword> {
    @Override
    public boolean isValid(ShouldConfirmPassword o, ConstraintValidatorContext context) {
        return o.getPassword().equals(o.getConfirmPassword());
    }
}
