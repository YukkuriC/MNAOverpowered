package io.yukkuric.mnaop.mixin_interface;

import io.yukkuric.mnaop.MNAOPMod;
import net.minecraft.util.StringUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class MNAOPMixinConfigFile {
    static final String CONFIG_PATH = "config/mnaoverpowered-mixin.ini";
    static final String KEY_DeniedMixinClasses = "DeniedMixinClasses";
    static final String KEY_DeniedConstructTasks = "DeniedConstructTasks";

    final Path configPath = Path.of(CONFIG_PATH);
    final Set<String> DeniedMixinClasses = new HashSet<>();
    final Set<String> DeniedConstructTasks = new HashSet<>();
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
            case KEY_DeniedMixinClasses -> dumpSet(val, DeniedMixinClasses);
            case KEY_DeniedConstructTasks -> dumpSet(val, DeniedConstructTasks);
        }
    }

    void load() {
        tryRun(this::loadInner, "read");
    }
    void save() {
        tryRun(this::saveInner, "writ");
    }

    interface RunnableWithError {
        void run() throws Throwable;
    }
    private void tryRun(RunnableWithError task, String name) {
        try {
            task.run();
        } catch (Throwable e) {
            MNAOPMod.LOGGER.error("Error when {}ing mixin config: {}", name, e.getLocalizedMessage());
            try (var sw = new StringWriter()) {
                try (var writer = new PrintWriter(sw)) {
                    e.printStackTrace(writer);
                    MNAOPMod.LOGGER.error(sw.toString());
                }
            } catch (Throwable ignored) {
            }
        }
    }
    private void loadInner() throws IOException {
        if (!Files.exists(configPath)) return;
        var lines = Files.readString(configPath, StandardCharsets.UTF_8).split("\n");
        for (var line : lines) {
            if (line.startsWith("#")) continue;
            var raw = line.split("=", -1);
            if (raw.length != 2) {
                MNAOPMod.LOGGER.error("Error when reading mixin config line: need exactly 1 '='\nline: " + line);
                continue;
            }
            processConfigLine(raw[0].trim(), raw[1].trim());
        }
    }
    private void saveInner() throws IOException {
        var sb = new StringBuilder();
        sb.append("# mixin classes that will be ignored before load; input their names separated by comma\n");
        sb.append("# e.g. MixinCastingResource, MixinInstantConstructShlorper\n");
        sb.append(String.format("%s=%s\n", KEY_DeniedMixinClasses, String.join(", ", DeniedMixinClasses)));
        sb.append("# construct tasks that won't be registered; input their registry IDs (without namespace) separated by comma\n");
        sb.append("# e.g. batch_provide_materia\n");
        sb.append(String.format("%s=%s\n", KEY_DeniedConstructTasks, String.join(", ", DeniedConstructTasks)));
        Files.writeString(configPath, sb.toString(), StandardCharsets.UTF_8);
    }
}
