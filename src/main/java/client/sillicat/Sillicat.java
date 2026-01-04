package client.sillicat;

import client.sillicat.event.impl.EventKey;
import client.sillicat.module.ModuleManager;
import client.sillicat.utils.logger.Logger;
import me.zero.alpine.bus.EventBus;
import me.zero.alpine.bus.EventManager;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import me.zero.alpine.listener.Subscriber;

import client.sillicat.module.Module;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

public enum Sillicat implements Subscriber {
    INSTANCE;

    // clientVerID - Used internally to identify if client is out of date.
    // Latest client version may use any variety of strings or whatever.
    private final String clientName = "Sillicat";
    private final String clientVerID = "00001";

    // Primary mistake last time was making this private. It got so spaghetti code at points.
    public final Minecraft mc = Minecraft.getMinecraft();

    private ModuleManager moduleManager;

    public static final EventBus BUS = EventManager.builder()
            .setName("root/sillicat")
            .setSuperListeners()
            .build();

    // See Minecraft.java for more on this function.
    public void init(){
        BUS.subscribe(this);
        Logger.log("Startup registered.");

        Display.setTitle(clientName);
        moduleManager = new ModuleManager();
    }

    public void shutdown(){
        BUS.unsubscribe(this);
        Logger.log("Shutdown registered.");
    }

    public Minecraft mc() {
        final Minecraft mc = Minecraft.getMinecraft();
        return mc;
    }

    @Subscribe
    public final Listener<EventKey> eventKeyListener = new Listener<>(e ->{
        if(moduleManager != null){
            moduleManager.getModules().values().forEach(module -> {
                if(module.getKey() == e.getKey()){
                    Logger.log("Key event: " + e.getKey());
                    module.toggle();
                }
            });
        }
    });
}
