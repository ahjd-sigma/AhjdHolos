package ahhh.ahjdHolos;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.TextDisplay;
import java.util.UUID;

/**
 * Public API for creating and managing holograms (TextDisplay entities).
 * Use static methods to spawn and remove holograms.
 */
public class HoloAPI {
    /**
     * Spawns a hologram at the given location with the given text.
     * @param plugin The plugin requesting the hologram (for tracking/cleanup)
     * @param location The location to spawn the hologram
     * @param text The text to display
     * @return The spawned TextDisplay entity
     */
    /**
     * Spawns a hologram with type and duration (for temporary).
     * @param plugin The plugin requesting the hologram
     * @param location The location to spawn
     * @param text The text to display
     * @param type TEMPORARY or PERSISTENT
     * @param durationSeconds For TEMPORARY, the lifetime in seconds. Ignored for PERSISTENT.
     * @param uniqueId For PERSISTENT, a unique string ID for this hologram. Null or empty for auto.
     * @return The spawned TextDisplay entity
     */
    public static TextDisplay spawnHologram(Plugin plugin, Location location, String text, HoloType type, int durationSeconds, String uniqueId) {
        return HoloManager.getInstance().spawnHologram(plugin, location, text, type, durationSeconds, uniqueId);
    }

    /**
     * Backwards-compatible spawn: permanent, not saved.
     */
    public static TextDisplay spawnHologram(Plugin plugin, Location location, String text) {
        return HoloManager.getInstance().spawnHologram(plugin, location, text, HoloType.TEMPORARY, 0, null);
    }

    /**
     * Removes a hologram by its entity UUID.
     * @param uuid The UUID of the TextDisplay entity
     * @return true if removed, false otherwise
     */
    public static boolean removeHologram(UUID uuid) {
        return HoloManager.getInstance().removeHologram(uuid);
    }

    /**
     * Removes all holograms spawned by a specific plugin.
     * @param plugin The plugin whose holograms to remove
     */
    public static void removeAllHolograms(Plugin plugin) {
        HoloManager.getInstance().removeAllHolograms(plugin);
    }
}
