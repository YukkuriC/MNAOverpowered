package io.yukkuric.mnaop.magichem.cc;

import dan200.computercraft.api.ComputerCraftAPI;

public class MagiChemCCInterop {
    public static void Init() {
        ComputerCraftAPI.registerGenericSource(MateriaPeripheral.INSTANCE);
    }
}
