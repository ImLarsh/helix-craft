package dev.lovable.spiral;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SpiralManager {

    @Getter
    @Nullable
    private SpiralAnimation currentAnimation;

    public synchronized boolean isRunning() {
        return this.currentAnimation != null && this.currentAnimation.isRunning();
    }

    public synchronized void start(@NotNull final Location center,
                                   final double radius,
                                   final double height,
                                   final double speed,
                                   @NotNull final Material blockMaterial,
                                   final int particleDensity) {
        if (this.isRunning()) {
            this.stop();
        }
        this.currentAnimation = new SpiralAnimation(center, radius, height, speed, blockMaterial, particleDensity);
        this.currentAnimation.start();
    }

    public synchronized void stop() {
        if (this.currentAnimation != null) {
            this.currentAnimation.stop();
            this.currentAnimation = null;
        }
    }
}
