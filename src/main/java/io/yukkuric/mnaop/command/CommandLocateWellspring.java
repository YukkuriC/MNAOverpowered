package io.yukkuric.mnaop.command;

import com.mna.api.capabilities.WellspringNode;
import com.mna.api.commands.AffinityArgument;
import com.mna.capabilities.worlddata.WorldMagicProvider;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.yukkuric.mnaop.MNAOPConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.*;

import java.util.function.Predicate;

// (mnaop) locate wellspring [type]
public class CommandLocateWellspring {
    public static void Init(CommandDispatcher<CommandSourceStack> dispatcher) {
        var subCmd = MNAOPCommands.registerLine(ctx -> {
            Predicate<WellspringNode> predicate;
            try {
                var aff = AffinityArgument.getAffinity(ctx, "type");
                predicate = n -> n.getAffinity().equals(aff);
            } catch (IllegalArgumentException e) {
                predicate = n -> true;
            }
            return search(ctx, predicate);
        }, 2, Commands.literal("locate"), Commands.literal("wellspring"), Commands.argument("type", AffinityArgument.affinity()));
        dispatcher.register(((LiteralArgumentBuilder<CommandSourceStack>) subCmd).requires(src -> switch (MNAOPConfig.EnablesLocateWellspringCommand()) {
            case ON -> true;
            case OP_ONLY -> src.hasPermission(2);
            default -> false;
        }));
    }

    static int search(CommandContext<CommandSourceStack> ctx, Predicate<WellspringNode> predicate) {
        var source = ctx.getSource();
        var player = source.getPlayer();
        if (player == null) return 0;

        final int[] ret = {0};
        var level = source.getLevel();
        level.getCapability(WorldMagicProvider.MAGIC).ifPresent(wm -> {
            WellspringNode spring = null;
            BlockPos nearestPos = BlockPos.ZERO;
            var nearestDist = Double.POSITIVE_INFINITY;
            var src = player.blockPosition().atY(0);

            var nodes = wm.getWellspringRegistry().getNearbyNodes(src, 0, 46340).entrySet();
            for (var kv : nodes) {
                var key = kv.getKey();
                var value = kv.getValue();
                if (!predicate.test(value)) continue;
                var newDist = src.distSqr(key);
                if (newDist < nearestDist) {
                    nearestDist = newDist;
                    spring = value;
                    nearestPos = key;
                }
            }

            if (spring == null) {
                source.sendFailure(Component.translatable("gui.none"));
            } else {
                int x = nearestPos.getX(), z = nearestPos.getZ();
                ret[0] = (int) Math.floor(Math.sqrt(nearestDist));
                var msg = Component.translatable(
                        "commands.locate.biome.success",
                        spring.getAffinity().name(),
                        Component
                                .translatable("commands.locate.wellspring.info", x, z, spring.getStrength())
                                .withStyle(
                                        Style.EMPTY
                                                .withHoverEvent(HoverEvent.Action.SHOW_TEXT.deserializeFromLegacy(
                                                        Component.translatable("chat.coordinates.tooltip")
                                                ))
                                                .withClickEvent(new ClickEvent(
                                                        ClickEvent.Action.RUN_COMMAND,
                                                        String.format("/tp @s %s ~ %s", x, z)
                                                ))
                                                .applyFormat(ChatFormatting.GREEN)
                                ),
                        ret[0]
                );
                source.sendSuccess(() -> msg, false);
            }
        });
        return ret[0];
    }
}
