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
import sillicat.event.impl.EventKey;
import sillicat.module.ModuleManager;
import sillicat.ui.clickgui.ClickGUIScreen;

@Getter
public enum Sillicat implements Subscriber {

    // TODO:
    // https://www.youtube.com/watch?v=dLtap75ZfeI&list=PLa9z_3uMqXcWQui2tT9Qok8QzN7nX7OwK&index=5
    // SETTINGS & ARRAYLIST.

    INSTANCE;

    private final String name = "Sillicat";

    private final Minecraft mc = Minecraft.getMinecraft();
    private final FontRenderer fr = mc.fontRendererObj;

    private ModuleManager moduleManager;

    public static final EventBus BUS = EventManager.builder()
            .setName("root/sillicat")
            .setSuperListeners()
            .build();

    public void init(){
        BUS.subscribe(this);
        Display.setTitle(name);

        moduleManager = new ModuleManager();
    }

    public void shutdown(){
        BUS.unsubscribe(this);
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
}
