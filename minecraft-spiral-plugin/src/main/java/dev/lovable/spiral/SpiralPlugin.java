package dev.lovable.spiral;

import lombok.Getter;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpiralPlugin extends JavaPlugin {

    @Getter
    private static SpiralPlugin instance;

    @Getter
    private SpiralManager spiralManager;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        this.spiralManager = new SpiralManager();

        final PluginCommand pluginCommand = this.getCommand("spiral");
        if (pluginCommand != null) {
            final SpiralCommand spiralCommand = new SpiralCommand();
            pluginCommand.setExecutor(spiralCommand);
            pluginCommand.setTabCompleter(spiralCommand);
        } else {
            this.getLogger().severe("Command 'spiral' not defined in plugin.yml. Disabling plugin.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.getLogger().info("SpiralBlocks enabled.");
    }

    @Override
    public void onDisable() {
        if (this.spiralManager != null) {
            this.spiralManager.stop();
        }
        this.getLogger().info("SpiralBlocks disabled.");
    }
}
