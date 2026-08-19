# FishyFiree Mod

A Minecraft Forge client-side HUD mod that counts inventory items and tracks nearby players by team color — with compass-like directional arrows.

**Minecraft 1.8.9 | Forge 11.15.1.2318**

---

## Features

### 🔲 Counter HUD

Displays the total count of **building blocks** and **arrows** in your inventory in real time.

- Color-coded count thresholds (green → yellow → orange → red)
- `/counter toggle` — show/hide the HUD
- `/counter settings` — configure thresholds, toggle sound, chat message, background
- Drag to reposition the HUD in-place

### 📍 Player Tracker HUD

Tracks the **nearest player of each team color** (red / green / blue / yellow) and shows:

- Player name (in team color)
- Horizontal distance
- Vertical height difference (`+` above you, `-` below you)
- **Directional arrow** — an equilateral-triangle V-shape that rotates freely to point toward the tracked player, relative to where you're looking

- `/playertracker toggle` — show/hide the tracker
- `/playertracker settings` — choose team color variants, toggle background, toggle arrows
- Drag to reposition the HUD in-place

### ⌨️ Keybinds

Both HUDs have toggle keybinds in Minecraft's controls menu under **"FishyFiree Mod"** category. Default: unbound.

---

## Installation

1. Install **Minecraft Forge 1.8.9** (11.15.1.2318+)
2. Drop the built `.jar` file into your `.minecraft/mods/` folder
3. Launch Minecraft with the Forge profile

---

## Configuration

All settings persist in `config/fishyfireemod.cfg` and can be modified either through the in-game GUI (`/counter settings` or `/playertracker settings`) or by editing the file directly.

---

## Building from source

```bash
git clone https://github.com/FishyFiree/FishyFireeMod.git
cd FishyFireeMod
./gradlew build
```

Output jar will be in `build/libs/`.

---

## License

This project is licensed under the same terms as Minecraft Forge.
