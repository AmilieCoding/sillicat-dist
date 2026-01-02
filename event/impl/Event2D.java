package sillicat.event.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.ScaledResolution;
import sillicat.event.Event;

@Getter
@AllArgsConstructor
public class Event2D extends Event {
    private final ScaledResolution sr;
}
