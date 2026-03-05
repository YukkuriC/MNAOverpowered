package io.yukkuric.mnaop.magichem.ae2.impl;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.*;
import com.aranaira.magichem.block.entity.FuseryBlockEntity;
import com.aranaira.magichem.block.entity.GrandFuseryBlockEntity;
import com.aranaira.magichem.block.entity.ext.AbstractFixationBlockEntity;
import com.aranaira.magichem.item.MateriaItem;
import com.aranaira.magichem.recipe.FixationSeparationRecipe;
import com.aranaira.magichem.registry.FluidRegistry;
import io.yukkuric.mnaop.magichem.ae2.AEHelpers;
import io.yukkuric.mnaop.magichem.ae2.AlchemicalCraftingMachineCap;
import io.yukkuric.mnaop.mixin_interface.magichem.IFixSepEx;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Function;

import static io.yukkuric.mnaop.magichem.ae2.AEHelpers.PLACEHOLDER_TOOLTIP;
import static io.yukkuric.mnaop.magichem.ae2.AEHelpers.isBottled;

public class FuseryCraftingCap extends AlchemicalCraftingMachineCap {
    // lateinit vars
    protected final IFixSepEx masterEx;
    protected final int slotBottle, slotInputStart, inputCounts;
    protected final AbstractFixationBlockEntity master;

    public FuseryCraftingCap(AbstractFixationBlockEntity be) {
        super(be.getBlockState().getBlock(), PLACEHOLDER_TOOLTIP);
        master = be;
        masterEx = (IFixSepEx) master;

        var isGrand = master instanceof GrandFuseryBlockEntity;
        Function<AbstractFixationBlockEntity.IDs, Integer> varFunc = isGrand ? GrandFuseryBlockEntity::getVar : FuseryBlockEntity::getVar;
        slotBottle = varFunc.apply(AbstractFixationBlockEntity.IDs.SLOT_BOTTLES);
        slotInputStart = varFunc.apply(AbstractFixationBlockEntity.IDs.SLOT_INPUT_START);
        inputCounts = varFunc.apply(AbstractFixationBlockEntity.IDs.SLOT_INPUT_COUNT);
    }
    @Override
    public boolean pushPattern(IPatternDetails pattern, KeyCounter[] keyCounters, Direction direction) {
        if (masterEx.hasAnyRecipe()) return false;
        var output = pattern.getPrimaryOutput();
        if (!(output.what() instanceof AEItemKey itemKey && itemKey.getItem() instanceof MateriaItem materia))
            return false;
        var recipe = FixationSeparationRecipe.getSeparatingRecipe(master.getLevel(), materia);
        if (recipe == null) return false;

        // check all filling
        var itemHandler = masterEx.getItemHandler();
        var changedBottle = false;
        long bottleCount = itemHandler.getStackInSlot(slotBottle).getCount();
        var filler = new AEHelpers.MateriaInventoryFiller(itemHandler, slotInputStart, inputCounts, recipe.getComponentMateria());
        if (filler.init()) return false;
        for (var kc : keyCounters) {
            for (var pair : kc) {
                var count = pair.getLongValue();
                var key = pair.getKey();

                // handle slurry
                if (key instanceof AEFluidKey fluidKeyInput) {
                    var fluidType = fluidKeyInput.getFluid().getFluidType();
                    if (fluidType != FluidRegistry.ACADEMIC_SLURRY_FLUID_TYPE.get()) return false;
                    // TODO handle slurry
                    continue;
                }
                if (!(key instanceof AEItemKey itemKeyInput)) return false;

                // handle all items
                var itemInput = itemKeyInput.getItem();
                if (itemInput == Items.GLASS_BOTTLE) { // placeholder item
                    changedBottle = true;
                    bottleCount += count;
                    if (bottleCount > 64) return false;
                } else if (filler.receive(itemInput, (int) count, isBottled(itemKeyInput))) return false;
            }
        }
        if (filler.finalCheck()) return false;

        // apply recipe
        if (changedBottle) {
            itemHandler.setStackInSlot(slotBottle, new ItemStack(Items.GLASS_BOTTLE, (int) bottleCount));
        }
        masterEx.setRecipe(recipe);
        master.clearRecipeAfterNextProcess = true;
        filler.actuallyFill();
        master.syncAndSave();
        return true;
    }
}
