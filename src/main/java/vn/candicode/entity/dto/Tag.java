package vn.candicode.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class Tag implements Serializable {
    private Long id;
    private Set<String> tags;
}
