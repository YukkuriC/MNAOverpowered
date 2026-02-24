package io.yukkuric.mnaop;

import com.mojang.authlib.GameProfile;
import io.yukkuric.mnaop.mixin_interface.magichem.IHasLastUser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.ModList;

import java.util.UUID;

public class MNAOPHelpers {
    public static boolean IsModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
    public static boolean IsMagiChemLoaded() {
        return ModList.get().isLoaded("magichem");
    }
    public static ResourceLocation modLoc(String path) {
        return ResourceLocation.tryBuild(MNAOPMod.MODID, path);
    }
    public static String getConstructFeedbackLang(String sub) {
        return "mnaop.constructs.feedback." + sub;
    }

    private static FakePlayer workerSetRecipe;
    public static ServerPlayer getFallbackWorker(ServerLevel level) {
        if (workerSetRecipe == null) {
            workerSetRecipe = new FakePlayer(level.getServer().overworld(), new GameProfile(UUID.randomUUID(), "worker"));
        }
        return workerSetRecipe;
    }
    public static ServerPlayer getWorker(BlockEntity src) {
        var level = (ServerLevel) src.getLevel();
        ServerPlayer worker = null;
        if (src instanceof IHasLastUser device) worker = device.getLastUser(level);
        if (worker == null) worker = getFallbackWorker(level);
        return worker;
    }
}
