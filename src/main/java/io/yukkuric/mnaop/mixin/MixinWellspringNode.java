package io.yukkuric.mnaop.mixin;

import com.mna.api.capabilities.WellspringNode;
import io.yukkuric.mnaop.mixin_interface.IWellspringNode;
import org.spongepowered.asm.mixin.*;

@Mixin(WellspringNode.class)
public class MixinWellspringNode implements IWellspringNode {
    @Shadow(remap = false)
    @Mutable
    private float strength;
    public void setStrength(double _strength) {
        strength = (float) _strength;
    }
}
