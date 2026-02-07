package io.yukkuric.mnaop.mixin_interface;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import io.yukkuric.mnaop.MNAOPMod;
import net.minecraft.util.StringUtil;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class MNAOPMixinPlugin implements IMixinConfigPlugin {
    static final String CONFIG_PATH = "config/mnaoverpowered-mixin.ini";
    static final String KEY_DENIED_MIXINS = "DeniedMixinClasses";
    static final String KEY_DENIED_CONSTRUCT_TASKS = "DeniedConstructTasks";

    public static MNAOPMixinPlugin INSTANCE;
    public MNAOPMixinPlugin() {
        INSTANCE = this;
    }
    public static boolean isConstructTaskDenied(String path) {
        return INSTANCE.deniedConstructTasks.contains(path);
    }

    final Set<String> deniedMixinClasses = new HashSet<>();
    final Set<String> deniedConstructTasks = new HashSet<>();
    void dumpSet(String raw, Set<String> target) {
        target.clear();
        for (var sub : raw.split(",")) {
            sub = sub.trim();
            if (StringUtil.isNullOrEmpty(sub)) continue;
            target.add(sub);
        }
    }
    void processConfigLine(String key, String val) {
        switch (key) {
            case KEY_DENIED_MIXINS -> dumpSet(val, deniedMixinClasses);
            case KEY_DENIED_CONSTRUCT_TASKS -> dumpSet(val, deniedConstructTasks);
        }
    }
    @Override
    public void onLoad(String s) {
        MixinExtrasBootstrap.init(); // do NOT forget this using MixinExtras

        // try load config file
        var configPath = Path.of(CONFIG_PATH);
        if (Files.exists(configPath)) {
            try {
                var lines = Files.readString(configPath, StandardCharsets.UTF_8).split("\n");
                for (var line : lines) {
                    if (line.startsWith("#")) continue;
                    var raw = line.split("=");
                    if (raw.length != 2) {
                        MNAOPMod.LOGGER.error("Error when reading mixin config line: need exactly 1 '='\nline: " + line);
                        continue;
                    }
                    processConfigLine(raw[0].trim(), raw[1].trim());
                }
            } catch (Throwable e) {
                MNAOPMod.LOGGER.error("Error when reading mixin config: {}", e.getLocalizedMessage());
                try (var sw = new StringWriter()) {
                    try (var writer = new PrintWriter(sw)) {
                        e.printStackTrace(writer);
                        MNAOPMod.LOGGER.error(sw.toString());
                    }
                } catch (Throwable ignored) {
                }
            }
        }

        // try create config file
        try {
            var sb = new StringBuilder();
            sb.append("# mixin classes that will be ignored before load; input their names separated by comma\n");
            sb.append("# e.g. MixinCastingResource, MixinInstantConstructShlorper\n");
            sb.append(String.format("%s=%s\n", KEY_DENIED_MIXINS, String.join(", ", deniedMixinClasses)));
            sb.append("# construct tasks that won't be registered; input their registry IDs (without namespace) separated by comma\n");
            sb.append("# e.g. batch_provide_materia\n");
            sb.append(String.format("%s=%s\n", KEY_DENIED_CONSTRUCT_TASKS, String.join(", ", deniedConstructTasks)));
            Files.writeString(configPath, sb.toString(), StandardCharsets.UTF_8);
        } catch (Throwable e) {
            MNAOPMod.LOGGER.error("Error when writing mixin config: {}", e.getLocalizedMessage());
            try (var sw = new StringWriter()) {
                try (var writer = new PrintWriter(sw)) {
                    e.printStackTrace(writer);
                    MNAOPMod.LOGGER.error(sw.toString());
                }
            } catch (Throwable ignored) {
            }
        }
    }
    @Override
    public String getRefMapperConfig() {
        return null;
    }
    @Override
    public boolean shouldApplyMixin(String targetCls, String mixinCls) {
        var raw = mixinCls.split("\\.");
        var mixinClsName = raw[raw.length - 1];
        if (deniedMixinClasses.contains(mixinClsName)) {
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
