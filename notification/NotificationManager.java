package sillicat.notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationManager {
    private static final List<Notification> notificiations = new ArrayList<>();

    public static void addNotification(String moduleName, boolean enabled){
        notificiations.add(new Notification(moduleName, enabled));
    }

    public static void renderNotifications(){
        int yOffset = 0;
        for(int i = 0; i < notificiations.size();  i++){
            Notification notification = notificiations.get(i);
            notification.draw(yOffset);
            yOffset += 30;

            if(notification.shouldRemove()){
                notificiations.remove(i);
                i--;
            }
        }
    }
}
