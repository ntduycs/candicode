package vn.candicode.payload.request.validator;

import vn.candicode.payload.request.PasswordConfirmable;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ConfirmPasswordValidator implements ConstraintValidator<PasswordConfirm, Object> {
    @Override
    public boolean isValid(Object o, ConstraintValidatorContext context) {
        PasswordConfirmable request = (PasswordConfirmable) o;
        return request.getPassword().equals(request.getConfirmPassword());
    }
}
