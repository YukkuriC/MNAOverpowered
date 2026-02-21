package io.yukkuric.mnaop.mixin.computercraft;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.peripheral.generic.methods.InventoryMethods;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(InventoryMethods.class)
public interface AccessorInventoryMethods {
    @Invoker(remap = false)
    static IItemHandler callExtractHandler(IPeripheral peripheral) {
        return null;
    }
}
