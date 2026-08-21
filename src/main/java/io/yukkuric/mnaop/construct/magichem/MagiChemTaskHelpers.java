package io.yukkuric.mnaop.construct.magichem;

import com.mna.blocks.tileentities.PedestalTile;
import com.mna.inventory.ItemInventoryBase;
import com.mna.items.runes.BookOfMarks;
import com.mna.items.runes.ItemRuneMarking;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashSet;
import java.util.function.BiConsumer;

public class MagiChemTaskHelpers {
    public static final int MAX_PEDESTAL_DEPTH = 5;

    public static int CollectTargetsFromBookOfMarks(Level level, ItemStack targetBookOfMark, BiConsumer<BlockEntity, BlockPos> collector) {
        return _CollectTargetsFromBookOfMarksImp(level, targetBookOfMark, collector, new HashSet<>(), 0);
    }
    private static int _CollectTargetsFromBookOfMarksImp(Level level, ItemStack targetBookOfMark, BiConsumer<BlockEntity, BlockPos> collector, HashSet<BlockPos> visited, int depth) {
        if (!(targetBookOfMark != null && targetBookOfMark.getItem() instanceof BookOfMarks)) return 0;
        int cntMarks = 0;
        var inv = new ItemInventoryBase(targetBookOfMark);
        for (int i = 0; i < BookOfMarks.INVENTORY_SIZE; i++) {
            var innerMark = inv.getStackInSlot(i);
            if (innerMark.isEmpty() || !(innerMark.getItem() instanceof ItemRuneMarking rune)) continue;
            var pos = rune.getLocation(innerMark);
            // validate
            if (pos == null || visited.contains(pos)) continue;
            visited.add(pos);
            cntMarks++;
            var be = level.getBlockEntity(pos);
            if (be instanceof PedestalTile pedestal) {
                var inner = pedestal.getItem(0);
                if (depth < MAX_PEDESTAL_DEPTH)
                    cntMarks += _CollectTargetsFromBookOfMarksImp(level, inner, collector, visited, depth + 1);
            } else collector.accept(be, pos);
        }
        return cntMarks;
    }
}
