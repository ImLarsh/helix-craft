
package dev.lovable.spiral;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class AdvancedSpiralCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull final CommandSender sender,
                             @NotNull final Command command,
                             @NotNull final String label,
                             @NotNull final String[] args) {
        if (!sender.hasPermission("spiral.use")) {
            sender.sendMessage("§cYou do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender, label);
            return true;
        }

        final String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "start":
                return handleStart(sender, args);
            case "preset":
                return handlePreset(sender, args);
            case "stop":
                return handleStop(sender, args);
            case "list":
                return handleList(sender);
            case "info":
                return handleInfo(sender);
            case "presets":
                return handlePresets(sender);
            default:
                sendHelp(sender, label);
                return true;
        }
    }

    private boolean handleStart(@NotNull final CommandSender sender, @NotNull final String[] args) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage("§cOnly players can start spirals.");
            return true;
        }

        if (args.length < 6) {
            sender.sendMessage("§cUsage: /spiral start <type> <radius> <height> <speed> <material> [secondary_material]");
            sender.sendMessage("§7Types: " + String.join(", ", getSpiralTypeNames()));
            return true;
        }

        try {
            final SpiralType type = SpiralType.fromString(args[1]);
            final double radius = Math.max(0.5, Math.min(10.0, Double.parseDouble(args[2])));
            final double height = Math.max(1.0, Math.min(20.0, Double.parseDouble(args[3])));
            final double speed = Math.max(0.01, Math.min(1.0, Double.parseDouble(args[4])));
            
            final Material primaryMaterial = Material.matchMaterial(args[5]);
            if (primaryMaterial == null || !primaryMaterial.isBlock()) {
                sender.sendMessage("§cInvalid primary material: " + args[5]);
                return true;
            }

            Material secondaryMaterial = primaryMaterial;
            if (args.length >= 7) {
                secondaryMaterial = Material.matchMaterial(args[6]);
                if (secondaryMaterial == null || !secondaryMaterial.isBlock()) {
                    secondaryMaterial = primaryMaterial;
                }
            }

            final SpiralPreset customPreset = SpiralPreset.builder()
                .name("Custom")
                .type(type)
                .radius(radius)
                .height(height)
                .speed(speed)
                .primaryMaterial(primaryMaterial)
                .secondaryMaterial(secondaryMaterial)
                .particleDensity(100)
                .particleType(Particle.END_ROD)
                .ambientSound(null)
                .glowEffect(true)
                .colorTransition(false)
                .build();

            final String spiralId = SpiralPlugin.getInstance().getAdvancedSpiralManager()
                .startGlobalSpiral(player.getLocation(), customPreset);

            sender.sendMessage("§a✨ Started spiral '" + spiralId + "'");
            sender.sendMessage("§7Type: §f" + type.name() + " §8| §7Radius: §f" + radius + 
                             " §8| §7Height: §f" + height + " §8| §7Speed: §f" + speed);
            
        } catch (final NumberFormatException ex) {
            sender.sendMessage("§cInvalid number format. Please check your values.");
            return true;
        }

        return true;
    }

    private boolean handlePreset(@NotNull final CommandSender sender, @NotNull final String[] args) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage("§cOnly players can start spirals.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /spiral preset <preset_name>");
            sender.sendMessage("§7Available presets: fire, ice, mystic, nature, classic");
            return true;
        }

        final String presetName = args[1].toLowerCase();
        final SpiralPreset preset = SpiralPreset.getPreset(presetName);
        
        final String spiralId = SpiralPlugin.getInstance().getAdvancedSpiralManager()
            .startGlobalSpiral(player.getLocation(), preset);

        sender.sendMessage("§a✨ Started preset spiral '" + preset.getName() + "' (ID: " + spiralId + ")");
        sender.sendMessage("§7" + preset.getType().getDescription());
        
        return true;
    }

    private boolean handleStop(@NotNull final CommandSender sender, @NotNull final String[] args) {
        if (args.length < 2) {
            // Stop all spirals
            SpiralPlugin.getInstance().getAdvancedSpiralManager().stopAllSpirals();
            sender.sendMessage("§a✨ Stopped all spirals.");
            return true;
        }

        final String spiralId = args[1];
        final boolean stopped = SpiralPlugin.getInstance().getAdvancedSpiralManager().stopSpiral(spiralId);
        
        if (stopped) {
            sender.sendMessage("§a✨ Stopped spiral '" + spiralId + "'.");
        } else {
            sender.sendMessage("§cSpiral '" + spiralId + "' not found.");
        }
        
        return true;
    }

    private boolean handleList(@NotNull final CommandSender sender) {
        final List<String> activeSpirals = SpiralPlugin.getInstance().getAdvancedSpiralManager().listActiveSpirals();
        
        if (activeSpirals.isEmpty()) {
            sender.sendMessage("§7No active spirals.");
            return true;
        }

        sender.sendMessage("§a✨ Active Spirals (" + activeSpirals.size() + "):");
        for (final String spiralId : activeSpirals) {
            sender.sendMessage("§f• §7" + spiralId);
        }
        
        return true;
    }

    private boolean handleInfo(@NotNull final CommandSender sender) {
        final int spiralCount = SpiralPlugin.getInstance().getAdvancedSpiralManager().getActiveSpiralCount();
        
        sender.sendMessage("§a✨ Spiral Plugin Premium §f- §7Advanced Features");
        sender.sendMessage("§7Active Spirals: §f" + spiralCount);
        sender.sendMessage("§7Available Types: §f" + String.join(", ", getSpiralTypeNames()));
        sender.sendMessage("§7Premium Features: §fMultiple spirals, Advanced particles, Sound effects, Presets");
        
        return true;
    }

    private boolean handlePresets(@NotNull final CommandSender sender) {
        sender.sendMessage("§a✨ Available Spiral Presets:");
        sender.sendMessage("§f• §c§lFire §8- §7Blazing tornado with magma blocks");
        sender.sendMessage("§f• §b§lIce §8- §7Frozen galaxy with ice effects");
        sender.sendMessage("§f• §5§lMystic §8- §7Portal-themed double helix");
        sender.sendMessage("§f• §a§lNature §8- §7Living vine DNA pattern");
        sender.sendMessage("§f• §e§lClassic §8- §7Traditional glowstone spiral");
        sender.sendMessage("§7Usage: §f/spiral preset <name>");
        
        return true;
    }

    private void sendHelp(@NotNull final CommandSender sender, @NotNull final String label) {
        sender.sendMessage("§a✨ Spiral Plugin Premium §f- §7Commands");
        sender.sendMessage("§f/" + label + " start <type> <radius> <height> <speed> <material> [secondary]");
        sender.sendMessage("§f/" + label + " preset <name> §8- §7Use premium presets");
        sender.sendMessage("§f/" + label + " stop [spiral_id] §8- §7Stop specific or all spirals");
        sender.sendMessage("§f/" + label + " list §8- §7List active spirals");
        sender.sendMessage("§f/" + label + " presets §8- §7View available presets");
        sender.sendMessage("§f/" + label + " info §8- §7Plugin information");
    }

    private List<String> getSpiralTypeNames() {
        final List<String> names = new ArrayList<>();
        for (final SpiralType type : SpiralType.values()) {
            names.add(type.name().toLowerCase());
        }
        return names;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull final CommandSender sender,
                                                @NotNull final Command command,
                                                @NotNull final String alias,
                                                @NotNull final String[] args) {
        final List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], 
                Arrays.asList("start", "preset", "stop", "list", "info", "presets"), completions);
        } else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "start":
                    StringUtil.copyPartialMatches(args[1], getSpiralTypeNames(), completions);
                    break;
                case "preset":
                    StringUtil.copyPartialMatches(args[1], 
                        Arrays.asList("fire", "ice", "mystic", "nature", "classic"), completions);
                    break;
                case "stop":
                    StringUtil.copyPartialMatches(args[1], 
                        SpiralPlugin.getInstance().getAdvancedSpiralManager().listActiveSpirals(), completions);
                    break;
            }
        } else if (args.length == 6 || args.length == 7) { // Material completions
            final String token = args[args.length - 1].toUpperCase();
            for (final Material material : Material.values()) {
                if (material.isBlock() && material.name().startsWith(token)) {
                    completions.add(material.name().toLowerCase());
                }
            }
        }

        Collections.sort(completions);
        return completions;
    }
}
