package sillicat.setting.impl;

import sillicat.module.Module;
import sillicat.setting.Setting;

public class BindSetting extends Setting {
    private final Module module;

    public BindSetting(String name, Module module) {
        super(name);
        this.module = module;
    }

    public int getKey() {
        return module.getKey();
    }

    public void setKey(int key) {
        module.setKey(key);
    }
}
