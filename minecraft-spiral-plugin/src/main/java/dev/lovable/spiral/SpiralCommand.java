package dev.lovable.spiral;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SpiralCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull final CommandSender commandSender,
                             @NotNull final Command command,
                             @NotNull final String label,
                             @NotNull final String[] args) {
        if (!commandSender.hasPermission("spiral.use")) {
            commandSender.sendMessage("You do not have permission to use this.");
            return true;
        }

        if (args.length == 0) {
            commandSender.sendMessage("Usage: /" + label + " <start|stop> [radius] [height] [speed] [block] [particles]");
            return true;
        }

        final String sub = args[0].toLowerCase();
        switch (sub) {
            case "start": {
                if (!(commandSender instanceof final Player player)) {
                    commandSender.sendMessage("Only players can start the spiral (needs a world & location).");
                    return true;
                }

                final double defaultRadius = SpiralPlugin.getInstance().getConfig().getDouble("radius", 3.0);
                final double defaultHeight = SpiralPlugin.getInstance().getConfig().getDouble("height", 8.0);
                final double defaultSpeed = SpiralPlugin.getInstance().getConfig().getDouble("speed", 0.15);
                final String defaultBlock = SpiralPlugin.getInstance().getConfig().getString("block", "GLOWSTONE");
                final int defaultDensity = SpiralPlugin.getInstance().getConfig().getInt("particle-density", 80);

                double radius = defaultRadius;
                double height = defaultHeight;
                double speed = defaultSpeed;
                String blockName = defaultBlock;
                int particleDensity = defaultDensity;

                try {
                    if (args.length >= 2) radius = Math.max(0.1, Double.parseDouble(args[1]));
                    if (args.length >= 3) height = Math.max(0.1, Double.parseDouble(args[2]));
                    if (args.length >= 4) speed = Math.max(0.01, Double.parseDouble(args[3]));
                    if (args.length >= 5) blockName = args[4].toUpperCase();
                    if (args.length >= 6) particleDensity = Math.max(1, Integer.parseInt(args[5]));
                } catch (final NumberFormatException ex) {
                    commandSender.sendMessage("Invalid number format. Usage: /" + label + " start [radius] [height] [speed] [block] [particles]");
                    return true;
                }

                final Material material = Material.matchMaterial(blockName);
                if (material == null || !material.isBlock()) {
                    commandSender.sendMessage("Unknown or non-block material: " + blockName);
                    return true;
                }

                if (player.getWorld() == null) {
                    commandSender.sendMessage("Your world is not available.");
                    return true;
                }

                SpiralPlugin.getInstance().getSpiralManager().start(
                        player.getLocation(),
                        radius,
                        height,
                        speed,
                        material,
                        particleDensity
                );

                commandSender.sendMessage(String.format("Started spiral: radius=%.2f height=%.2f speed=%.2f block=%s particles=%d",
                        radius, height, speed, material.name(), particleDensity));
                return true;
            }
            case "stop": {
                SpiralPlugin.getInstance().getSpiralManager().stop();
                commandSender.sendMessage("Stopped spiral.");
                return true;
            }
            default: {
                commandSender.sendMessage("Unknown subcommand. Use start/stop.");
                return true;
            }
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull final CommandSender commandSender,
                                                @NotNull final Command command,
                                                @NotNull final String alias,
                                                @NotNull final String[] args) {
        final List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], List.of("start", "stop"), completions);
            Collections.sort(completions);
            return completions;
        }
        if (args.length == 5) {
            // Material names for the 5th argument
            final String token = args[4].toUpperCase();
            for (final Material material : Material.values()) {
                if (material.isBlock() && material.name().startsWith(token)) {
                    completions.add(material.name());
                }
            }
            Collections.sort(completions);
            return completions;
        }
        return completions;
    }
}
