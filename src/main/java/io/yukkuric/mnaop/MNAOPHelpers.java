package io.yukkuric.mnaop;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;

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
}
