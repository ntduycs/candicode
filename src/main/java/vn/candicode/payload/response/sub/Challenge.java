package vn.candicode.payload.response.sub;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Challenge implements Serializable {
    private String language;
    private String text;

    public Challenge(String language, String text) {
        this.language = language;
        this.text = text;
    }
}
