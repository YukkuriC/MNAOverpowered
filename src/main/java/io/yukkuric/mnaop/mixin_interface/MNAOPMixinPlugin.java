package io.yukkuric.mnaop.mixin_interface;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.*;

public class MNAOPMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String s) {
        MixinExtrasBootstrap.init(); // do NOT forget this using MixinExtras
    }
    @Override
    public String getRefMapperConfig() {
        return null;
    }
    @Override
    public boolean shouldApplyMixin(String s, String s1) {
        return true;
    }
    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {
    }
    @Override
    public List<String> getMixins() {
        return null;
    }
    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
    }
    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
    }
}
