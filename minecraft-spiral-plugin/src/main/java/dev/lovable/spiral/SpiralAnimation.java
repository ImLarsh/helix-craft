package dev.lovable.spiral;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class SpiralAnimation {

    private static final int INTERPOLATION_TICKS = 2; // small smoothing window
    private static final int TURNS = 3; // number of helix turns for layout

    @Getter
    private boolean running = false;

    @NotNull private final Location center;
    private final double radius;
    private final double height;
    private final double speed;
    @NotNull private final Material blockMaterial;
    private final int particleDensity;

    private final List<BlockDisplay> displays = new ArrayList<>();
    private BukkitRunnable task;
    private double phase = 0.0;

    public void start() {
        if (this.running) return;
        if (this.center.getWorld() == null) return;

        final World world = this.center.getWorld();
        final int segments = Math.max(8, this.particleDensity);

        final BlockData blockData = this.blockMaterial.createBlockData();

        // Pre-spawn displays along the helix
        for (int i = 0; i < segments; i++) {
            final double t = (double) i / (double) segments; // 0..1
            final double angle = t * (Math.PI * 2.0 * TURNS) + this.phase;
            final double y = this.center.getY() + (t * this.height);
            final double x = this.center.getX() + (this.radius * Math.cos(angle));
            final double z = this.center.getZ() + (this.radius * Math.sin(angle));

            final Location displayLocation = new Location(world, x, y, z);
            final BlockDisplay display = world.spawn(displayLocation, BlockDisplay.class, spawned -> {
                try {
                    spawned.setBlock(blockData);
                    spawned.setInterpolationDuration(INTERPOLATION_TICKS);
                } catch (final Exception ex) {
                    // Ensure we do not crash if something goes wrong with Paper internals
                    SpiralPlugin.getInstance().getLogger().warning("Failed to configure BlockDisplay: " + ex.getMessage());
                }
            });
            this.displays.add(display);
        }

        // Start tick task
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        };
        this.task.runTaskTimer(SpiralPlugin.getInstance(), 1L, 1L);
        this.running = true;
    }

    public void stop() {
        if (!this.running) return;
        this.running = false;
        if (this.task != null) {
            try {
                this.task.cancel();
            } catch (final Exception ignored) {}
            this.task = null;
        }
        for (final BlockDisplay display : this.displays) {
            try {
                display.remove();
            } catch (final Exception ignored) {}
        }
        this.displays.clear();
    }

    private void tick() {
        if (this.center.getWorld() == null) {
            this.stop();
            return;
        }
        final World world = this.center.getWorld();

        this.phase += this.speed; // advance the spiral
        final int segments = this.displays.size();
        if (segments == 0) return;

        for (int i = 0; i < segments; i++) {
            final double t = (double) i / (double) segments; // 0..1
            final double angle = t * (Math.PI * 2.0 * TURNS) + this.phase;
            final double y = this.center.getY() + (t * this.height);
            final double x = this.center.getX() + (this.radius * Math.cos(angle));
            final double z = this.center.getZ() + (this.radius * Math.sin(angle));

            final BlockDisplay display = this.displays.get(i);
            final Location newLocation = new Location(world, x, y, z);

            try {
                // Interpolated movement for smooth visuals
                display.setInterpolationDuration(INTERPOLATION_TICKS);
                display.teleport(newLocation);
            } catch (final Exception ex) {
                // If teleport fails, skip this display this tick
            }

            // Particles along the path (density-thinned to avoid spam)
            if (i % Math.max(1, (int) Math.ceil(segments / 20.0)) == 0) {
                world.spawnParticle(Particle.END_ROD, newLocation, 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
    }
}
