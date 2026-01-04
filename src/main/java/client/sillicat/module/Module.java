package client.sillicat.module;

import client.sillicat.Sillicat;
import client.sillicat.event.impl.Event2D;
import client.sillicat.event.impl.EventKey;
import client.sillicat.event.impl.EventRotate;
import client.sillicat.event.impl.EventUpdate;
import client.sillicat.event.impl.movement.EventMotionPost;
import client.sillicat.event.impl.movement.EventMotionPre;
import client.sillicat.setting.Setting;
import lombok.Getter;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import me.zero.alpine.listener.Subscriber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public abstract class Module implements Subscriber {
    private final String name, description;
    private final Category category;
    private final boolean enabledByDefault;
    private final List<Setting> settingList = new ArrayList<>();
    private boolean toggled;
    private final int defaultKey;
    @Getter
    private int key;

    // Import Minecraft from Sillicat - instead of spaghetti code :).
    protected final Minecraft mc = Sillicat.INSTANCE.mc();

    public Module(){
        ModuleInfo info = getClass().getAnnotation(ModuleInfo.class);
        Validate.notNull(info, "Invalid annotation exception.");

        this.name = info.name();
        this.description = info.description();
        this.category = info.category();
        this.enabledByDefault = info.enabled();
        this.defaultKey = info.defaultKey();
        this.key = this.defaultKey;

        if(enabledByDefault) toggle();
    }

    public void toggle(){
        setEnabled(!toggled);
    }

    public void setEnabled(boolean state) {
        if (this.toggled == state) return;
        this.toggled = state;
        onToggle();

        if (state) {
            Sillicat.BUS.subscribe(this);
            onEnable();
        } else {
            Sillicat.BUS.unsubscribe(this);
            onDisable();
        }
    }

    // Adding settings for use in the GUI.
    protected void addSettings(Setting... settings){
        settingList.addAll(Arrays.asList(settings));
    }

    public void onEnable(){}
    public void onDisable(){}
    public void onUpdate(){}
    public void on2D(ScaledResolution sr){}
    public void onKey(int key){}
    public void onRotate(){}
    public void onMotionPre(){}
    public void onMotionPost(){}

    public void onToggle(){}

//    @Subscribe
//    public final Listener<EventUpdate> eventUpdateListener = new Listener<>(e ->{
//        if(toggled) onUpdate();
//    });
//
//    @Subscribe
//    public final Listener<Event2D> event2DListener = new Listener<>(e ->{
//        if(toggled) on2D(e.getSr());
//    });
//
//    @Subscribe
//    public final Listener<EventKey> eventKeyListener = new Listener<>(e ->{
//        if(toggled) onKey(e.getKey());
//    });
//
//    public final Listener<EventRotate> eventRotateListener = new Listener<>(e ->{
//        if(toggled) onRotate();
//    });
//
//    public final Listener<EventMotionPre> eventMotionPreListener = new Listener<>(e ->{
//        if(toggled) onMotionPre();
//    });
//
//    public final Listener<EventMotionPost> eventMotionPostListener = new Listener<>(e ->{
//        if(toggled) onMotionPost();
//    });
}
