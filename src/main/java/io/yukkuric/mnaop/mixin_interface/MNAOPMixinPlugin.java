package io.yukkuric.mnaop.mixin_interface;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.*;

public class MNAOPMixinPlugin implements IMixinConfigPlugin {
    static final Logger LOGGER = LogManager.getLogger("mnaop-mixin");
    static final Map<String, String> modCheckTargets = new HashMap<>() {
        {
            put("magichem", "com.aranaira.magichem.MagiChemMod");
            put("computercraft", "dan200.computercraft.ComputerCraft");
        }
    };

    public static MNAOPMixinPlugin INSTANCE;
    public MNAOPMixinPlugin() {
        INSTANCE = this;
        LOGGER.info("mixin loaded");
    }
    public static boolean isConstructTaskDenied(String path) {
        return INSTANCE.CFG.DeniedConstructTasks.contains(path);
    }
    public static boolean isPeripheralDenied(String className) {
        return INSTANCE.CFG.DeniedCCPeripherals.contains(className);
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
        // by interop check
        for (var modid : modCheckTargets.entrySet())
            if (mixinCls.contains(modid.getKey()) && !classExists(modid.getValue())) {
                LOGGER.warn("Skipped mixin class: {} (mod {} missing)", mixinCls, modid.getKey());
                return false;
            }

        // by blacklist config
        var shouldDeny = false;
        for (var key : CFG.DeniedMixinClasses) {
            if (mixinCls.contains(key)) {
                shouldDeny = true;
                break;
            }
        }
        if (shouldDeny) {
            LOGGER.warn("Denied mixin class: {}", mixinCls);
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

    private boolean classExists(String path) {
        var resourcePath = path.replace('.', '/') + ".class";
        var classLoader = Thread.currentThread().getContextClassLoader();
        var resource = classLoader.getResource(resourcePath);
        return resource != null;
    }
}
