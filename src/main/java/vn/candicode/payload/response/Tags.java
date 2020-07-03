package vn.candicode.payload.response;

import lombok.Getter;
import lombok.Setter;
import vn.candicode.payload.response.sub.Tag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Tags implements Serializable {
    private List<Tag> tags = new ArrayList<>();

    public Tags(Map<String, Integer> map, Integer size) {
        map.forEach((tag, count) -> tags.add(new Tag(tag, count)));

        tags.sort((a, b) -> b.getCount() - a.getCount());

        if (tags.size() > size) {
            tags = tags.subList(0, size);
        }
    }

}
