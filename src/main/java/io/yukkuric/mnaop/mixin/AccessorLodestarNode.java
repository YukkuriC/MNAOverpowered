package io.yukkuric.mnaop.mixin;

import com.mna.gui.widgets.lodestar.LodestarNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.HashMap;

@Mixin(LodestarNode.class)
public interface AccessorLodestarNode {
    @Accessor(remap = false)
    HashMap<Integer, String> getConnections();
}
