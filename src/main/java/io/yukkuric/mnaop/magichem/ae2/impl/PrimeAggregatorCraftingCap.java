package io.yukkuric.mnaop.magichem.ae2.impl;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.*;
import com.aranaira.magichem.block.entity.PrimeAggregatorBlockEntity;
import com.aranaira.magichem.foundation.IHasDeviceRecipeSlot;
import com.aranaira.magichem.registry.*;
import com.aranaira.magichem.util.InventoryHelper;
import io.yukkuric.mnaop.magichem.ae2.AlchemicalCraftingMachineCap;
import io.yukkuric.mnaop.mixin.magichem.AccessorPrimeAggregator;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

import static io.yukkuric.mnaop.MNAOPHelpers.getWorker;

public class PrimeAggregatorCraftingCap extends AlchemicalCraftingMachineCap {
    protected final PrimeAggregatorBlockEntity master;
    protected final AccessorPrimeAggregator masterEx;
    public PrimeAggregatorCraftingCap(PrimeAggregatorBlockEntity target) {
        super(target.getBlockState().getBlock(), List.of());
        master = target;
        masterEx = (AccessorPrimeAggregator) master;
    }

    @Override
    public boolean pushPattern(IPatternDetails pattern, KeyCounter[] keyCounters, Direction direction) {
        var animStage = master.getAnimStage();
        if (animStage != 0 && animStage != 1) return false;
        var itemHandler = masterEx.getItemHandler();
        if (masterEx.getItemsDelivered() > 0 || !itemHandler.getStackInSlot(PrimeAggregatorBlockEntity.SLOT_PROGRESS_HOLDER).isEmpty())
            return false;

        var target = pattern.getPrimaryOutput().what();
        if (!(target instanceof AEItemKey itemKeyOutput)) return false;
        var worker = getWorker(master);
        var code = master.setRecipe(itemKeyOutput.toStack(), worker);
        if (code != IHasDeviceRecipeSlot.ERROR_CODE_SUCCESS) return false;

        // check canFill
        var recipe = master.getCurrentRecipe();
        var itemType = recipe.getItemType();
        var itemRequired = recipe.getItemsRequired();
        var materiaType = recipe.getMateriaType();
        var materiaRequired = recipe.getMateriaRequired();
        var slurryRequired = recipe.getSlurryRequired();

        var changed = false;
        long currentItem = masterEx.getItemsDelivered();
        long currentMateria = masterEx.getMateriaDelivered();
        long currentSlurry = masterEx.getSlurryDelivered();
        var changedBottle = false;
        long bottleCount = itemHandler.getStackInSlot(PrimeAggregatorBlockEntity.SLOT_BOTTLES_OUTPUT).getCount();

        for (var kc : keyCounters) {
            for (var pair : kc) {
                var count = pair.getLongValue();
                var key = pair.getKey();
                if (key instanceof AEItemKey itemKeyInput) {
                    var itemInput = itemKeyInput.getItem();
                    if (itemInput == itemType) {
                        changed = true;
                        currentItem += count;
                        if (currentItem > itemRequired) return false;
                    } else if (itemInput == materiaType) {
                        changed = true;
                        currentMateria += count;
                        if (currentMateria > materiaRequired) return false;

                        // recycle bottles
                        if (!InventoryHelper.hasCustomModelData(itemKeyInput.toStack())) {
                            changedBottle = true;
                            bottleCount += count;
                            if (bottleCount > 64) return false;
                        }
                    }
                } else if (key instanceof AEFluidKey fluidKeyInput) {
                    var fluidType = fluidKeyInput.getFluid().getFluidType();
                    if (fluidType != FluidRegistry.ACADEMIC_SLURRY_FLUID_TYPE.get()) return false;
                    changed = true;
                    currentSlurry += count;
                    if (currentSlurry > slurryRequired) return false;
                } else return false;
            }
        }

        // actually fill
        if (changed) {
            if (changedBottle) {
                itemHandler.setStackInSlot(PrimeAggregatorBlockEntity.SLOT_BOTTLES_OUTPUT, new ItemStack(Items.GLASS_BOTTLE, (int) bottleCount));
            }
            masterEx.setItemsDelivered((int) currentItem);
            masterEx.setMateriaDelivered((int) currentMateria);
            masterEx.setSlurryDelivered((int) currentSlurry);
            if (master.getAnimStage() <= 1 && currentItem >= itemRequired) {
                masterEx.setAnimStage(2);
                if (itemHandler.getStackInSlot(PrimeAggregatorBlockEntity.SLOT_PROGRESS_HOLDER).isEmpty())
                    itemHandler.setStackInSlot(PrimeAggregatorBlockEntity.SLOT_PROGRESS_HOLDER, new ItemStack(ItemRegistry.EXALTATION_IN_PROGRESS.get()));
            }
            master.syncAndSave();
        }
        return true;
    }
}
