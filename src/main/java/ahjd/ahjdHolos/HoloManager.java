package ahjd.ahjdHolos;

import org.bukkit.Location;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;
import java.io.File;
import java.io.IOException;
import java.util.*;
import org.bukkit.scheduler.BukkitRunnable; // For possible future expansion

/**
 * Internal manager for tracking and cleaning up holograms.
 */
class HoloManager {
    private static HoloManager instance;
    private final Map<Plugin, Set<UUID>> pluginHolograms = new HashMap<>();
    private final Map<UUID, TextDisplay> activeHolograms = new HashMap<>();
    private final Map<String, UUID> persistentIds = new HashMap<>(); // uniqueId -> entity UUID
    private final Map<UUID, BukkitTask> tempTasks = new HashMap<>(); // for temporary holograms
    private File persistentFile;
    private YamlConfiguration persistentConfig;
    private File timedFile;
    private YamlConfiguration timedConfig;
    private final Map<UUID, TimedHoloInfo> timedInfos = new HashMap<>(); // uuid -> info
    private HoloManager() {
        persistentFile = new File(Bukkit.getPluginManager().getPlugin("AhjdHolos").getDataFolder(), "persistent_holograms.yml");
        if (!persistentFile.exists()) try { persistentFile.createNewFile(); } catch (IOException ignored) {}
        persistentConfig = YamlConfiguration.loadConfiguration(persistentFile);
        timedFile = new File(Bukkit.getPluginManager().getPlugin("AhjdHolos").getDataFolder(), "timed_holograms.yml");
        if (!timedFile.exists()) try { timedFile.createNewFile(); } catch (IOException ignored) {}
        timedConfig = YamlConfiguration.loadConfiguration(timedFile);
    }

    public static HoloManager getInstance() {
        if (instance == null) instance = new HoloManager();
        return instance;
    }

    public TextDisplay spawnHologram(Plugin plugin, Location location, String text, HoloType type, int durationSeconds, String uniqueId) {
        TextDisplay display = location.getWorld().spawn(location, TextDisplay.class, e -> {
            e.setText(text);
            e.setBillboard(org.bukkit.entity.Display.Billboard.FIXED);
            e.setSeeThrough(true);
            e.setPersistent(type == HoloType.PERSISTENT);
        });
        activeHolograms.put(display.getUniqueId(), display);
        pluginHolograms.computeIfAbsent(plugin, k -> new HashSet<>()).add(display.getUniqueId());
        if (type == HoloType.TEMPORARY && durationSeconds > 0) {
            long expireAt = System.currentTimeMillis() + durationSeconds * 1000L;
            TimedHoloInfo info = new TimedHoloInfo(display.getUniqueId(), location, text, expireAt);
            timedInfos.put(display.getUniqueId(), info);
            saveTimedHologram(info);
            BukkitTask task = Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("AhjdHolos"), () -> {
                removeHologram(display.getUniqueId());
            }, durationSeconds * 20L);
            tempTasks.put(display.getUniqueId(), task);
        } else if (type == HoloType.PERSISTENT) {
            String id = (uniqueId == null || uniqueId.isEmpty()) ? UUID.randomUUID().toString() : uniqueId;
            persistentIds.put(id, display.getUniqueId());
            savePersistentHologram(id, location, text);
        }
        return display;
    }

    private void savePersistentHologram(String id, Location loc, String text) {
        persistentConfig.set(id + ".world", loc.getWorld().getName());
        persistentConfig.set(id + ".x", loc.getX());
        persistentConfig.set(id + ".y", loc.getY());
        persistentConfig.set(id + ".z", loc.getZ());
        persistentConfig.set(id + ".text", text);
        try { persistentConfig.save(persistentFile); } catch (IOException ignored) {}
    }

    public void loadPersistentHolograms(Plugin plugin) {
        // Persistent
        for (String id : persistentConfig.getKeys(false)) {
            String world = persistentConfig.getString(id + ".world");
            double x = persistentConfig.getDouble(id + ".x");
            double y = persistentConfig.getDouble(id + ".y");
            double z = persistentConfig.getDouble(id + ".z");
            String text = persistentConfig.getString(id + ".text");
            Location loc = new Location(Bukkit.getWorld(world), x, y, z);
            spawnHologram(plugin, loc, text, HoloType.PERSISTENT, 0, id);
        }
        // Timed
        List<String> toRemove = new ArrayList<>();
        for (String uuidStr : timedConfig.getKeys(false)) {
            long expireAt = timedConfig.getLong(uuidStr + ".expireAt");
            String world = timedConfig.getString(uuidStr + ".world");
            double x = timedConfig.getDouble(uuidStr + ".x");
            double y = timedConfig.getDouble(uuidStr + ".y");
            double z = timedConfig.getDouble(uuidStr + ".z");
            String text = timedConfig.getString(uuidStr + ".text");
            UUID uuid = UUID.fromString(uuidStr);
            Location loc = new Location(Bukkit.getWorld(world), x, y, z);
            long now = System.currentTimeMillis();
            if (expireAt <= now) {
                // Expired, try to remove if entity exists
                for (org.bukkit.entity.Entity ent : loc.getWorld().getEntitiesByClass(TextDisplay.class)) {
                    if (ent.getUniqueId().equals(uuid)) ent.remove();
                }
                toRemove.add(uuidStr);
            } else {
                // Still valid, respawn and schedule removal
                TextDisplay display = spawnHologram(plugin, loc, text, HoloType.TEMPORARY, (int)((expireAt-now)/1000), null);
                timedInfos.put(display.getUniqueId(), new TimedHoloInfo(display.getUniqueId(), loc, text, expireAt));
            }
        }
        // Remove expired from file
        for (String uuidStr : toRemove) {
            timedConfig.set(uuidStr, null);
        }
        try { timedConfig.save(timedFile); } catch (IOException ignored) {}
    }

    public boolean removeHologram(UUID uuid) {
        TextDisplay display = activeHolograms.remove(uuid);
        if (display != null) {
            display.remove();
            pluginHolograms.values().forEach(set -> set.remove(uuid));
            tempTasks.computeIfPresent(uuid, (k, t) -> { t.cancel(); return null; });
            // Remove from persistent
            String removeId = null;
            for (Map.Entry<String, UUID> e : persistentIds.entrySet()) {
                if (e.getValue().equals(uuid)) {
                    removeId = e.getKey();
                    break;
                }
            }
            if (removeId != null) {
                persistentIds.remove(removeId);
                persistentConfig.set(removeId, null);
                try { persistentConfig.save(persistentFile); } catch (IOException ignored) {}
            }
            // Remove from timed
            TimedHoloInfo info = timedInfos.remove(uuid);
            if (info != null) {
                timedConfig.set(uuid.toString(), null);
                try { timedConfig.save(timedFile); } catch (IOException ignored) {}
            }
            return true;
        }
        return false;
    }

    public void removeAllHolograms(Plugin plugin) {
        Set<UUID> set = pluginHolograms.remove(plugin);
        if (set != null) {
            for (UUID uuid : set) {
                TextDisplay display = activeHolograms.remove(uuid);
                if (display != null) display.remove();
            }
        }
    }

    public void removeAll() {
        for (TextDisplay display : activeHolograms.values()) {
            display.remove();
        }
        activeHolograms.clear();
        pluginHolograms.clear();
        tempTasks.values().forEach(BukkitTask::cancel);
        tempTasks.clear();
        persistentIds.clear();
        timedInfos.clear();
        // Do not clear YAML on shutdown, only on explicit removal
    }

    private void saveTimedHologram(TimedHoloInfo info) {
        String key = info.uuid.toString();
        timedConfig.set(key + ".world", info.world);
        timedConfig.set(key + ".x", info.x);
        timedConfig.set(key + ".y", info.y);
        timedConfig.set(key + ".z", info.z);
        timedConfig.set(key + ".text", info.text);
        timedConfig.set(key + ".expireAt", info.expireAt);
        try { timedConfig.save(timedFile); } catch (IOException ignored) {}
    }
}
