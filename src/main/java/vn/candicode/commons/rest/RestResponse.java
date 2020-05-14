package vn.candicode.commons.rest;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@Getter
@Setter
@RequiredArgsConstructor
public class RestResponse implements Serializable {
    public static final long serialVersionUID = 1L;

    @NonNull
    private int code;

    @NonNull
    private String message;

    @NonNull
    private Object result;

    public static RestResponse build(Object data, @Nullable HttpStatus status) {
        if (status == null) {
            return new RestResponse(OK.value(), OK.getReasonPhrase(), data);
        }

        return new RestResponse(status.value(), status.getReasonPhrase(), data);
    }

    public static RestResponse build(Page<?> data, @Nullable HttpStatus status) {
        Map<String, Object> dataMap = Map.of(
            "page", data.getNumber(),
            "size", data.getSize(),
            "totalElements", data.getTotalElements(),
            "totalPages", data.getTotalPages(),
            "first", data.isFirst(),
            "last", data.isLast(),
            "items", data.getContent()
        );

        if (status == null) {
            return new RestResponse(OK.value(), OK.getReasonPhrase(), dataMap);
        }

        return new RestResponse(status.value(), status.getReasonPhrase(), dataMap);
    }
}
