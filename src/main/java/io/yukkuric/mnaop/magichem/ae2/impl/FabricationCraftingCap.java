package io.yukkuric.mnaop.magichem.ae2.impl;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.*;
import com.aranaira.magichem.block.entity.ext.AbstractFabricationBlockEntity;
import com.aranaira.magichem.registry.BlockRegistry;
import io.yukkuric.mnaop.magichem.ae2.AlchemicalCraftingMachineCap;
import io.yukkuric.mnaop.mixin.magichem.AccessorCircleFabrication;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class FabricationCraftingCap extends AlchemicalCraftingMachineCap {
    protected AbstractFabricationBlockEntity master;
    protected AccessorCircleFabrication masterEx;
    public FabricationCraftingCap(AbstractFabricationBlockEntity target) {
        master = target;
        masterEx = (AccessorCircleFabrication) target;
    }

    @Override
    protected Block getDisplayBlock() {
        return BlockRegistry.GRAND_CIRCLE_FABRICATION.get();
    }
    @Override
    public boolean pushPattern(IPatternDetails pattern, KeyCounter[] keyCounters, Direction direction) {
        var target = pattern.getPrimaryOutput().what();
        var targetIsFluid = false;
        ResourceLocation targetId = null;
        if (target instanceof AEItemKey targetItemKey) {
            targetId = ForgeRegistries.ITEMS.getKey(targetItemKey.getItem());
        } else if (target instanceof AEFluidKey targetFluidKey) {
            targetIsFluid = true;
            targetId = ForgeRegistries.FLUIDS.getKey(targetFluidKey.getFluid());
        } else return false;

        masterEx.setDoDeferredRecipeCheck(true);
        masterEx.setDeferredRecipeIsFluid(targetIsFluid);
        masterEx.setDeferredRecipeQuery(targetId);
        master.clearRecipeAfterNextProcess = true;
        master.syncAndSave();

        return false;
    }
}
