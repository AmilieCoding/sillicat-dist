package sillicat.module;

import lombok.Getter;
import lombok.Setter;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import me.zero.alpine.listener.Subscriber;
import net.minecraft.client.gui.ScaledResolution;
import org.apache.commons.lang3.Validate;
import org.apache.http.client.UserTokenHandler;
import sillicat.Sillicat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import sillicat.event.impl.Event2D;
import sillicat.event.impl.EventKey;
import sillicat.event.impl.EventUpdate;
import sillicat.notification.NotificationManager;
import sillicat.setting.Setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public abstract class Module implements Subscriber {
    // Setting actual parameters for the modules.
    private final String name, description;
    private final Category category;
    private final boolean enabledByDefault;
    private final List<Setting> settingList = new ArrayList<>();
    private boolean toggled;

    @Setter
    private int key;

    protected final Minecraft mc = Sillicat.INSTANCE.getMc();
    protected final FontRenderer fr = Sillicat.INSTANCE.getFr();

    // Setting those from moduleinfo.
    public Module(){
        ModuleInfo info = getClass().getAnnotation(ModuleInfo.class);
        Validate.notNull(info, "Annotation? Where? (Confused annotation exception.)");

        this.name = info.name();
        this.description = info.description();
        this.category = info.category();
        this.enabledByDefault = info.enabled();

        if(enabledByDefault) toggle();
    }

    // Setting enabled, subscribe to event listeners.
    public void toggle(){
        setEnabled(!toggled);
    }

    public void setEnabled(boolean state){
        if(this.toggled == state) return;
        this.toggled = state;

        onToggle();

        if(state){
            Sillicat.BUS.subscribe(this);
            Sillicat.BUS.subscribe(eventUpdateListener);
            Sillicat.BUS.subscribe(event2DListener);
            Sillicat.BUS.subscribe(eventKeyListener);
            onEnable();
        }else{
            Sillicat.BUS.unsubscribe(this);
            Sillicat.BUS.unsubscribe(eventUpdateListener);
            Sillicat.BUS.unsubscribe(event2DListener);
            Sillicat.BUS.unsubscribe(eventKeyListener);
            onDisable();
        }
    }

    protected void addSettings(Setting... settings){
        settingList.addAll(Arrays.asList(settings));
    }

    public void onEnable(){}
    public void onDisable(){}
    public void onUpdate(){}
    public void on2D(ScaledResolution sr){}
    public void onKey(int key){}

    public void onToggle(){
        if(mc.theWorld != null){
            if(!this.getName().equalsIgnoreCase("clickgui")){
                NotificationManager.addNotification(this.getName(), toggled);
            }
        }
    }

    // Actual event calling now.
    @Subscribe
    private final Listener<EventUpdate> eventUpdateListener = new Listener<>(e ->{
        if(toggled) onUpdate();
    });

    @Subscribe
    private final Listener<Event2D> event2DListener = new Listener<>(e ->{
        if(toggled) on2D(e.getSr());
    });

    @Subscribe
    private final Listener<EventKey> eventKeyListener = new Listener<>(e ->{
        if(toggled) onKey(e.getKey());
    });
}
