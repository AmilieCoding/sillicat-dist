package sillicat.setting.impl;

import lombok.Getter;
import lombok.Setter;
import sillicat.setting.Setting;

@Getter
@Setter
public class BooleanSetting extends Setting {
    private boolean value;

    public BooleanSetting(String name, boolean defaultValue){
        this.name = name;
        this.value = defaultValue;
    }

    public boolean isEnabled() {
        return value;
    }

    private void toggle(){
        value = !value;
    }
}
