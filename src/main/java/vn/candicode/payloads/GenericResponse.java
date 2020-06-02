package vn.candicode.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

import static org.springframework.http.HttpStatus.OK;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponse implements Serializable {
    public static final long serialVersionUID = 11012204L;

    private int code;

    private String message;

    private Object result;

    public static GenericResponse from(Object result, HttpStatus status) {
        if (status == null) {
            return new GenericResponse(OK.value(), OK.getReasonPhrase(), result);
        }

        return new GenericResponse(status.value(), status.getReasonPhrase(), result);
    }

    public static GenericResponse from(Object result) {
        return from(result, OK);
    }
}
