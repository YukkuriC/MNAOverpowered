package io.yukkuric.mnaop.magichem.cc;

import dan200.computercraft.api.ComputerCraftAPI;

public class MagiChemCCInterop {
    public static void Init() {
        ComputerCraftAPI.registerGenericSource(AlchemicalNexusPeripheral.INSTANCE);
        ComputerCraftAPI.registerGenericSource(AlchemicalNexusRouterPeripheral.INSTANCE);
        ComputerCraftAPI.registerGenericSource(MateriaPeripheral.INSTANCE);
        ComputerCraftAPI.registerGenericSource(RecipePeripheral.INSTANCE);
    }
}
