package vn.candicode.commons.dsa;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Composite implements Component {
    private final String label;
    private final Object value;
    private final String type;
    private final List<Component> children;

    public Composite(String label, Object value, String type, List<Component> children) {
        this.label = label;
        this.value = value;
        this.type = type;
        this.children = children;
    }

    public Composite(String label, String value, String type) {
        this.label = label;
        this.value = value;
        this.type = type;
        this.children = new ArrayList<>();
    }

    public void addChild(Component child) {
        children.add(child);
    }

    public void removeChild(Component child) {
        children.remove(child);
    }

    @Override
    public String toString() {
        return String.format("{key: %s, value: %s, children: %s, type: %s}", label, value, children, type);
    }
}
