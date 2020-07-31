package vn.candicode.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserProfile implements Serializable {
    private Long userId;
    private String avatar;
    private String firstName;
    private String lastName;
    private String fullName;
    private String slogan;
    private String facebook;
    private String github;
    private String linkedin;
    private String location;
    private String company;
    private String university;
    private Long gainedPoint;
    private List<SubmissionHistory> recentSubmissions = new ArrayList<>();
}
