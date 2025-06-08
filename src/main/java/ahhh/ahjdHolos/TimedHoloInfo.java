package ahhh.ahjdHolos;

import org.bukkit.Location;
import java.util.UUID;

/**
 * Stores info about a timed hologram for persistence.
 */
public class TimedHoloInfo {
    public UUID uuid;
    public Location location;
    public String world;
    public double x, y, z;
    public String text;
    public long expireAt; // epoch millis

    public TimedHoloInfo(UUID uuid, Location loc, String text, long expireAt) {
        this.uuid = uuid;
        this.location = loc;
        this.world = loc.getWorld().getName();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.text = text;
        this.expireAt = expireAt;
    }

    public Location toLocation() {
        return this.location;
    }
}
