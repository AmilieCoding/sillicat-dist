package sillicat.event.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sillicat.event.Event;

@Getter
@AllArgsConstructor
public class EventKey extends Event {
    private final int key;
}
