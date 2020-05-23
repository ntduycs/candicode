package vn.candicode.payloads.responses;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@RequiredArgsConstructor
public class ChallengeContent implements Serializable {
    @NonNull
    private String language;

    @NonNull
    private String text;
}
