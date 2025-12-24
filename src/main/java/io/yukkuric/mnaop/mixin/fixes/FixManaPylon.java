package io.yukkuric.mnaop.mixin.fixes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mna.blocks.tileentities.pylon.ManaPylonTile;
import com.mna.entities.constructs.animated.Construct;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ManaPylonTile.class)
public class FixManaPylon {
    @WrapOperation(method = "lambda$rechargeConstructs$1", at = @At(value = "INVOKE", target = "Lcom/mna/entities/constructs/animated/Construct;adjustMana(F)V"), remap = false, require = 0)
    void DontTargetFarConstructs(Construct instance, float amount, Operation<Void> original) {
        original.call(instance, Math.max(0, amount));
    }
}
