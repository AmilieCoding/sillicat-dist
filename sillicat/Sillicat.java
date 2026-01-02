// Much thanks to Aznos, and their YouTube series, for even making this possible.
// https://www.youtube.com/watch?v=DSdUYXldFAQ&list=PLa9z_3uMqXcWQui2tT9Qok8QzN7nX7OwK&index=1
// Check them out at the link above.
package sillicat;

import lombok.Getter;
// The Alpine event system will be utilised.
// https://github.com/ZeroMemes/Alpine
import me.zero.alpine.bus.EventBus;
import me.zero.alpine.bus.EventManager;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import me.zero.alpine.listener.Subscriber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import sillicat.config.ConfigManager;
import sillicat.event.impl.EventKey;
import sillicat.module.ModuleManager;
import sillicat.module.impl.render.Notifications;
import sillicat.ui.clickgui.ClickGUIScreen;
import sillicat.util.alts.AltManager;
import sillicat.util.font.FontManager;

@Getter
public enum Sillicat implements Subscriber {
    INSTANCE;

    private final String name = "Sillicat";

    private final Minecraft mc = Minecraft.getMinecraft();
    private final FontRenderer fr = mc.fontRendererObj;
    private FontManager fontManager;

    private ModuleManager moduleManager;
    private ConfigManager configManager;
    public AltManager altManager;

    public static final EventBus BUS = EventManager.builder()
            .setName("root/sillicat")
            .setSuperListeners()
            .build();

    public void init(){
        BUS.subscribe(this);

        fontManager = new FontManager();
        fontManager.preload();

        Display.setTitle(name);

        moduleManager = new ModuleManager();
        configManager = new ConfigManager();
        altManager = new AltManager();

        configManager.loadConfig();
    }

    public void shutdown(){
        BUS.unsubscribe(this);
        configManager.saveConfig();
    }

    @Subscribe
    private final Listener<EventKey> keyListener = new Listener<>(e ->{
        if(moduleManager != null){
            moduleManager.getModules().values().forEach(module -> {
                if(module.getKey() == e.getKey()){
                    module.toggle();
                }
            });
        }

        if(e.getKey() == Keyboard.KEY_DELETE || e.getKey() == Keyboard.KEY_RSHIFT){
            mc.displayGuiScreen(new ClickGUIScreen());
        }
    });

    public Notifications getNotificationsModule() {
        return (Notifications) Sillicat.INSTANCE.getModuleManager().getModule(Notifications.class);
    }


    public FontManager getFontManager(){
        return fontManager;
    }
}
