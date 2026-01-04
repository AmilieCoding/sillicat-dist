package client.sillicat.event.impl;

import client.sillicat.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.ScaledResolution;

@Getter
@AllArgsConstructor
public class Event2D extends Event {
    private final ScaledResolution sr;
}
