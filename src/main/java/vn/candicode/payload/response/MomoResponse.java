package vn.candicode.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class MomoResponse implements Serializable {
    protected Integer errorCode;
    protected String message;
    protected String localMessage;
    protected List<Object> details;
}
