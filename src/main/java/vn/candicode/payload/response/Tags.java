package vn.candicode.payload.response;

import lombok.Getter;
import lombok.Setter;
import vn.candicode.payload.response.sub.Tag;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class Tags implements Serializable {
    private List<Tag> tags;
}
