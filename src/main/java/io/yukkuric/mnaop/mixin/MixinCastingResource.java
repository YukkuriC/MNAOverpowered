package io.yukkuric.mnaop.mixin;

import com.mna.api.capabilities.resource.ICastingResource;
import com.mna.api.capabilities.resource.SimpleCastingResource;
import io.yukkuric.mnaop.MNAOPConfig;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SimpleCastingResource.class)
public abstract class MixinCastingResource implements ICastingResource {
    @Override
    public boolean hungerAffectsRegenRate() {
        return MNAOPConfig.FastRegenManaIgnoresSaturation();
    }
}
