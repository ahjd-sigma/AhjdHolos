package ahjd.ahjdHolos;

import org.bukkit.plugin.java.JavaPlugin;

public final class AhjdHolos extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig(); // Loads config.yml if not present
        HoloManager.getInstance().loadPersistentHolograms(this);
        getLogger().info("AhjdHolos enabled. Public API ready.");
    }

    @Override
    public void onDisable() {
        // Cleanup all holograms
        HoloManager.getInstance().removeAll();
        getLogger().info("AhjdHolos disabled. All holograms cleaned up.");
    }
}
