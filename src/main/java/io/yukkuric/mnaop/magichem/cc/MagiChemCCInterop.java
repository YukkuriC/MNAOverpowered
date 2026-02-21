package io.yukkuric.mnaop.magichem.cc;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.GenericPeripheral;
import io.yukkuric.mnaop.MNAOPMod;
import io.yukkuric.mnaop.mixin_interface.MNAOPMixinPlugin;

public class MagiChemCCInterop {
    static void doRegister(GenericPeripheral peri) {
        var className = peri.getClass().getSimpleName();
        if (MNAOPMixinPlugin.isPeripheralDenied(className)) {
            MNAOPMod.LOGGER.warn("Denied peripheral: {}", className);
            return;
        }
        ComputerCraftAPI.registerGenericSource(peri);
    }

    public static void Init() {
        doRegister(AlchemicalNexusPeripheral.INSTANCE);
        doRegister(AlchemicalNexusRouterPeripheral.INSTANCE);
        doRegister(MateriaPeripheral.INSTANCE);
        doRegister(RecipePeripheral.INSTANCE);
    }
}
