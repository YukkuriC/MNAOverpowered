package io.yukkuric.mnaop.mixin;

import com.mna.api.affinity.Affinity;
import com.mna.api.capabilities.IWellspringNodeRegistry;
import com.mna.capabilities.worlddata.WellspringNodeRegistry;
import io.yukkuric.mnaop.MNAOPConfig;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(WellspringNodeRegistry.class)
public abstract class MixinWellspringSystem implements IWellspringNodeRegistry {
    @Inject(method = "insertPowerDiminishing", at = @At("HEAD"), cancellable = true, remap = false)
    private void EmpoweredMatrix(UUID player, Level world, Affinity type, float amount, float diminish, CallbackInfoReturnable<Float> cir) {
        if (!MNAOPConfig.EmpoweredEldrinMatrix()) return;
        var mult = getEldrinGenerationMultiplierFor(player, world, type);
        var ret = insertPower(player, world, type, mult * amount);
        cir.setReturnValue(ret);
        cir.cancel();
    }
}
