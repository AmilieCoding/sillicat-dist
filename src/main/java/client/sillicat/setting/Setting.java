package client.sillicat.setting;

import lombok.Getter;
import java.util.function.BooleanSupplier;

@Getter
public class Setting {
    private final String name;
    public Setting(String name){
        this.name = name;
    }
    private BooleanSupplier visible = () -> true;

    public void setVisible(BooleanSupplier visible){
        this.visible = visible;
    }

    public boolean isVisible(){
        return visible.getAsBoolean();
    }
}
