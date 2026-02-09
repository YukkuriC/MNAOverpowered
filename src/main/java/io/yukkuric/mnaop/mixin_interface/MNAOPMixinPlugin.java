package io.yukkuric.mnaop.mixin_interface;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import io.yukkuric.mnaop.MNAOPMod;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MNAOPMixinPlugin implements IMixinConfigPlugin {
    public static MNAOPMixinPlugin INSTANCE;
    public MNAOPMixinPlugin() {
        INSTANCE = this;
    }
    public static boolean isConstructTaskDenied(String path) {
        return INSTANCE.CFG.deniedConstructTasks.contains(path);
    }

    final MNAOPMixinConfigFile CFG = new MNAOPMixinConfigFile();
    @Override
    public void onLoad(String s) {
        MixinExtrasBootstrap.init(); // do NOT forget this using MixinExtras

        // try load/save config file
        CFG.load();
        CFG.save();
    }
    @Override
    public String getRefMapperConfig() {
        return null;
    }
    @Override
    public boolean shouldApplyMixin(String targetCls, String mixinCls) {
        var raw = mixinCls.split("\\.");
        var mixinClsName = raw[raw.length - 1];
        if (CFG.deniedMixinClasses.contains(mixinClsName)) {
            MNAOPMod.LOGGER.warn("Denied mixin class: {}", mixinCls);
            return false;
        }
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
