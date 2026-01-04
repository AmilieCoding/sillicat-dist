package client.sillicat.event.impl;

import client.sillicat.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventKey extends Event {
    private final int key;
}
