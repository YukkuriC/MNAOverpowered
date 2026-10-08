package io.yukkuric.mnaop.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mna.api.affinity.Affinity;
import com.mna.api.capabilities.IWellspringNodeRegistry;
import com.mna.api.capabilities.WellspringNode;
import com.mna.capabilities.worlddata.WellspringNodeRegistry;
import io.yukkuric.mnaop.MNAOPConfig;
import io.yukkuric.mnaop.mixin_interface.IWellspringNode;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(WellspringNodeRegistry.class)
public abstract class MixinWellspringSystem implements IWellspringNodeRegistry {
    @WrapMethod(method = "insertPowerDiminishing", remap = false)
    private float EmpoweredMatrix1(UUID player, Level world, Affinity type, float amount, float diminish, Operation<Float> original) {
        if (MNAOPConfig.EmpoweredEldrinMatrix()) {
            var mult = getEldrinGenerationMultiplierFor(player, world, type);
            amount *= mult;
        }

        if (MNAOPConfig.NonDiminishingEldrinMatrix()) {
            return insertPower(player, world, type, amount);
        } else {
            return original.call(player, world, type, amount, diminish);
        }
    }

    @Inject(method = "lambda$addRandomNode$2", at = @At("RETURN"), remap = false)
    private static void afterGenRandomNode(CallbackInfoReturnable<WellspringNode> cir) {
        var node = cir.getReturnValue();
        var smin = MNAOPConfig.NaturalWellspringMinStrength();
        var smax = MNAOPConfig.NaturalWellspringMaxStrength();
        IWellspringNode.class.cast(node).setStrength(smin + Math.random() * (smax - smin));
    }
    @Inject(method = "lambda$addRandomNode$3", at = @At("RETURN"), remap = false)
    private void afterGenRandomNode2(CallbackInfoReturnable<WellspringNode> cir) {
        afterGenRandomNode(cir);
    }
}
