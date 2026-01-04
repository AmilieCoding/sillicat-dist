package client.sillicat.event.impl.movement;

import client.sillicat.event.Event;

// Post packet send, used for COSMETIC adjustments only.
public class EventMotionPost extends Event {
    private final double x, y, z;
    private final float yaw, pitch;
    private final boolean onGround;

    public EventMotionPost(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }

    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }

    public boolean isOnGround() { return onGround; }
}
