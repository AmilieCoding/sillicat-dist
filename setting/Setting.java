package sillicat.setting;

import lombok.Getter;

@Getter
public class Setting {
    private final String name;

    public Setting(String name) {
        this.name = name;
    }
}
