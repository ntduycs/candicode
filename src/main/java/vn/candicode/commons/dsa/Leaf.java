package vn.candicode.commons.dsa;

import lombok.Getter;

@Getter
public class Leaf implements Component {
    private final String label;
    private final Object value;
    private final String type;

    public Leaf(String label, Object value, String type) {
        this.label = label;
        this.value = value;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("{key: %s, value: %s, type: %s}", label, value, type);
    }
}
