package sillicat.util;

public class HoverUtil {
    public static boolean isHovered(int left, int top, int right, int bottom, int mouseX, int mouseY){
        return mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bottom;
    }

}
