
package dev.lovable.spiral;

import lombok.Getter;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpiralPlugin extends JavaPlugin {

    @Getter
    private static SpiralPlugin instance;

    @Getter
    private SpiralManager spiralManager; // Keep for backwards compatibility

    @Getter
    private AdvancedSpiralManager advancedSpiralManager;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        // Initialize both managers for compatibility
        this.spiralManager = new SpiralManager();
        this.advancedSpiralManager = new AdvancedSpiralManager();

        // Register commands
        final PluginCommand spiralCommand = this.getCommand("spiral");
        if (spiralCommand != null) {
            final AdvancedSpiralCommand commandHandler = new AdvancedSpiralCommand();
            spiralCommand.setExecutor(commandHandler);
            spiralCommand.setTabCompleter(commandHandler);
        } else {
            this.getLogger().severe("Command 'spiral' not defined in plugin.yml. Disabling plugin.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.getLogger().info("✨ SpiralBlocks Premium enabled with advanced features!");
        this.getLogger().info("Available spiral types: " + java.util.Arrays.toString(SpiralType.values()));
    }

    @Override
    public void onDisable() {
        if (this.spiralManager != null) {
            this.spiralManager.stop();
        }
        
        if (this.advancedSpiralManager != null) {
            this.advancedSpiralManager.stopAllSpirals();
        }
        
        this.getLogger().info("✨ SpiralBlocks Premium disabled. All spirals stopped.");
    }
}
