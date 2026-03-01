package io.yukkuric.mnaop.mixin.magichem;

import com.aranaira.magichem.block.entity.ActuatorEnderBlockEntity;
import com.aranaira.magichem.block.entity.ext.AbstractDistillationBlockEntity;
import com.aranaira.magichem.block.entity.ext.AbstractSeparationBlockEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.yukkuric.mnaop.mixin_interface.magichem.LabyrinthNavigatorExt;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;

public class MixinFasterLabyrinthNavigator {
    @Mixin(AbstractDistillationBlockEntity.class)
    public static class Distillery {
        @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/ItemStackHandler;setStackInSlot(ILnet/minecraft/world/item/ItemStack;)V"), remap = false)
        private static void extraShlorps(ItemStackHandler instance, int slot, ItemStack stack, Operation<Void> original, @Local ActuatorEnderBlockEntity ender, @Local(argsOnly = true, ordinal = 0) Function<AbstractDistillationBlockEntity.IDs, Integer> pVarFunc) {
            original.call(instance, slot, stack);
            LabyrinthNavigatorExt.extendNavigatorBatch(ender, instance, slot, pVarFunc.apply(AbstractDistillationBlockEntity.IDs.SLOT_OUTPUT_START), pVarFunc.apply(AbstractDistillationBlockEntity.IDs.SLOT_OUTPUT_COUNT));
        }
    }
    @Mixin(AbstractSeparationBlockEntity.class)
    public static class Centrifuge {
        @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/ItemStackHandler;setStackInSlot(ILnet/minecraft/world/item/ItemStack;)V"), remap = false)
        private static void extraShlorps(ItemStackHandler instance, int slot, ItemStack stack, Operation<Void> original, @Local ActuatorEnderBlockEntity ender, @Local(argsOnly = true, ordinal = 0) Function<AbstractSeparationBlockEntity.IDs, Integer> pVarFunc) {
            original.call(instance, slot, stack);
            LabyrinthNavigatorExt.extendNavigatorBatch(ender, instance, slot, pVarFunc.apply(AbstractSeparationBlockEntity.IDs.SLOT_OUTPUT_START), pVarFunc.apply(AbstractSeparationBlockEntity.IDs.SLOT_OUTPUT_COUNT));
        }
    }
}
