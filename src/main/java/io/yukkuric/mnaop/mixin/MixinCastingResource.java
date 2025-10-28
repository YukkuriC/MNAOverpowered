package io.yukkuric.mnaop.mixin;

import com.mna.api.capabilities.resource.ICastingResource;
import io.yukkuric.mnaop.MNAOPConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ICastingResource.class)
public class MixinCastingResource {
    @Inject(method = "hungerAffectsRegenRate", at = @At("HEAD"), remap = false, cancellable = true)
    void IgnoreSaturation(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(MNAOPConfig.FastRegenManaIgnoresSaturation());
    }
}
