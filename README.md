# ThrowableTNT Plugin - Minecraft Grenade Plugin

A professional Minecraft plugin that allows players to throw TNT grenades with customizable power, distance, and effects. Perfect for PvP servers, creative servers, or survival gameplay with a twist.

## Quick Start - Test Server

Want to test the plugin locally? Use the setup script to download and run a Paper server:

1. **Run the setup script**:
   ```bash
   node index.js
   ```
   This will download Paper 1.21 and create start scripts.

2. **Build the plugin**:
   ```bash
   ./gradlew shadowJar
   ```
   (Windows: `gradlew shadowJar`)

3. **Copy the plugin**:
   ```bash
   mkdir -p plugins
   cp build/libs/ThrowableTNT-1.0.0.jar plugins/
   ```

4. **Start the server**:
   - Windows: `start.bat`
   - Mac/Linux: `./start.sh`

5. **Connect** to `localhost` in Minecraft 1.21

## Features

- **Throwable TNT Grenades**: Right-click to throw TNT that explodes on impact
- **Customizable Properties**: Each TNT item can have different power levels and throw distances
- **NBT-Based System**: Power and distance stored in item metadata, preventing cheating
- **Configurable Lore**: Server owners can customize TNT item descriptions
- **Sound & Particle Effects**: Smoke effects and explosion sounds
- **Cooldown System**: Prevent spam throwing with customizable cooldowns
- **Full Permission System**: Control who can throw, give, and reload configurations
- **In-Game Commands**: Easy item creation and management

## Installation

1. **Build the Plugin**:
   ```bash
   ./gradlew build
   ```
   The compiled JAR will be located at: `build/libs/ThrowableTNT-1.0.0.jar`

2. **Install on Server**:
   - Place the JAR file in your server's `plugins/` directory
   - Restart your server

3. **First Run**:
   - The plugin will automatically create `config.yml` in the `plugins/ThrowableTNT/` directory
   - Edit the configuration as needed
   - Run `/throwabletnt reload` to apply changes

## Commands

### Give TNT to Player
```
/throwabletnt give <player> <amount> [power] [distance]
```
- `player`: Target player's username
- `amount`: Number of TNT items to give
- `power`: Explosion power (default: 4.0)
- `distance`: Throw distance multiplier (default: 1.0)

**Example:**
```
/throwabletnt give Steve 10 5.0 1.5
```
Gives Steve 10 TNT grenades with power 5.0 and 1.5x distance multiplier.

### Reload Configuration
```
/throwabletnt reload
```
Reloads the configuration file without restarting the server.

### Show Plugin Info
```
/throwabletnt info
```
Displays current plugin settings and version.

### Reset Player Cooldown
```
/throwabletnt cooldown <player>
```
Resets the throw cooldown for a specific player.

### Show Help
```
/throwabletnt help
```
Displays all available commands.

## Permissions

| Permission | Description | Default |
|-----------|-------------|---------|
| `throwabletnt.throw` | Permission to throw TNT grenades | true (all players) |
| `throwabletnt.give` | Permission to use give command | op (operators only) |
| `throwabletnt.reload` | Permission to reload configuration | op (operators only) |
| `throwabletnt.cooldown.bypass` | Bypass cooldown restrictions | op (operators only) |
| `throwabletnt.command` | Access to any throwabletnt command | op (operators only) |

## Configuration Guide

Edit `plugins/ThrowableTNT/config.yml` to customize the plugin:

### TNT Item Settings
```yaml
tnt:
  item-name: "&c&lThrowable TNT"           # Display name in inventory
  main-lore:
    - "&7Power Level: &e%power%"           # Use %power% for dynamic values
    - "&7Distance: &e%distance%x"          # Use %distance% for dynamic values
    - "&8Grenades explode on impact"
  material: "TNT"
```

### Explosion Settings
```yaml
explosion:
  default-power: 4.0              # Default explosion power
  min-power: 1.0                  # Minimum allowed power
  max-power: 10.0                 # Maximum allowed power
  default-distance: 1.0           # Default throw distance multiplier
  min-distance: 0.5               # Minimum distance
  max-distance: 3.0               # Maximum distance
  damage-players: true            # Whether explosions damage players
  break-blocks: true              # Whether explosions break blocks
  radius-multiplier: 1.0          # Explosion radius multiplier
```

### Particle Effects
```yaml
particles:
  enabled: true
  smoke-type: "SMOKE"             # Particle type (SMOKE, CAMPFIRE_COSY_SMOKE, etc.)
  smoke-count: 30                 # Number of particles
  projectile-trail: true          # Particles following TNT while flying
  trail-type: "SMOKE"
  trail-count: 3                  # Particles per tick
```

### Sound Effects
```yaml
sounds:
  enabled: true
  throw-sound: "ENTITY_SNOWBALL_THROW"      # Sound when thrown
  throw-volume: 0.7                         # Volume (0.0-1.0)
  throw-pitch: 1.0                          # Pitch (0.5-2.0)
  explosion-sound: "ENTITY_GENERIC_EXPLODE" # Sound on explosion
  explosion-volume: 1.0
  explosion-pitch: 1.0
```

### Cooldown Settings
```yaml
cooldown:
  duration: 3                     # Cooldown in seconds
  show-action-bar: true           # Show remaining time in action bar
```

### Messages
All messages support color codes with `&` (e.g., `&c` for red, `&a` for green):
```yaml
messages:
  give-success: "&aGave &b%player% &ax%amount% &cThrowable TNT &a(Power: &e%power%&a, Distance: &e%distance%x&a)"
  cooldown-message: "&cWait %remaining% seconds before throwing another TNT"
  throw-success: "&aYou threw a TNT grenade!"
  # ... more messages
```

## How It Works

1. **Creating TNT**: Use `/throwabletnt give` to create TNT items with custom power/distance
2. **Throwing**: Right-click with TNT item to throw it as a grenade
3. **Impact**: TNT explodes on contact with blocks or entities
4. **Effects**: Smoke particles and explosion sound on impact
5. **Cooldown**: Players must wait configured cooldown before throwing again

### NBT Data Protection

Power and distance values are stored in the item's NBT data, making it impossible for players to modify via regular means. The custom lore displays these values for transparency.

## Customization Examples

### PvP-Focused Server
```yaml
explosion:
  default-power: 6.0
  max-power: 8.0
  damage-players: true
  break-blocks: false

cooldown:
  duration: 2
```

### Creative Server
```yaml
explosion:
  max-power: 15.0
  default-distance: 2.0
  max-distance: 5.0
  break-blocks: true

cooldown:
  duration: 0  # No cooldown
```

### Survival Server
```yaml
explosion:
  default-power: 3.0
  max-power: 5.0
  damage-players: true
  break-blocks: true

particles:
  smoke-count: 40
  enabled: true
```

## Sound and Particle Options

### Particle Types
Common values: `SMOKE`, `CAMPFIRE_COSY_SMOKE`, `LARGE_SMOKE`, `CLOUD`, `EXPLOSION`, `EXPLOSION_EMITTER`

### Sound Types
Common values: `ENTITY_SNOWBALL_THROW`, `ENTITY_GENERIC_EXPLODE`, `ENTITY_ENDER_PEARL_THROW`, `BLOCK_DISPENSER_DISPENSE`

## Troubleshooting

**Q: Plugin won't load**
- Check that you're using Java 17 or higher
- Verify the JAR is in the plugins folder
- Check server logs for error messages

**Q: TNT doesn't explode**
- Ensure player has `throwabletnt.throw` permission
- Check that cooldown has expired
- Verify the TNT item was created with `/throwabletnt give`

**Q: Sounds not playing**
- Verify sound names in config.yml are correct
- Check game volume settings
- Use `/throwabletnt info` to verify sounds are enabled

**Q: Explosions cause lag**
- Reduce `smoke-count` in particles section
- Decrease `default-power` to reduce explosion radius
- Disable `projectile-trail` if not needed

## System Requirements

- Minecraft Server 1.21+
- Java 21 or higher
- Paper API (or compatible fork like Spigot)

## Support & Issues

For issues or suggestions, check your server logs for error messages. Enable debug mode in config.yml to see additional information:

```yaml
debug: true
```

## License

This plugin is provided as-is for use on Minecraft servers.

## Version Info

- **Plugin Version**: 1.0.0
- **Minecraft Version**: 1.21+
- **Build Tool**: Gradle 8.5
- **Java Version**: 21+
