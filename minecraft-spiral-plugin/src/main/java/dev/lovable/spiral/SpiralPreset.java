
package dev.lovable.spiral;

import lombok.Builder;
import lombok.Getter;
import lombok.Builder.Default;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

@Builder
@Getter
public final class SpiralPreset {
    private final String name;
    private final SpiralType type;
    private final double radius;
    private final double height;
    private final double speed;
    private final Material primaryMaterial;
    private final Material secondaryMaterial; // for multi-material spirals
    private final int particleDensity;
    private final Particle particleType;
    private final Sound ambientSound;
    private final boolean glowEffect;
    private final boolean colorTransition;
    @Default
    private final int segmentsPerStream = 0; // 0 = auto
    @Default
    private final int interpolationTicks = 5; // smoother by default
    
    public static SpiralPreset getPreset(final String presetName) {
        switch (presetName.toLowerCase()) {
            case "fire":
                return SpiralPreset.builder()
                    .name("Fire Tornado")
                    .type(SpiralType.TORNADO)
                    .radius(2.5)
                    .height(12.0)
                    .speed(0.25)
                    .primaryMaterial(Material.MAGMA_BLOCK)
                    .secondaryMaterial(Material.FIRE_CORAL_BLOCK)
                    .particleDensity(150)
                    .particleType(Particle.FLAME)
                    .ambientSound(Sound.BLOCK_FIRE_AMBIENT)
                    .glowEffect(true)
                    .colorTransition(true)
                    .build();
            case "ice":
                return SpiralPreset.builder()
                    .name("Frozen Galaxy")
                    .type(SpiralType.GALAXY)
                    .radius(4.0)
                    .height(8.0)
                    .speed(0.1)
                    .primaryMaterial(Material.BLUE_ICE)
                    .secondaryMaterial(Material.PACKED_ICE)
                    .particleDensity(120)
                    .particleType(Particle.SNOWFLAKE)
                    .ambientSound(Sound.BLOCK_GLASS_BREAK)
                    .glowEffect(false)
                    .colorTransition(true)
                    .build();
            case "mystic":
                return SpiralPreset.builder()
                    .name("Mystic Portal")
                    .type(SpiralType.DOUBLE_HELIX)
                    .radius(3.5)
                    .height(10.0)
                    .speed(0.15)
                    .primaryMaterial(Material.END_STONE)
                    .secondaryMaterial(Material.PURPUR_BLOCK)
                    .particleDensity(200)
                    .particleType(Particle.PORTAL)
                    .ambientSound(Sound.BLOCK_PORTAL_AMBIENT)
                    .glowEffect(true)
                    .colorTransition(true)
                    .build();
            case "nature":
                return SpiralPreset.builder()
                    .name("Living Vine")
                    .type(SpiralType.DNA)
                    .radius(2.0)
                    .height(15.0)
                    .speed(0.08)
                    .primaryMaterial(Material.MOSS_BLOCK)
                    .secondaryMaterial(Material.OAK_LEAVES)
                    .particleDensity(100)
                    .particleType(Particle.SPORE_BLOSSOM_AIR)
                    .ambientSound(Sound.BLOCK_GRASS_BREAK)
                    .glowEffect(false)
                    .colorTransition(false)
                    .build();
            default:
                return SpiralPreset.builder()
                    .name("Classic")
                    .type(SpiralType.HELIX)
                    .radius(3.0)
                    .height(8.0)
                    .speed(0.15)
                    .primaryMaterial(Material.GLOWSTONE)
                    .secondaryMaterial(Material.GLOWSTONE)
                    .particleDensity(80)
                    .particleType(Particle.END_ROD)
                    .ambientSound(null)
                    .glowEffect(true)
                    .colorTransition(false)
                    .build();
        }
    }
}
