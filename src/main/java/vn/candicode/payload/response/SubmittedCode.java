package vn.candicode.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SubmittedCode implements Serializable {
    private String code;
    private String language;
}
