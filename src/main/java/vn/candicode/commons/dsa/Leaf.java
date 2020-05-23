package vn.candicode.commons.dsa;

import lombok.Getter;

@Getter
public class Leaf implements Component {
    private final String key;
    private final Object value;
    private final String type;

    public Leaf(String key, Object value, String type) {
        this.key = key;
        this.value = value;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("{key: %s, value: %s, type: %s}", key, value, type);
    }
}
