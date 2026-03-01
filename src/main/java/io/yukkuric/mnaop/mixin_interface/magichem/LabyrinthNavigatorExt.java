package io.yukkuric.mnaop.mixin_interface.magichem;

import com.aranaira.magichem.block.entity.ActuatorEnderBlockEntity;
import io.yukkuric.mnaop.MNAOPConfig;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class LabyrinthNavigatorExt {
    public static void extendNavigatorBatch(ActuatorEnderBlockEntity ender, ItemStackHandler instance, int slot, int slotStart, int slotCount) {
        final var extraBatch = MNAOPConfig.LabyrinthNavigatorExtraOutputBatch();
        if (extraBatch <= 0) return;

        // check boundary & navigator level
        int slotEnd = slotStart + slotCount;
        if (slot < slotStart || slot >= slotEnd) return;
        var instant = ender.getPowerLevel() == 2;
        if (!instant) return;

        // extra shlorps
        slot++;
        for (int extra = extraBatch; extra > 0 && slot < slotEnd; slot++) {
            var content = instance.getStackInSlot(slot);
            if (content.isEmpty()) continue;
            extra--;
            ender.createShlorpToTarget(content, true);
            instance.setStackInSlot(slot, ItemStack.EMPTY);
        }
    }
}
