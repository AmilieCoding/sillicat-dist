package sillicat.setting.impl;

import lombok.Getter;
import lombok.Setter;
import sillicat.setting.Setting;

@Getter
@Setter
public class NumberSetting extends Setting {
    private double val, minVal, maxVal, defaultVal, increment;

    public NumberSetting(String name, double defaultVal, double minVal, double maxVal, double increment){
        super(name);
        this.val = defaultVal;
        this.defaultVal = defaultVal;
        this.maxVal = maxVal;
        this.minVal = minVal;
        this.increment = increment;
    }

    public void setVal(double val){
        this.val = clamp(val, minVal, maxVal);
    }

    private double clamp(double val, double minVal, double maxVal){
        return Math.min(maxVal, Math.max(minVal, val));
    }
}
