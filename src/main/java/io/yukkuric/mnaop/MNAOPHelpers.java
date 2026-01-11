package io.yukkuric.mnaop;

import net.minecraftforge.fml.ModList;

public class MNAOPHelpers {
    public static boolean IsMagiChemLoaded(){
        return ModList.get().isLoaded("magichem");
    }
}
