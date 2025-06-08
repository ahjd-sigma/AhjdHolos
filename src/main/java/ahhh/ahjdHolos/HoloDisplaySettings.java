package ahhh.ahjdHolos;

import org.bukkit.Color;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.bukkit.util.Transformation;

/**
 * Encapsulates all customizable settings for a hologram TextDisplay.
 */
public class HoloDisplaySettings {
    // Font
    public String font = "minecraft:default";

    // Text display settings
    public boolean shadowed = false;
    public boolean seeThrough = true;
    public int textOpacity = -1; // -1 = default
    public float textScale = 1.0f;
    public int lineWidth = 200;
    public TextAlignment alignment = TextAlignment.CENTER;

    // Background
    public boolean hasBackground = false;
    public Color backgroundColor = Color.fromARGB(0, 0, 0, 0); // Transparent

    // Billboard/rotation
    public Billboard billboard = Billboard.CENTER; // Default: follows player
    public float yaw = 0f;
    public float pitch = 0f;
    public boolean animated = false;
    public long animationSpeed = 20L;

    // Quality of life: builder
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final HoloDisplaySettings s = new HoloDisplaySettings();
        public Builder font(String font) { s.font = font; return this; }
        public Builder shadowed(boolean b) { s.shadowed = b; return this; }
        public Builder seeThrough(boolean b) { s.seeThrough = b; return this; }
        public Builder textOpacity(int o) { s.textOpacity = o; return this; }
        public Builder textScale(float f) { s.textScale = f; return this; }
        public Builder lineWidth(int w) { s.lineWidth = w; return this; }
        public Builder alignment(TextAlignment a) { s.alignment = a; return this; }
        public Builder hasBackground(boolean b) { s.hasBackground = b; return this; }
        public Builder backgroundColor(Color c) { s.backgroundColor = c; return this; }
        public Builder billboard(Billboard b) { s.billboard = b; return this; }
        public Builder yaw(float y) { s.yaw = y; return this; }
        public Builder pitch(float p) { s.pitch = p; return this; }
        public Builder animated(boolean b) { s.animated = b; return this; }
        public Builder animationSpeed(long s_) { s.animationSpeed = s_; return this; }
        public HoloDisplaySettings build() { return s; }
    }

    // Apply settings to TextDisplay
    public void apply(TextDisplay display) {
        display.setShadowed(shadowed);
        display.setSeeThrough(seeThrough);
        if (textOpacity >= 0 && textOpacity <= 255) display.setTextOpacity((byte)textOpacity);
        // display.setTextScale(textScale); // Not supported in Spigot 1.21.4+
        display.setLineWidth(lineWidth); // Supported
        display.setAlignment(alignment);
        if (hasBackground) display.setBackgroundColor(backgroundColor);
        display.setBillboard(billboard);
        // Font, rotation, animation handled in spawn logic
        // (Font API is limited in Spigot, but can be set via display.setFont if supported)
    }
}
