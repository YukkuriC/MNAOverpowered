package io.yukkuric.mnaop.magichem.ae2.impl;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.*;
import com.aranaira.magichem.block.entity.*;
import com.aranaira.magichem.block.entity.ext.AbstractFabricationBlockEntity;
import com.aranaira.magichem.item.MateriaItem;
import com.aranaira.magichem.recipe.DistillationFabricationRecipe;
import com.aranaira.magichem.recipe.FluidDistillationFabricationRecipe;
import com.aranaira.magichem.registry.BlockRegistry;
import io.yukkuric.mnaop.MNAOPMod;
import io.yukkuric.mnaop.magichem.ae2.AlchemicalCraftingMachineCap;
import io.yukkuric.mnaop.mixin_interface.magichem.ICoFEx;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class FabricationCraftingCap extends AlchemicalCraftingMachineCap {
    protected final AbstractFabricationBlockEntity master;
    protected final ICoFEx masterEx;
    protected final int slotBottle;
    public FabricationCraftingCap(AbstractFabricationBlockEntity target) {
        master = target;
        masterEx = (ICoFEx) target;
        slotBottle = master instanceof GrandCircleFabricationBlockEntity ? GrandCircleFabricationBlockEntity.SLOT_BOTTLES :
                CircleFabricationBlockEntity.SLOT_BOTTLES;
    }

    @Override
    protected Block getDisplayBlock() {
        return BlockRegistry.GRAND_CIRCLE_FABRICATION.get();
    }
    @Override
    public boolean pushPattern(IPatternDetails pattern, KeyCounter[] keyCounters, Direction direction) {
        var output = pattern.getPrimaryOutput();
        var target = output.what();
        if (masterEx.hasAnyRecipe()) return false;

        // query recipe & size
        DistillationFabricationRecipe targetRecipe = null;
        FluidDistillationFabricationRecipe targetFluidRecipe = null;
        int batchCount;
        if (target instanceof AEItemKey targetItemKey) {
            targetRecipe = masterEx.queryRecipe(targetItemKey.getItem());
            if (targetRecipe == null) return false;
            batchCount = (int) Math.ceil(output.amount() / targetRecipe.getOutputRate() / targetRecipe.getAlchemyObject().getCount());
            MNAOPMod.LOGGER.error("test3 batch={} target={}", batchCount, targetRecipe.getBatchSize());
            if (batchCount > targetRecipe.getBatchSize()) return false;
        } else if (target instanceof AEFluidKey targetFluidKey) {
            targetFluidRecipe = masterEx.queryRecipe(targetFluidKey.getFluid());
            if (targetFluidRecipe == null) return false;
            batchCount = (int) Math.ceil(output.amount() / targetFluidRecipe.getOutputRate() / targetFluidRecipe.getAlchemyFluid().getAmount());
            if (batchCount > targetFluidRecipe.getBatchSize()) return false;
        } else return false;

        // query bottles
        var itemHandler = masterEx.getItemHandler();
        var changedBottle = false;
        long bottleCount = itemHandler.getStackInSlot(slotBottle).getCount();
        for (var kc : keyCounters) {
            for (var pair : kc) {
                var count = pair.getLongValue();
                var key = pair.getKey();
                if (key instanceof AEItemKey itemKeyInput) {
                    var itemInput = itemKeyInput.getItem();
                    if (itemInput == Items.GLASS_BOTTLE) { // placeholder item
                        changedBottle = true;
                        bottleCount += count;
                        if (bottleCount > 64) return false;
                    } else if (itemInput instanceof MateriaItem) {
                        // TODO manual materia input
                        return false;
                        /*
                        if (!InventoryHelper.hasCustomModelData(itemKeyInput.toStack())) {
                            changedBottle = true;
                            bottleCount += count;
                            if (bottleCount > 64) return false;
                        }
                        */
                    }
                } else return false;
            }
        }

        if (changedBottle) {
            itemHandler.setStackInSlot(slotBottle, new ItemStack(Items.GLASS_BOTTLE, (int) bottleCount));
        }
        masterEx.setRecipes(targetRecipe, targetFluidRecipe, batchCount);
        master.clearRecipeAfterNextProcess = true;
        master.syncAndSave();
        return true;
    }
}
