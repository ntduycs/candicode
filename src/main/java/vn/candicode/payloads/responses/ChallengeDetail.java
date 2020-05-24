package vn.candicode.payloads.responses;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class ChallengeDetail implements Serializable {
    @NonNull
    private String title;

    @NonNull
    private String description;

    private String banner;

    @NonNull
    private String level;

    @NonNull
    private Integer points;

    @NonNull
    private String tcInputFormat;

    @NonNull
    private String tcOutputFormat;

    @NonNull
    private List<ChallengeContent> contents;

    @NonNull
    private List<Testcase> testcases;
}
