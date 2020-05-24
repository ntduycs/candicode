package vn.candicode.commons.rest;

import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class RestResponse implements Serializable {
    public static final long serialVersionUID = 1L;

    @NonNull
    private int code;

    @NonNull
    private String message;

    private Object result;

    public static RestResponse build(Object data, @Nullable HttpStatus status) {
        if (status == null) {
            return new RestResponse(OK.value(), OK.getReasonPhrase(), data);
        }

        return new RestResponse(status.value(), status.getReasonPhrase(), data);
    }

    public static RestResponse build(List<String> items, Page<?> dbRecords, @Nullable HttpStatus status) {
        Map<String, Object> dataMap = Map.of(
            "page", dbRecords.getNumber(),
            "size", dbRecords.getSize(),
            "totalElements", dbRecords.getTotalElements(),
            "totalPages", dbRecords.getTotalPages(),
            "first", dbRecords.isFirst(),
            "last", dbRecords.isLast(),
            "items", items
        );

        if (status == null) {
            return new RestResponse(OK.value(), OK.getReasonPhrase(), dataMap);
        }

        return new RestResponse(status.value(), status.getReasonPhrase(), dataMap);
    }
}
