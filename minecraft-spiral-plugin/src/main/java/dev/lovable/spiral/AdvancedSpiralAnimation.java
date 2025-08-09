
package dev.lovable.spiral;

import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AdvancedSpiralAnimation {

    
    private static final double GOLDEN_RATIO = 1.618033988749;

    @Getter
    private boolean running = false;

    @NotNull private final Location center;
    @NotNull private final SpiralPreset preset;
    
    private final List<List<BlockDisplay>> spiralStreams = new ArrayList<>();
    private final Map<BlockDisplay, Double> displayPhases = new HashMap<>();
    private BukkitRunnable animationTask;
    private BukkitRunnable soundTask;
    private double globalPhase = 0.0;
    private int colorCycle = 0;

    public AdvancedSpiralAnimation(@NotNull final Location center, @NotNull final SpiralPreset preset) {
        this.center = center;
        this.preset = preset;
    }

    public void start() {
        if (this.running) return;
        if (this.center.getWorld() == null) return;

        final World world = this.center.getWorld();
        final int configuredSegments = this.preset.getSegmentsPerStream();
        final int segmentsPerStream = configuredSegments > 0
            ? configuredSegments
            : Math.max(12, this.preset.getParticleDensity() / 10);

        // Create multiple streams based on spiral type
        for (int stream = 0; stream < this.preset.getType().getStreamCount(); stream++) {
            final List<BlockDisplay> streamDisplays = new ArrayList<>();
            final double streamOffset = (Math.PI * 2.0 * stream) / this.preset.getType().getStreamCount();
            
            final Material streamMaterial = (stream % 2 == 0) ? 
                this.preset.getPrimaryMaterial() : this.preset.getSecondaryMaterial();
            final BlockData blockData = streamMaterial.createBlockData();

            for (int i = 0; i < segmentsPerStream; i++) {
                final double t = (double) i / (double) segmentsPerStream;
                final Vector3 position = calculatePosition(t, streamOffset);
                
                final Location displayLocation = new Location(world, 
                    this.center.getX() + position.x,
                    this.center.getY() + position.y,
                    this.center.getZ() + position.z);

                final BlockDisplay display = world.spawn(displayLocation, BlockDisplay.class, spawned -> {
                    try {
                        spawned.setBlock(blockData);
                        spawned.setInterpolationDuration(this.preset.getInterpolationTicks());
                        
                        if (this.preset.isGlowEffect()) {
                            spawned.setBrightness(new Display.Brightness(15, 15));
                        }
                        
                        // Add rotation and scaling effects
                        final Transformation transform = new Transformation(
                            new Vector3f(0, 0, 0),
                            new AxisAngle4f(0, 0, 0, 1),
                            new Vector3f(1.0f),
                            new AxisAngle4f(0, 0, 0, 1)
                        );
                        spawned.setTransformation(transform);
                        
                    } catch (final Exception ex) {
                        SpiralPlugin.getInstance().getLogger().warning("Failed to configure BlockDisplay: " + ex.getMessage());
                    }
                });
                
                streamDisplays.add(display);
                this.displayPhases.put(display, t + streamOffset);
            }
            
            this.spiralStreams.add(streamDisplays);
        }

        // Start animation task
        this.animationTask = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        };
        this.animationTask.runTaskTimer(SpiralPlugin.getInstance(), 1L, 1L);

        // Start ambient sound task if preset has sound
        if (this.preset.getAmbientSound() != null) {
            this.soundTask = new BukkitRunnable() {
                @Override
                public void run() {
                    playAmbientSound();
                }
            };
            this.soundTask.runTaskTimer(SpiralPlugin.getInstance(), 20L, 60L); // Every 3 seconds
        }

        this.running = true;
        SpiralPlugin.getInstance().getLogger().info("Started advanced spiral: " + this.preset.getName());
    }

    public void stop() {
        if (!this.running) return;
        this.running = false;

        if (this.animationTask != null) {
            this.animationTask.cancel();
            this.animationTask = null;
        }

        if (this.soundTask != null) {
            this.soundTask.cancel();
            this.soundTask = null;
        }

        for (final List<BlockDisplay> stream : this.spiralStreams) {
            for (final BlockDisplay display : stream) {
                try {
                    display.remove();
                } catch (final Exception ignored) {}
            }
        }

        this.spiralStreams.clear();
        this.displayPhases.clear();
    }

    private void tick() {
        if (this.center.getWorld() == null) {
            this.stop();
            return;
        }

        final World world = this.center.getWorld();
        this.globalPhase += this.preset.getSpeed();
        this.colorCycle++;

        for (int streamIndex = 0; streamIndex < this.spiralStreams.size(); streamIndex++) {
            final List<BlockDisplay> stream = this.spiralStreams.get(streamIndex);
            final double streamOffset = (Math.PI * 2.0 * streamIndex) / this.preset.getType().getStreamCount();

            for (int i = 0; i < stream.size(); i++) {
                final BlockDisplay display = stream.get(i);
                final double t = (double) i / (double) stream.size();
                final double phase = this.displayPhases.get(display) + this.globalPhase;
                
                final Vector3 position = calculateAdvancedPosition(t, phase, streamOffset);
                final Location newLocation = new Location(world,
                    this.center.getX() + position.x,
                    this.center.getY() + position.y,
                    this.center.getZ() + position.z);

                try {
                    display.setInterpolationDuration(this.preset.getInterpolationTicks());
                    display.teleport(newLocation);

                    // Dynamic scaling and rotation
                    if (this.colorCycle % 10 == 0) { // Update every 10 ticks for performance
                        final Transformation transform = new Transformation(
                            new Vector3f(0, 0, 0),
                            new AxisAngle4f((float)phase, 0, 1, 0),
                            new Vector3f(1.0f, 1.0f, 1.0f),
                            new AxisAngle4f(0, 0, 0, 1)
                        );
                        display.setTransformation(transform);
                    }

                } catch (final Exception ex) {
                    // Skip this display if teleport fails
                }

                // Advanced particle effects
                spawnAdvancedParticles(world, newLocation, phase, streamIndex);
            }
        }
    }

    private Vector3 calculatePosition(final double t, final double streamOffset) {
        final double angle = t * Math.PI * 6.0 + streamOffset; // 3 full turns
        final double x = this.preset.getRadius() * Math.cos(angle);
        final double z = this.preset.getRadius() * Math.sin(angle);
        final double y = t * this.preset.getHeight();
        return new Vector3(x, y, z);
    }

    private Vector3 calculateAdvancedPosition(final double t, final double phase, final double streamOffset) {
        switch (this.preset.getType()) {
            case DOUBLE_HELIX:
                final double helixAngle = t * Math.PI * 6.0 + phase + streamOffset;
                final double helixRadius = this.preset.getRadius() * (0.8 + 0.2 * Math.sin(phase * 3));
                return new Vector3(
                    helixRadius * Math.cos(helixAngle),
                    t * this.preset.getHeight(),
                    helixRadius * Math.sin(helixAngle)
                );

            case WAVE:
                final double waveAngle = t * Math.PI * 4.0 + phase;
                final double waveRadius = this.preset.getRadius() * Math.sin(t * Math.PI * 2 + phase);
                return new Vector3(
                    waveRadius * Math.cos(waveAngle),
                    t * this.preset.getHeight() + Math.sin(phase * 2) * 2,
                    waveRadius * Math.sin(waveAngle)
                );

            case TORNADO:
                final double tornadoAngle = t * Math.PI * 8.0 + phase;
                final double tornadoRadius = this.preset.getRadius() * (1 - t * 0.7); // Narrows towards top
                final double tornadoY = t * this.preset.getHeight() + Math.sin(phase * 4) * 0.5;
                return new Vector3(
                    tornadoRadius * Math.cos(tornadoAngle),
                    tornadoY,
                    tornadoRadius * Math.sin(tornadoAngle)
                );

            case GALAXY:
                final double galaxyAngle = t * Math.PI * 4.0 + phase * 0.5;
                final double galaxyRadius = this.preset.getRadius() * Math.pow(t, 0.7); // Logarithmic spiral
                final double armOffset = streamOffset * GOLDEN_RATIO;
                return new Vector3(
                    galaxyRadius * Math.cos(galaxyAngle + armOffset),
                    (t * this.preset.getHeight()) + Math.sin(phase) * 1.5,
                    galaxyRadius * Math.sin(galaxyAngle + armOffset)
                );

            case DNA:
                final double dnaAngle = t * Math.PI * 4.0 + phase + streamOffset;
                final double dnaRadius = this.preset.getRadius() * 0.8;
                final double dnaY = t * this.preset.getHeight() + Math.cos(dnaAngle * 2) * 0.3;
                return new Vector3(
                    dnaRadius * Math.cos(dnaAngle),
                    dnaY,
                    dnaRadius * Math.sin(dnaAngle)
                );

            default: // HELIX
                final double defaultAngle = t * Math.PI * 6.0 + phase;
                return new Vector3(
                    this.preset.getRadius() * Math.cos(defaultAngle),
                    t * this.preset.getHeight(),
                    this.preset.getRadius() * Math.sin(defaultAngle)
                );
        }
    }

    private void spawnAdvancedParticles(final World world, final Location location, final double phase, final int streamIndex) {
        if (this.colorCycle % (20 / Math.max(1, this.preset.getParticleDensity() / 50)) != 0) return;

        final Particle particle = this.preset.getParticleType();
        final int count = Math.min(3, Math.max(1, this.preset.getParticleDensity() / 100));
        
        // Create particle trails with different colors based on stream
        for (int i = 0; i < count; i++) {
            final double offsetX = (Math.random() - 0.5) * 0.5;
            final double offsetY = (Math.random() - 0.5) * 0.5;
            final double offsetZ = (Math.random() - 0.5) * 0.5;
            
            final Location particleLocation = location.clone().add(offsetX, offsetY, offsetZ);
            
            if (this.preset.isColorTransition()) {
                // Create color-changing particle effects
                final double colorPhase = phase + streamIndex * Math.PI / 3;
                world.spawnParticle(particle, particleLocation, 1, 
                    Math.sin(colorPhase) * 0.1, 
                    Math.cos(colorPhase) * 0.1, 
                    Math.sin(colorPhase + Math.PI/2) * 0.1, 
                    0.1);
            } else {
                world.spawnParticle(particle, particleLocation, 1, 0.05, 0.05, 0.05, 0.02);
            }
        }
    }

    private void playAmbientSound() {
        if (this.center.getWorld() == null) return;
        
        this.center.getWorld().playSound(this.center, this.preset.getAmbientSound(), 
            SoundCategory.AMBIENT, 0.3f, 1.0f + (float)(Math.random() * 0.2 - 0.1));
    }

    // Helper class for 3D vectors
    private static class Vector3 {
        final double x, y, z;
        
        Vector3(final double x, final double y, final double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
