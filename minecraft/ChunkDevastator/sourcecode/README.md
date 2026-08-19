# ChunkDevastator

> **A brutal-survival Fabric mod for Minecraft 26.2 ("Chaos Cubed")**
>
> Take any damage, and the chunk you stand on gets a 5-second countdown — then the whole 16×16 column
> is deleted, leaving a hole into the void. Only end portal frames survive.

Every hit costs you a chunk. Run, heal up, or lose your base.

## Features

- ⚠️ **Any damage triggers it** — mobs, fall, lava, explosions, PvP, poison… anything (except void damage and `/kill`; creative/spectator players are exempt)
- 🕐 **5-second warning** — a light box outline + translucent fill around the chunk edges in the world, plus a HUD countdown per doomed chunk; the color animates **yellow → red**, one color per second
- 💥 **Full column deletion** — every block from the world floor to the sky, **including bedrock and chest contents**
- 🌀 **Survivor structure** — `minecraft:end_portal_frame` and the portal block itself are preserved
- 👥 **Multiplayer-ready** — the warning is visible to ALL players; countdowns continue even if the player dies, logs out, or the server restarts (state is persisted to disk)

## How it works

1. A survival/adventure player takes damage → their current chunk becomes **doomed**
2. The server broadcasts the doom to every client → warning box + countdown appear
3. After **5.0 seconds** the server voids the entire chunk column (bulk `setBlockState`, light/heightmap-safe) and syncs it to all players

Repeated hits in the same chunk do **not** reset the timer; hits in different chunks spawn **parallel countdowns**.

## Play rules (v1.0 — fixed, not configurable)

| Rule | Value |
|---|---|
| Trigger | any damage to survival/adventure players — except void damage and `/kill` |
| Exempt | creative / spectator; all difficulties, all dimensions apply |
| Countdown | 5.0 s (100 ticks), NOT reset by repeat hits in the same chunk |
| Parallel dooms | each distinct chunk hit gets its own countdown (no cap in v1.0) |
| Survival | doom fires even if the player dies / logs out / the server restarts (state is persisted) |
| Clearing | whole column incl. bedrock + block entities (inventories destroyed); entities untouched (fall) |
| Survivors | `minecraft:end_portal_frame` and `minecraft:end_portal` only |
| Warning | box outline + translucent fill (world) + HUD countdown number, seen by ALL players; re-synced on join |

## Requirements

- Minecraft **26.2** with **Fabric Loader >= 0.19.3** and **Fabric API 0.158.0+26.2**
- **Java 25** to build

## Install (players)

1. Download the latest jar from [Releases](https://github.com/FishyFiree/ChunkDevastator/releases)
2. Drop it into your instance's `mods/` folder
3. Launch — nothing else to configure

## Build from source (developers)

```bash
./gradlew build          # produces build/libs/chunkdevastator-<version>.jar
./gradlew runClient      # launch a dev instance to test
```

The Gradle wrapper (including its binary jar) is committed, so no separate Gradle install is needed.

## Project layout

```
src/main/java/com/chunkdevastator/    server-side: DoomManager, persistence, chunk nuker, networking
src/client/java/com/chunkdevastator/  client-side: doom registry, warning-box renderer (RenderPipeline), HUD countdown
src/main/resources/fabric.mod.json    mod metadata (id, entrypoints, dependencies)
```

## License

MIT © [FishyFiree](https://github.com/FishyFiree) — see [LICENSE](LICENSE).

## Disclaimer

This is an experimental survival-challenge mod. The client warning box uses Minecraft 26.2's new
render pipeline API; if you hit a crash while a doom is active, please open an
[issue](https://github.com/FishyFiree/ChunkDevastator/issues) with the crash report.