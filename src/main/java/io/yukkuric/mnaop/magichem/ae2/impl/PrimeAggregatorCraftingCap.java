package io.yukkuric.mnaop.magichem.ae2.impl;

import appeng.api.crafting.IPatternDetails;
import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import com.aranaira.magichem.block.entity.PrimeAggregatorBlockEntity;
import com.aranaira.magichem.registry.BlockRegistry;
import io.yukkuric.mnaop.magichem.ae2.AlchemicalCraftingMachineCap;
import net.minecraft.core.Direction;

import java.util.List;

import static io.yukkuric.mnaop.MNAOPHelpers.getWorker;

public class PrimeAggregatorCraftingCap extends AlchemicalCraftingMachineCap {
    protected PrimeAggregatorBlockEntity master;
    public PrimeAggregatorCraftingCap(PrimeAggregatorBlockEntity target) {
        master = target;
    }

    @Override
    protected PatternContainerGroup getCraftingMachineInfoInner() {
        var block = BlockRegistry.PRIME_AGGREGATOR.get();
        return new PatternContainerGroup(
                AEItemKey.of(block),
                block.getName(),
                List.of()
        );
    }
    @Override
    public boolean pushPattern(IPatternDetails pattern, KeyCounter[] keyCounters, Direction direction) {
        var target = pattern.getPrimaryOutput().what();
        if (!(target instanceof AEItemKey item)) return false;
        var worker = getWorker(master);
        master.setRecipe(item.toStack(), worker);
        return false; // always use item handler
    }
    @Override
    public boolean acceptsPlans() {
        var animStage = master.getAnimStage();
        return animStage == 0 || animStage == 1;
    }
}
