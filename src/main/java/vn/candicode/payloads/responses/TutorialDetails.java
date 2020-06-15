package vn.candicode.payloads.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TutorialDetails implements Serializable {
    private Long tutorialId;
    private String title;
    private List<String> tags = new ArrayList<>();
    private String banner;
    private String content;
    private String description;
    private Long numComments;
    private List<Comment> comments = new ArrayList<>();
    private Integer likes;
    private Integer dislikes;
    private List<String> categories = new ArrayList<>();
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime updatedAt;
    private String author;
}
