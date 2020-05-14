package vn.candicode.payloads.validators;

import org.springframework.util.StringUtils;
import vn.candicode.utils.BeanUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueValidator implements ConstraintValidator<Unique, Object> {
    private String column;
    private UniqueValidatorService service;

    @Override
    public void initialize(Unique constraintAnnotation) {
        this.column = constraintAnnotation.column();

        Class<? extends UniqueValidatorService> service = constraintAnnotation.service();

        if (StringUtils.isEmpty(constraintAnnotation.qualifier())) {
            this.service = BeanUtils.getBean(constraintAnnotation.qualifier(), service);
        } else {
            this.service = BeanUtils.getBean(service);
        }
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        return service.isUnique(column, value);
    }
}
