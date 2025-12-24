package io.yukkuric.mnaop.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class MNAOPCommands {
    static LiteralArgumentBuilder<CommandSourceStack> MAIN_CMD = Commands.literal("mnaop");

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        CommandLocateWellspring.Init(dispatcher);
        dispatcher.register(MAIN_CMD);
    }

    // helper, same as HexParse
    @SafeVarargs
    public static <T extends ArgumentBuilder> T registerLine(Command<CommandSourceStack> exec, T... args) {
        return registerLine(exec, 1, args);
    }
    @SafeVarargs
    public static <T extends ArgumentBuilder> T registerLine(Command<CommandSourceStack> exec, int execCount, T... args) {
        for (var i = 1; i <= execCount; i++)
            args[args.length - i] = (T) args[args.length - i].executes(exec);
        for (var i = args.length - 1; i > 0; i--) args[i - 1] = (T) args[i - 1].then(args[i]);
        MAIN_CMD = MAIN_CMD.then(args[0]);
        return args[0];
    }
}
