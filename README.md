# AhjdHolos Public API Documentation
evil emoji plugin by chatgpt

## Features
- **Public API** for other plugins to spawn and manage holographic text (TextDisplay entities)
- **Temporary Holograms:** Auto-despawn after a set time, crash-safe (no orphans)
- **Persistent Holograms:** Saved to disk, always restored after server/plugin restart
- **Automatic cleanup** of all holograms on plugin disable

---
> **Tip:** You can use `main` for the latest commit, but using a tag like `0.1` is recommended for stability.


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
import ahhh.ahjdHolos.HoloAPI;
import ahhh.ahjdHolos.HoloType;
import ahhh.ahjdHolos.HoloDisplaySettings;
```

### Spawning a Temporary (Timed) Hologram
```java
TextDisplay holo = HoloAPI.spawnHologram(
    plugin,
    location,
    HoloAPI.colorize("{#FF00FF}Hello, world! {#00FFFF}\u2728"), // Hex color & icon support
    HoloDisplaySettings.builder()
        .font("minecraft:default")
        .shadowed(true)
        .seeThrough(true)
        .textOpacity(200) // 0-255
        .lineWidth(200)
        .alignment(TextAlignment.CENTER)
        .hasBackground(true)
        .backgroundColor(Color.fromARGB(128, 0, 0, 0)) // ARGB
        .billboard(Billboard.CENTER) // faces player like armorstand name
        .build(),
    HoloType.TEMPORARY,
    10, // seconds
    null
);
// Access the hologram's location from the TimedHoloInfo object
TimedHoloInfo timedHoloInfo = HoloAPI.getTimedHoloInfo(holo.getUniqueId());
Location holoLocation = timedHoloInfo.location;
```

### Spawning a Persistent Hologram
```java
TextDisplay holo = HoloAPI.spawnHologram(
    plugin,
    location,
    HoloAPI.colorize("{#00FF00}Welcome back!"),
    HoloDisplaySettings.builder()
        .shadowed(false)
        .hasBackground(false)
        .billboard(Billboard.CENTER)
        .build(),
    HoloType.PERSISTENT,
    0, // duration ignored for persistent
    "spawn_welcome" // unique id for persistence
);
```

### Features
- **Fonts:** Use any font key available to the client (resource pack required for custom fonts)
- **Shadow, seethrough, text opacity**
- **Line width, alignment**
- **Background (on/off), ARGB color**
- **Billboard:**
  - `Billboard.CENTER` (default): faces player like armorstand names
  - `Billboard.FIXED`: text stays static
- **Hex color:** `{#RRGGBB}` or `&#RRGGBB` in text
- **Icons:** Unicode icons supported if font/resource pack includes them
- **Builder pattern:** Easy, modern developer API

### Color Presets
For convenience, you can use these ARGB color presets (see `HoloColors.java`):
```java
public static final Color BLACK      = Color.fromARGB(255, 0, 0, 0);
public static final Color WHITE      = Color.fromARGB(255, 255, 255, 255);
public static final Color GRAY       = Color.fromARGB(255, 128, 128, 128);
public static final Color LIGHT_GRAY = Color.fromARGB(255, 211, 211, 211);
public static final Color DARK_GRAY  = Color.fromARGB(255, 64, 64, 64);
public static final Color RED        = Color.fromARGB(255, 255, 0, 0);
public static final Color DARK_RED   = Color.fromARGB(255, 139, 0, 0);
public static final Color ORANGE     = Color.fromARGB(255, 255, 140, 0);
public static final Color YELLOW     = Color.fromARGB(255, 255, 255, 0);
public static final Color GOLD       = Color.fromARGB(255, 255, 215, 0);
public static final Color BROWN      = Color.fromARGB(255, 150, 75, 0);
public static final Color TAN        = Color.fromARGB(255, 210, 180, 140);
public static final Color BEIGE      = Color.fromARGB(255, 245, 245, 220);
public static final Color OLIVE      = Color.fromARGB(255, 128, 128, 0);
public static final Color GREEN      = Color.fromARGB(255, 0, 255, 0);
public static final Color DARK_GREEN = Color.fromARGB(255, 0, 100, 0);
public static final Color LIME       = Color.fromARGB(255, 50, 205, 50);
public static final Color AQUA       = Color.fromARGB(255, 0, 255, 170);
public static final Color CYAN       = Color.fromARGB(255, 0, 255, 255);
public static final Color TEAL       = Color.fromARGB(255, 0, 128, 128);
public static final Color BLUE       = Color.fromARGB(255, 0, 0, 255);
public static final Color NAVY       = Color.fromARGB(255, 0, 0, 128);
public static final Color PURPLE     = Color.fromARGB(255, 128, 0, 128);
public static final Color INDIGO     = Color.fromARGB(255, 75, 0, 130);
public static final Color VIOLET     = Color.fromARGB(255, 238, 130, 238);
public static final Color MAGENTA    = Color.fromARGB(255, 255, 0, 255);
public static final Color PINK       = Color.fromARGB(255, 255, 105, 180);
```
Use them in your builder: `.backgroundColor(HoloColors.MAGENTA)`


### Hex Color & Icons
- Use `{#RRGGBB}` or `&#RRGGBB` in your text for hex colors.
- Unicode icons (e.g., `\u2728`) are supported if your font/resource pack allows.

### Removing All Holograms For Your Plugin
```java
// Remove all holograms spawned by your plugin
HoloManager.getInstance().removeAllHolograms(plugin);
```

### Troubleshooting
- **Dependency not found:**
  - Ensure your `plugin.yml` lists `AhjdHolos` under `depends:`.
  - If using Maven, check your `pom.xml` dependency coordinates match the installed version.
  - Reload/refresh your IDE and Maven project if symbols are not resolved.
- **Font not working:**
  - Custom fonts require the client to have a resource pack with the font key.
- **Text not facing player:**
  - Use `Billboard.CENTER` (default) for armorstand-style facing. Use `Billboard.FIXED` for static.

---

### Advanced Customization Example
```java
TextDisplay holo = HoloAPI.spawnHologram(
    plugin,
    location,
    HoloAPI.colorize("{#FF00FF}Fancy & Animated Holo! {#00FFFF}984"), // supports hex color and icons
    HoloDisplaySettings.builder()
        .font("minecraft:uniform")
        .shadowed(true)
        .seeThrough(false)
        .textOpacity(200)
        .textScale(1.5f)
        .lineWidth(300)
        .alignment(TextAlignment.LEFT)
        .hasBackground(true)
        .backgroundColor(Color.fromARGB(128, 0, 0, 0))
        .billboard(Billboard.CENTER)
        .animated(true)
        .animationSpeed(10L)
        .build(),
    HoloType.TEMPORARY,
    30, // seconds
    null
);
```

### Hex Color and Icons
- Use `{#RRGGBB}` or `&#RRGGBB` in your text for hex colors.
- Unicode icons are supported if your font/resource pack allows.

### All Features
- Font selection (default if not present)
- Shadow, see-through, text opacity, scale
- Line width, alignment
- Background (on/off), background color, transparency/opacity (ARGB)
- Text rotation/orientation, fixed/billboard/animated/following
- Hex color and icon support
- Builder pattern for easy developer usage

---

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
//HoloAPI.removeAllHolograms(plugin);
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