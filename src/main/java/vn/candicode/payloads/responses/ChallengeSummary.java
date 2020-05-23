package vn.candicode.payloads.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ChallengeSummary implements Serializable {
    private Long id;
    private String title;
    private String banner;
    private String description;
    private String level;
    private Integer point;
    private List<String> languages;
    private Long numAttendees;
}
