# Spiral Blocks Plugin — Development Plan

Goal: Build a Paper (Minecraft) plugin that renders a mesmerizing spiral/helix of floating blocks with smooth animation and rich customization.

## 1. Scope & Features
- Dynamic Block Spiral
  - Floating blocks arranged along a helix
  - Smooth rotation/advancement over time (phase-based)
  - Configurable radius and height
  - Interpolation for fluid movement via Display entity interpolation
- Customization Options
  - Adjustable animation speed
  - Configurable block type (any Minecraft Material)
  - Customizable particle density along the spiral path
  - Adjustable spiral radius and height
- Commands
  - `/spiral start [radius] [height] [speed] [block] [particles]`
  - `/spiral stop`
  - Permission: `spiral.use`

## 2. Tech Stack
- Paper API 1.21.8 (R0.1-SNAPSHOT)
- Java 21
- Maven build
- Lombok for getters/setters/constructors

## 3. Architecture
- Main: `dev.lovable.spiral.SpiralPlugin` extends `JavaPlugin`
  - Static plugin instance (singleton access)
  - Command registration, config initialization
- Command: `SpiralCommand implements CommandExecutor, TabCompleter`
  - Start/stop control, argument parsing and safety checks
- Manager: `SpiralManager`
  - Orchestrates the lifecycle of the current animation
- Animation: `SpiralAnimation` (tick task)
  - Spawns `BlockDisplay` entities arranged along a parameterized helix
  - Updates positions each tick using a phase angle; applies interpolation to displays
  - Emits particles based on density

## 4. Data Flow
- Defaults read from `config.yml`
- Command overrides for radius/height/speed/block/particles
- One active global animation at a time (simple, extendable later for per-player support)

## 5. Files
- `minecraft-spiral-plugin/pom.xml` — Maven project
- `minecraft-spiral-plugin/src/main/java/dev/lovable/spiral/SpiralPlugin.java`
- `minecraft-spiral-plugin/src/main/java/dev/lovable/spiral/SpiralCommand.java`
- `minecraft-spiral-plugin/src/main/java/dev/lovable/spiral/SpiralManager.java`
- `minecraft-spiral-plugin/src/main/java/dev/lovable/spiral/SpiralAnimation.java`
- `minecraft-spiral-plugin/src/main/resources/plugin.yml`
- `minecraft-spiral-plugin/src/main/resources/config.yml`

## 6. Safety & Quality
- Full null-safety and error handling; validate all inputs
- Consistent naming; always use `this.` and `final` where practical
- Avoid passing plugin through constructors — use static instance from main class
- Clean up entities on stop and on plugin disable

## 7. Build & Run
1. `cd minecraft-spiral-plugin`
2. `mvn -q -DskipTests package`
3. Copy the built JAR from `target/` to your Paper server `plugins/`
4. Start the server and use `/spiral start` to see the animation

## 8. Future Extensions (Post-v1)
- Multiple simultaneous spirals
- Per-player private spirals
- Preset patterns (double helix, pulsating radius, color cycling blocks)
- GUI for live parameter tweaking
