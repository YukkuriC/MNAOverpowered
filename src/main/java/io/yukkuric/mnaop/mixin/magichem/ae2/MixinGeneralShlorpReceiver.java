package io.yukkuric.mnaop.mixin.magichem.ae2;

import appeng.api.behaviors.GenericInternalInventory;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.blockentity.grid.AENetworkBlockEntity;
import appeng.capabilities.Capabilities;
import com.aranaira.magichem.foundation.IShlorpReceiver;
import com.aranaira.magichem.item.MateriaItem;
import io.yukkuric.mnaop.magichem.ae2.AEHelpers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AENetworkBlockEntity.class)
public class MixinGeneralShlorpReceiver extends BlockEntity implements IShlorpReceiver {
    public MixinGeneralShlorpReceiver() {
        super(null, null, null);
    }

    @Unique
    private GenericInternalInventory mnaop$getGeneralInventory() {
        var invCap = getCapability(Capabilities.GENERIC_INTERNAL_INV);
        return invCap.orElse(null);
    }
    @Unique
    private void mnaop$unwrapBottle(ItemStack toInsert) {
        if (!(toInsert.getItem() instanceof MateriaItem)) return;
        if (!toInsert.hasTag()) toInsert.setTag(AEHelpers.GLOB_NBT);
    }

    @Override
    public int canAcceptStackFromShlorp(ItemStack toInsert) {
        mnaop$unwrapBottle(toInsert);
        var inv = mnaop$getGeneralInventory();
        if (inv == null || !inv.canInsert()) return 0;
        int maxCap = 0;
        int maxStackSize = toInsert.getMaxStackSize();
        for (int i = 0; i < inv.size(); i++) {
            var content = inv.getStack(i);
            if (content == null) return toInsert.getCount();
            if (content.what() instanceof AEItemKey itemKey && itemKey.matches(toInsert)) {
                maxCap += (int) Math.max(0, maxStackSize - content.amount());
            }
        }
        return Math.min(toInsert.getCount(), maxCap);
    }
    @Override
    public int insertStackFromShlorp(ItemStack toInsert) {
        mnaop$unwrapBottle(toInsert);
        var inv = mnaop$getGeneralInventory();
        if (inv == null || !inv.canInsert()) return toInsert.getCount();
        int countLeft = toInsert.getCount();
        int maxStackSize = toInsert.getMaxStackSize();
        for (int i = 0; i < inv.size(); i++) {
            var content = inv.getStack(i);
            if (content == null) {
                inv.setStack(i, GenericStack.fromItemStack(toInsert));
                return 0;
            }
            if (content.what() instanceof AEItemKey itemKey && itemKey.matches(toInsert)) {
                var maxInsert = Math.min(countLeft, maxStackSize - content.amount());
                if (maxInsert <= 0) continue;
                countLeft -= maxInsert;
                inv.setStack(i, new GenericStack(content.what(), content.amount() + maxInsert));
                if (countLeft <= 0) return 0;
            }
        }
        return countLeft;
    }
}
