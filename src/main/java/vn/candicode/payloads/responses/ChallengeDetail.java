package vn.candicode.payloads.responses;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class ChallengeDetail implements Serializable {
    @NonNull
    private String title;

    @NonNull
    private String description;

    @NonNull
    private String level;

    @NonNull
    private Integer points;

    @NonNull
    private String tcInputFormat;

    @NonNull
    private String tcOutputFormat;
}
