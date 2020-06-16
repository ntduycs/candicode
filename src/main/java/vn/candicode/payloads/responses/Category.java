package vn.candicode.payloads.responses;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Category implements Serializable {
    private Long categoryId;
    private String text;
    private Integer count;
    private List<Category> children = new ArrayList<>();
}
