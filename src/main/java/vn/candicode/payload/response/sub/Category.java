package vn.candicode.payload.response.sub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class Category implements Serializable {
    private Long categoryId;
    private String name;
    private Long count;
}
