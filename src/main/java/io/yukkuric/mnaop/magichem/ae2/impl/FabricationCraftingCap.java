package io.yukkuric.mnaop.magichem.ae2.impl;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.*;
import com.aranaira.magichem.block.entity.CircleFabricationBlockEntity;
import com.aranaira.magichem.block.entity.GrandCircleFabricationBlockEntity;
import com.aranaira.magichem.block.entity.ext.AbstractFabricationBlockEntity;
import com.aranaira.magichem.recipe.DistillationFabricationRecipe;
import com.aranaira.magichem.recipe.FluidDistillationFabricationRecipe;
import io.yukkuric.mnaop.magichem.ae2.AlchemicalCraftingMachineCap;
import io.yukkuric.mnaop.mixin_interface.magichem.ICoFEx;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.function.Function;

import static io.yukkuric.mnaop.magichem.ae2.AEHelpers.MateriaInventoryFiller;
import static io.yukkuric.mnaop.magichem.ae2.AEHelpers.isBottled;

public class FabricationCraftingCap extends AlchemicalCraftingMachineCap {
    protected final AbstractFabricationBlockEntity master;
    protected final ICoFEx masterEx;
    protected final Function<AbstractFabricationBlockEntity.IDs, Integer> varFunc;
    protected final boolean isGrand;
    static final List<Component> CRAFTER_TOOLTIP = List.of(Component.translatable("mnaop.magichem.ae2.fabrication.tooltip"));

    public FabricationCraftingCap(AbstractFabricationBlockEntity target) {
        super(target.getBlockState().getBlock(), CRAFTER_TOOLTIP);
        master = target;
        masterEx = (ICoFEx) target;
        isGrand = master instanceof GrandCircleFabricationBlockEntity;
        varFunc = isGrand ? GrandCircleFabricationBlockEntity::getVar :
                CircleFabricationBlockEntity::getVar;
    }

    @Override
    public boolean pushPattern(IPatternDetails pattern, KeyCounter[] keyCounters, Direction direction) {
        var output = pattern.getPrimaryOutput();
        var target = output.what();
        if (masterEx.hasAnyRecipe()) return false;
        var wisdomLevel = isGrand ? master.getCurrentWisdom(GrandCircleFabricationBlockEntity::getVar) : 0;

        int
                slotBottle = varFunc.apply(AbstractFabricationBlockEntity.IDs.SLOT_BOTTLES),
                slotInputStart = varFunc.apply(AbstractFabricationBlockEntity.IDs.SLOT_INPUT_START),
                inputCounts = varFunc.apply(AbstractFabricationBlockEntity.IDs.SLOT_INPUT_COUNT);

        // query recipe & size
        DistillationFabricationRecipe targetRecipe = null;
        FluidDistillationFabricationRecipe targetFluidRecipe = null;
        List<ItemStack> recipeInputs;
        int batchCount;
        if (target instanceof AEItemKey targetItemKey) {
            targetRecipe = masterEx.queryRecipe(targetItemKey.getItem());
            if (targetRecipe == null || targetRecipe.getWisdom() > wisdomLevel) return false;
            batchCount = (int) Math.ceil(output.amount() * targetRecipe.getOutputRate() / targetRecipe.getAlchemyObject().getCount());
            if (batchCount > targetRecipe.getBatchSize()) return false;
            recipeInputs = targetRecipe.getComponentMateria();
        } else if (target instanceof AEFluidKey targetFluidKey) {
            targetFluidRecipe = masterEx.queryRecipe(targetFluidKey.getFluid());
            if (targetFluidRecipe == null || targetFluidRecipe.getWisdom() > wisdomLevel) return false;
            batchCount = (int) Math.ceil(output.amount() * targetFluidRecipe.getOutputRate() / targetFluidRecipe.getAlchemyFluid().getAmount());
            if (batchCount > targetFluidRecipe.getBatchSize()) return false;
            recipeInputs = targetFluidRecipe.getComponentMateria();
        } else return false;

        // query materia & bottles
        var itemHandler = masterEx.getItemHandler();
        var changedBottle = false;
        long bottleCount = itemHandler.getStackInSlot(slotBottle).getCount();
        var filler = new MateriaInventoryFiller(itemHandler, slotInputStart, inputCounts, recipeInputs);
        if (filler.init()) return false;
        for (var kc : keyCounters) {
            for (var pair : kc) {
                var count = pair.getLongValue();
                var key = pair.getKey();
                if (!(key instanceof AEItemKey itemKeyInput)) return false;
                var itemInput = itemKeyInput.getItem();
                if (itemInput == Items.GLASS_BOTTLE) { // placeholder item
                    changedBottle = true;
                    bottleCount += count;
                    if (bottleCount > 64) return false;
                } else if (filler.receive(itemInput, (int) count, isBottled(itemKeyInput))) return false;
            }
        }
        if (filler.finalCheck()) return false;

        if (changedBottle) {
            itemHandler.setStackInSlot(slotBottle, new ItemStack(Items.GLASS_BOTTLE, (int) bottleCount));
        }
        masterEx.setRecipes(targetRecipe, targetFluidRecipe, batchCount);
        master.clearRecipeAfterNextProcess = true;
        filler.actuallyFill();
        master.syncAndSave();
        return true;
    }
}
