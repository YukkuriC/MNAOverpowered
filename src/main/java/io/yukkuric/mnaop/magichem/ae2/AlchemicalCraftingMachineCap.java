package io.yukkuric.mnaop.magichem.ae2;

import appeng.api.implementations.blockentities.ICraftingMachine;
import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.capabilities.Capabilities;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AlchemicalCraftingMachineCap implements ICapabilityProvider, ICraftingMachine {
    LazyOptional<ICraftingMachine> myProvider;
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        if (!capability.equals(Capabilities.CRAFTING_MACHINE)) return LazyOptional.empty();
        if (myProvider == null) myProvider = LazyOptional.of(() -> this);
        return myProvider.cast();
    }

    PatternContainerGroup _cachedGroupInfo;
    public PatternContainerGroup getCraftingMachineInfo() {
        if (_cachedGroupInfo == null) _cachedGroupInfo = getCraftingMachineInfoInner();
        return _cachedGroupInfo;
    }
    protected abstract PatternContainerGroup getCraftingMachineInfoInner();

    public boolean acceptsPlans() {
        // never return false here, or AE2 treats it like normal inventory
        return true;
    }
}
