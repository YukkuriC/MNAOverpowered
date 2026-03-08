package io.yukkuric.mnaop.magichem.ae2.impl;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import com.aranaira.magichem.block.entity.CentrifugeBlockEntity;
import com.aranaira.magichem.block.entity.GrandCentrifugeBlockEntity;
import com.aranaira.magichem.block.entity.ext.AbstractSeparationBlockEntity;
import com.aranaira.magichem.item.MateriaItem;
import com.aranaira.magichem.recipe.FixationSeparationRecipe;
import io.yukkuric.mnaop.magichem.ae2.AlchemicalCraftingMachineCap;
import io.yukkuric.mnaop.mixin_interface.magichem.IFixSepEx;
import net.minecraft.core.Direction;

import java.util.List;
import java.util.function.Function;

public class CentrifugeCraftingCap extends AlchemicalCraftingMachineCap {
    protected final IFixSepEx masterEx;
    protected final int slotBottle, slotInputStart, inputCounts;
    protected final AbstractSeparationBlockEntity master;

    public CentrifugeCraftingCap(AbstractSeparationBlockEntity be) {
        super(be.getBlockState().getBlock(), List.of());
        master = be;
        masterEx = (IFixSepEx) master;

        var isGrand = master instanceof GrandCentrifugeBlockEntity;
        Function<AbstractSeparationBlockEntity.IDs, Integer> varFunc = isGrand ? GrandCentrifugeBlockEntity::getVar : CentrifugeBlockEntity::getVar;
        slotBottle = varFunc.apply(AbstractSeparationBlockEntity.IDs.SLOT_BOTTLES);
        slotInputStart = varFunc.apply(AbstractSeparationBlockEntity.IDs.SLOT_INPUT_START);
        inputCounts = varFunc.apply(AbstractSeparationBlockEntity.IDs.SLOT_INPUT_COUNT);
    }
    @Override
    public boolean pushPattern(IPatternDetails pattern, KeyCounter[] keyCounters, Direction direction) {
        if (masterEx.hasAnyRecipe()) return false;

        // fetch single recipe from input(s)
        var inputs = pattern.getInputs();
        if (inputs.length != 1 || inputs[0].getMultiplier() > masterEx.getBatchSize()) return false;
        var choices = inputs[0].getPossibleInputs();
        MateriaItem materia = null;
        for (var choice : choices) {
            if (!(choice.what() instanceof AEItemKey ik && ik.getItem() instanceof MateriaItem mat)) return false;
            if (materia == null) materia = mat;
            else if (materia != mat) return false;
        }
        var recipe = FixationSeparationRecipe.getSeparatingRecipe(master.getLevel(), materia);
        if (recipe == null) return false;

        // check all filling
        var itemHandler = masterEx.getItemHandler();
        for (var kc : keyCounters) {
            for (var pair : kc) {
                var count = pair.getLongValue();
                var key = pair.getKey();
                if (!(key instanceof AEItemKey itemKeyInput))
                    return false;

                // find empty slot to fill
                var countLeft = count;
                for (int offset = 0; offset < inputCounts; offset++) {
                    var content = itemHandler.getStackInSlot(slotInputStart + offset);
                    if (content.isEmpty()) countLeft = 0;
                    else if (itemKeyInput.matches(content))
                        countLeft -= content.getMaxStackSize() - content.getCount();
                    if (countLeft <= 0) break;
                }
                if (countLeft > 0) return false;
            }
        }

        // apply recipe
        masterEx.setRecipe(recipe);
        master.clearRecipeAfterNextProcess = true;
        for (var kc : keyCounters) {
            for (var pair : kc) {
                var count = pair.getLongValue();
                var itemKeyInput = (AEItemKey) pair.getKey();

                // fill for real
                int countLeft = (int) count;
                for (int offset = 0; offset < inputCounts; offset++) {
                    var content = itemHandler.getStackInSlot(slotInputStart + offset);
                    if (content.isEmpty()) {
                        itemHandler.setStackInSlot(slotInputStart + offset, itemKeyInput.toStack(countLeft));
                        break;
                    } else if (itemKeyInput.matches(content)) {
                        int step = Math.min(content.getMaxStackSize() - content.getCount(), countLeft);
                        countLeft -= step;
                        if (countLeft <= 0) break;
                    }
                }
            }
        }
        master.syncAndSave();
        return true;
    }
}
