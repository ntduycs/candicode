package vn.candicode.payloads.validators;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import vn.candicode.models.enums.ChallengeLevel;
import vn.candicode.payloads.requests.ChallengeMetadataRequest;

import java.util.List;

@Component("challengeMetadata")
public class ChallengeMetadataRequestValidator implements Validator {
    @Override
    public boolean supports(@NonNull Class<?> aClass) {
        return ChallengeMetadataRequest.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(@NonNull Object o, @NonNull Errors errors) {
        ChallengeMetadataRequest request = (ChallengeMetadataRequest) o;

        if (!StringUtils.hasText(request.getTitle())) {
            errors.rejectValue("title", "challenge.field.missing", new String[]{"title"}, "Required field is missing");
        }

        if (!StringUtils.hasText(request.getLevel())) {
            errors.rejectValue("level", "challenge.field.missing", new String[]{"level"}, "Required field is missing");
        } else {
            try {
                ChallengeLevel.valueOf(request.getLevel().toUpperCase());
            } catch (IllegalArgumentException e) {
                errors.rejectValue("level", "challenge.enum.not-found", new String[]{ChallengeLevel.class.getSimpleName()}, "Given value not belong the enum");
            }
        }

        if (!StringUtils.hasText(request.getDescription())) {
            errors.rejectValue("description", "challenge.field.missing", new String[]{"description"}, "Required field is missing");
        }

        if (request.getBanner() != null && !request.getBanner().isEmpty()) {
            if (!List.of("image/jpeg", "image/png").contains(request.getBanner().getContentType())) {
                errors.rejectValue("banner", "challenge.mime-type.not-supported", new String[]{request.getBanner().getContentType()}, "Mime-type is not supported");
            }
        }
    }
}
