package client.sillicat.module.impl.render;

import client.sillicat.module.Category;
import client.sillicat.module.Module;
import client.sillicat.module.ModuleInfo;
import org.lwjgl.input.Keyboard;

@ModuleInfo(
        name = "Fullbright",
        description = "See in the dark",
        category = Category.RENDER,
        defaultKey = Keyboard.KEY_G,
        enabled = false
)

public class Fullbright extends Module {
    @Override
    public void onEnable(){
        mc.gameSettings.gammaSetting = 100f;

        super.onEnable();
    }

    public void onDisable() {
        mc.gameSettings.gammaSetting = 1f;

        super.onDisable();
    }
}
