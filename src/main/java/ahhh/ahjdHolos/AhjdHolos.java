package ahhh.ahjdHolos;

import org.bukkit.plugin.java.JavaPlugin;

public final class AhjdHolos extends JavaPlugin {
    private static AhjdHolos instance;

    @Override
    public void onEnable() {
        instance = this;
        
        // Save default config if not exists
        saveDefaultConfig();
        
        // Initialize hologram manager
        HoloManager holoManager = HoloManager.getInstance();
        holoManager.loadPersistentHolograms(this);

        
        
        getLogger().info("AhjdHolos v" + getDescription().getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        // Cleanup all holograms
        HoloManager.getInstance().removeAll();
        getLogger().info("AhjdHolos has been disabled. All holograms cleaned up.");
    }
    
    public static AhjdHolos getInstance() {
        return instance;
    }
}
