# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

TD2Core is a Spigot 1.12.2 Minecraft plugin for a parkour server ("TD2"). It manages parkour maps across 10 sections, player progression, checkpoints, leaderboards, Discord integration, and player state.

## Build

```bash
mvn clean package
```

The shaded JAR is output directly to `server/plugins/` for local testing. Java 8 target.

There are no unit tests.

## Key Dependencies

- **Spigot 1.12.2** (provided) - server API
- **Lombok** (provided) - `@Getter`, `@Setter`, `@RequiredArgsConstructor` used heavily
- **InventoryGUI** (de.themoep, shaded) - GUI framework for in-game menus
- **ProtocolLib** (provided) - packet manipulation for player tags
- **ViaVersion** (provided, soft-depend) - multi-version client support
- **JDA 5.0.0-beta.13** (shaded) - Discord bot integration
- **MySQL Connector 8.0.33** (shaded) - database driver

## Architecture

### Plugin Lifecycle (`TD2Core.java`)

Singleton pattern via `TD2Core.getInstance()`. All managers are instantiated in `onLoad()`, events/commands registered in `onEnable()`. Database access is via the static `TD2Core.sql()` helper.

### Manager Pattern

The plugin is organized around managers that are created in `TD2Core.onLoad()` and wired together via constructor injection:

- **PlayerManager** - tracks `ParkourPlayer` instances, handles join/leave, state transitions
- **MapManager** - loads map definitions from `td2_map.yml`, map metadata
- **SessionManager** - active parkour sessions per player
- **BlockManager** (also a Listener) - checkpoint/pressure plate mechanics, potion effects, timed plates
- **KitManager** - per-state inventory kits (Lobby, Parkour, Practice, Staff, Plot)
- **HideManager** (also a Listener) - player visibility toggling
- **DiscordManager** - JDA bot, scheduled leaderboard/progress updates
- **VerifyManager** - Discord account verification
- **ConfigManager** - loads 7 config files, accessed via `getConfig(SomeConfig.class)`

### Player State Machine

`PlayerState` enum: `LOBBY`, `PARKOUR`, `PRACTICE`, `STAFF`, `PLOT`, `TUTORIAL`. State transitions happen through `ParkourPlayer` and trigger kit changes, teleports, and permission updates.

### Database

All DB operations go through `AsyncMySQL` which wraps a `ThreadPoolExecutor`. Queries use prepared statements. Tables: `blockdata`, `collectedcp`, `maps`, `discorddata`, `playerlog`. Config in `td2_db.yml`.

### Event Listeners

- **GeneralListener** - join/quit, chat, damage, food, inventory
- **ParkourListener** - pressure plates, buttons, trapdoors, item interactions (the core parkour mechanics)
- **BlockManager** - block break events, checkpoint editing
- **HideManager** - player show/hide events

### Discord Progress System

`discord/progress/` contains per-section progress tracking classes (section1-10 + overall). Each extends `ProgressMap` and defines checkpoint ranges for individual maps within that section. The `DiscordManager` runs scheduled tasks to update progress channels.

### Config System

`ConfigAccessor` is the base class. Concrete configs: `DBConfig`, `ServerConfig`, `MapConfig`, `PlayerConfig`, `DiscordConfig`, `AnnouncementConfig`, `PlayerInventoryConfig`. All loaded through `ConfigManager` and accessed by class type.

### GUI System

GUIs use the `inventorygui` library. Key GUIs: `SectionGUI` (map section picker), `CheckPointGUI` (checkpoint selector), `GUILeaderboard`/`GlobalLBGUI`/`MapLBGUI` (leaderboard display), `GUIScrollable`/`GUIPane` (scrollable lists).

## Package Layout

```
de.legoshi.td2core
├── block/        # Checkpoint/pressure plate data and management
├── cache/        # Scheduled leaderboard caching (GlobalLBCache, MapLBCache)
├── command/      # 25+ commands (+ hide/ subpackage)
├── config/       # 7 config accessors + ConfigManager
├── database/     # AsyncMySQL, DBManager
├── discord/      # JDA bot, role management, progress/ subpackages (section1-10, overall)
├── gui/          # In-game inventory GUIs
├── kit/          # Per-state player kits
├── listener/     # GeneralListener, ParkourListener, item/ subpackage
├── map/          # ParkourMap, MapManager, session/ (SessionManager, ParkourSession)
├── permission/   # PermissionManager
├── player/       # ParkourPlayer, PlayerManager, PlayerState, hide/, tag/
└── util/         # Message, ScoreboardUtil, ItemUtils, WorldLoader, etc.
```

## Conventions

- Commands are registered in `TD2Core.registerCommands()` and declared in `plugin.yml`.
- All commands implement `CommandExecutor`. No tab completion.
- Permissions are prefixed with `td2core.` (e.g., `td2core.staff`, `td2core.build`).
- Database calls must be async (use `TD2Core.sql()` with the async query methods).
- Player version checks use ViaVersion API (`Via.getAPI().getPlayerVersion()`), checking for 1.13+ (protocol >= 393).
- The local test server in `server/` is gitignored.