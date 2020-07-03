package vn.candicode.payload.response.sub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@RequiredArgsConstructor
public class Tag implements Serializable {
    private String name;
    private Integer count;
}
