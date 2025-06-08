# AhjdHolos Public API Documentation

A robust, crash-safe hologram API for Spigot 1.21.5+.

## Features
- **Public API** for other plugins to spawn and manage holographic text (TextDisplay entities)
- **Temporary Holograms:** Auto-despawn after a set time, crash-safe (no orphans)
- **Persistent Holograms:** Saved to disk, always restored after server/plugin restart
- **Automatic cleanup** of all holograms on plugin disable

---

## Adding AhjdHolos as a Dependency

### 1. Build and Install the Plugin Locally
If AhjdHolos is not published to a public Maven repository, you can use it as a local dependency:

#### **Maven**
- Build AhjdHolos (using `mvn package` or your IDE)
- Copy the resulting `AhjdHolos-x.x.x.jar` to your project's `libs` folder (create if it doesn't exist)
- Add to your `pom.xml`:

```xml
<dependency>
    <groupId>ahjd.ahjdHolos</groupId>
    <artifactId>AhjdHolos</artifactId>
    <version>1.0.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/libs/AhjdHolos-1.0.0.jar</systemPath>
</dependency>
```

#### **Gradle**
- Build AhjdHolos and place the jar in your `libs` folder
- Add to your `build.gradle`:

```groovy
dependencies {
    implementation files('libs/AhjdHolos-1.0.0.jar')
}
```

### 2. plugin.yml
Add this to your plugin's `plugin.yml` to ensure AhjdHolos loads before your plugin:

```yaml
depends:
  - AhjdHolos
```

---

## Usage
### Import
```java
import ahjd.ahjdHolos.HoloAPI;
import ahjd.ahjdHolos.HoloType;
```

### Spawning a Temporary (Timed) Hologram
```java
// Spawns a hologram that despawns after 10 seconds
TextDisplay holo = HoloAPI.spawnHologram(plugin, location, "Hello, world!", HoloType.TEMPORARY, 10, null);
```

### Spawning a Persistent Hologram
```java
// Spawns a hologram that persists across restarts (uniqueId can be any string, e.g. "spawn_welcome")
TextDisplay holo = HoloAPI.spawnHologram(plugin, location, "Welcome to spawn!", HoloType.PERSISTENT, 0, "spawn_welcome");
```

### Basic (Legacy) Spawn
```java
// Spawns a permanent hologram (not saved, not crash-safe)
TextDisplay holo = HoloAPI.spawnHologram(plugin, location, "Legacy style");
```

### Removing a Hologram
```java
// Remove by UUID
boolean removed = HoloAPI.removeHologram(holo.getUniqueId());
```

### Removing All Holograms for a Plugin
```java
// Remove all holograms spawned by your plugin
HoloAPI.removeAllHolograms(plugin);
```

---

## Crash Safety
- **Timed holograms** are tracked in `timed_holograms.yml`. If the server crashes, expired ones are removed and valid ones are restored on next startup.
- **Persistent holograms** are tracked in `persistent_holograms.yml` and always restored.
- **No orphans:** All holograms are cleaned up on plugin disable and startup reconciliation.

## File Locations
- `plugins/AhjdHolos/persistent_holograms.yml`: Persistent holograms
- `plugins/AhjdHolos/timed_holograms.yml`: Timed holograms

## API Method Reference
- `spawnHologram(plugin, location, text, type, durationSeconds, uniqueId)`
- `spawnHologram(plugin, location, text)`
- `removeHologram(uuid)`
- `removeAllHolograms(plugin)`

## Notes
- **Multi-line, per-player, and animated holograms** are not yet supported, but can be added in future updates.
- All API calls must be made from the main server thread.

---

## Example Plugin Usage
```java
import ahjd.ahjdHolos.HoloAPI;
import ahjd.ahjdHolos.HoloType;

public class ExamplePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        Location loc = new Location(Bukkit.getWorld("world"), 0, 80, 0);
        HoloAPI.spawnHologram(this, loc, "Temporary Holo", HoloType.TEMPORARY, 15, null);
        HoloAPI.spawnHologram(this, loc.add(0, 2, 0), "Persistent Holo", HoloType.PERSISTENT, 0, "unique_id");
    }
}
```

---

For more advanced features or questions, see the code or contact the author.
