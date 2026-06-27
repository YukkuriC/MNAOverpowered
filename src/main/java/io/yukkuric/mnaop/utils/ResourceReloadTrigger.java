package io.yukkuric.mnaop.utils;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

// why do I have to do so much for a simple resource-reload listener
public class ResourceReloadTrigger extends SimplePreparableReloadListener<Object> {
    Runnable action;
    public ResourceReloadTrigger(Runnable action) {
        this.action = action;
    }
    @Override
    protected Object prepare(ResourceManager rm, ProfilerFiller pf) {
        return this;
    }
    @Override
    protected void apply(Object o, ResourceManager rm, ProfilerFiller pf) {
        action.run();
    }
}
