
package dev.lovable.spiral;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class AdvancedSpiralManager {

    // Global spirals visible to all players
    private final Map<String, AdvancedSpiralAnimation> globalSpirals = new ConcurrentHashMap<>();
    
    // Per-player private spirals
    private final Map<UUID, Map<String, AdvancedSpiralAnimation>> playerSpirals = new ConcurrentHashMap<>();
    
    @Getter
    private int nextSpiralId = 1;

    public synchronized boolean hasActiveSpirals() {
        return !this.globalSpirals.isEmpty() || 
               this.playerSpirals.values().stream().anyMatch(map -> !map.isEmpty());
    }

    public synchronized String startGlobalSpiral(@NotNull final Location center, @NotNull final SpiralPreset preset) {
        final String spiralId = "global_" + this.nextSpiralId++;
        final AdvancedSpiralAnimation animation = new AdvancedSpiralAnimation(center, preset);
        
        this.globalSpirals.put(spiralId, animation);
        animation.start();
        
        SpiralPlugin.getInstance().getLogger().info("Started global spiral '" + spiralId + "' with preset: " + preset.getName());
        return spiralId;
    }

    public synchronized String startPlayerSpiral(@NotNull final Player player, @NotNull final SpiralPreset preset) {
        final UUID playerId = player.getUniqueId();
        final String spiralId = "player_" + player.getName() + "_" + this.nextSpiralId++;
        
        this.playerSpirals.computeIfAbsent(playerId, k -> new HashMap<>());
        
        final AdvancedSpiralAnimation animation = new AdvancedSpiralAnimation(player.getLocation(), preset);
        this.playerSpirals.get(playerId).put(spiralId, animation);
        animation.start();
        
        return spiralId;
    }

    public synchronized boolean stopSpiral(@NotNull final String spiralId) {
        // Try global spirals first
        final AdvancedSpiralAnimation globalSpiral = this.globalSpirals.remove(spiralId);
        if (globalSpiral != null) {
            globalSpiral.stop();
            return true;
        }

        // Try player spirals
        for (final Map<String, AdvancedSpiralAnimation> playerMap : this.playerSpirals.values()) {
            final AdvancedSpiralAnimation playerSpiral = playerMap.remove(spiralId);
            if (playerSpiral != null) {
                playerSpiral.stop();
                return true;
            }
        }
        
        return false;
    }

    public synchronized void stopAllGlobalSpirals() {
        this.globalSpirals.values().forEach(AdvancedSpiralAnimation::stop);
        this.globalSpirals.clear();
    }

    public synchronized void stopAllPlayerSpirals(@NotNull final UUID playerId) {
        final Map<String, AdvancedSpiralAnimation> playerMap = this.playerSpirals.get(playerId);
        if (playerMap != null) {
            playerMap.values().forEach(AdvancedSpiralAnimation::stop);
            playerMap.clear();
        }
    }

    public synchronized void stopAllSpirals() {
        stopAllGlobalSpirals();
        
        for (final UUID playerId : this.playerSpirals.keySet()) {
            stopAllPlayerSpirals(playerId);
        }
        this.playerSpirals.clear();
    }

    public synchronized List<String> listActiveSpirals() {
        final List<String> spirals = new ArrayList<>();
        spirals.addAll(this.globalSpirals.keySet());
        
        this.playerSpirals.values().forEach(playerMap -> spirals.addAll(playerMap.keySet()));
        return spirals;
    }

    public synchronized int getActiveSpiralCount() {
        int total = this.globalSpirals.size();
        total += this.playerSpirals.values().stream().mapToInt(Map::size).sum();
        return total;
    }
}
