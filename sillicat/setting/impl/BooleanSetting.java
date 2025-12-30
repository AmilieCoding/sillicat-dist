package sillicat.setting.impl;

import lombok.Getter;
import lombok.Setter;
import sillicat.setting.Setting;

@Getter
@Setter
public class BooleanSetting extends Setting {
    private boolean state;

    public BooleanSetting(String name, boolean state){
        this.name = name;
        this.state = state;
    }

    private boolean isEnabled() {
        return state;
    }

    private void toggle(){
        setState(!isEnabled());
    }
}
