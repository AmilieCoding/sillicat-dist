package sillicat.notification;

import java.util.ArrayList;
import java.util.List;

import sillicat.Sillicat;
import sillicat.module.impl.settings.Notifications;

public class NotificationManager {
    private static final List<Notification> notificiations = new ArrayList<>();

    public static void addNotification(String moduleName, boolean enabled){
        notificiations.add(new Notification(moduleName, enabled));
    }

    public static void renderNotifications(){
        Notifications nm = (Notifications) Sillicat.INSTANCE.getModuleManager().getModule(Notifications.class);

        int notifH = nm != null ? (int) nm.height.getVal() : 30;
        int gap    = nm != null ? (int) nm.gap.getVal()    : 2;

        int yOffset = 0;
        for(int i = 0; i < notificiations.size(); i++){
            Notification notification = notificiations.get(i);
            notification.draw(yOffset);

            yOffset += notifH + gap;

            if(notification.shouldRemove()){
                notificiations.remove(i);
                i--;
            }
        }
    }
}
