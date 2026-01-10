package io.yukkuric.mnaop.mixin.fixes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mna.api.items.DynamicItemFilter;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.*;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DynamicItemFilter.class)
public class FixItemFilterWithHollow {
    @WrapOperation(method = "loadItemsFromTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/ContainerHelper;loadAllItems(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/NonNullList;)V"), require = 0)
    void SetRealListSize(CompoundTag listRaw, NonNullList<ItemStack> output, Operation<Void> original) {
        ListTag itemsRaw = listRaw.getList("Items", Tag.TAG_COMPOUND);
        int maxIdx = 0;
        for (var tagRaw : itemsRaw) {
            var itemRaw = (CompoundTag) tagRaw;
            maxIdx = Math.max(maxIdx, itemRaw.getByte("Slot") & 255);
        }
        for (var i = output.size(); i <= maxIdx; i++) {
            output.add(ItemStack.EMPTY);
        }
        original.call(listRaw, output);
    }
}
