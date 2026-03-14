package io.yukkuric.mnaop.ritual;

import com.mna.api.capabilities.IWellspringNodeRegistry;
import com.mna.api.config.GeneralConfigValues;
import com.mna.api.rituals.IRitualContext;
import com.mna.api.rituals.RitualEffect;
import com.mna.capabilities.worlddata.WorldMagicProvider;
import com.mna.effects.EffectInit;
import com.mna.items.ItemInit;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.item.ItemEntity;

public abstract class RitualEffectChangeWellspring extends RitualEffect implements IRitualHelpers {
    final boolean isDelete;
    public RitualEffectChangeWellspring(ResourceLocation ritualName, boolean shouldWellspringExist) {
        super(ritualName);
        this.isDelete = shouldWellspringExist;
    }

    static final Component MSG_CREATE_MISS = Component.translatable("mna.commands.wellspring.create.nodeoverlap");
    static final Component MSG_CREATE_MISS_UNKNOWN = Component.translatable("mna.commands.wellspring.create.failed");
    static final Component MSG_DESTROY_MISS = Component.translatable("mna.commands.wellspring.delete.failed");

    Component unknownFail() {
        return isDelete ? MSG_DESTROY_MISS : MSG_CREATE_MISS_UNKNOWN;
    }
    Component knownFail() {
        return isDelete ? MSG_DESTROY_MISS : MSG_CREATE_MISS;
    }

    Component failMsg = null;
    synchronized void generalCheckAndApply(IRitualContext context, boolean apply) {
        failMsg = null; // reset msg

        var pos = context.getCenter();
        var wm = context.getLevel().getCapability(WorldMagicProvider.MAGIC).orElse(null);
        if (wm == null) {// no system at all, should not happen
            failMsg = unknownFail();
            if (apply) tryReturnItems(context);
            return;
        }

        // check overlap spring
        var springs = wm.getWellspringRegistry();
        var hasSpringHere = springs.getNodeAt(pos).isPresent();
        if (hasSpringHere != isDelete) {
            failMsg = knownFail();
            if (apply) tryReturnItems(context);
            return;
        }

        // really do effect
        if (apply) {
            var ret = applyEffect(context, springs);
            if (!ret) { // srsly? why
                failMsg = unknownFail();
                tryReturnItems(context);
            }
        }
    }
    abstract boolean applyEffect(IRitualContext context, IWellspringNodeRegistry nodes);

    public boolean applyStartCheckInCreative() {
        return true;
    }
    public synchronized Component canRitualStart(IRitualContext context) {
        generalCheckAndApply(context, false);
        return failMsg;
    }

    @Override
    protected synchronized boolean applyRitualEffect(IRitualContext context) {
        generalCheckAndApply(context, true);

        // temp effect to see newly created spring
        context.getCaster().addEffect(new MobEffectInstance(EffectInit.WELLSPRING_SIGHT.get(), 1200, 0));
        return failMsg == null;
    }
    @Override
    protected int getApplicationTicks(IRitualContext context) {
        return 20;
    }

    protected void tryReturnItems(IRitualContext context) {
        giveItemsAtCenter(context, context.getCollectedReagents());
        context.getCaster().sendSystemMessage(failMsg);
        var center = context.getCenter().getCenter();
        var level = context.getLevel();
        for (var stack : context.getCollectedReagents()) {
            var entity = new ItemEntity(level, center.x, center.y, center.z, stack);
            level.addFreshEntity(entity);
        }
    }

    public static class Create extends RitualEffectChangeWellspring {
        public Create(ResourceLocation ritualName) {
            super(ritualName, false);
        }
        @Override
        boolean applyEffect(IRitualContext context, IWellspringNodeRegistry nodes) {
            // hack: temp disable range check
            var oldDist = GeneralConfigValues.WellspringDistance;
            GeneralConfigValues.WellspringDistance = 0;
            var ret = nodes.addRandomNode(context.getLevel(), context.getCenter());
            GeneralConfigValues.WellspringDistance = oldDist;

            return ret;
        }
    }
    public static class Destroy extends RitualEffectChangeWellspring {
        public Destroy(ResourceLocation ritualName) {
            super(ritualName, true);
        }
        @Override
        boolean applyEffect(IRitualContext context, IWellspringNodeRegistry nodes) {

            var ret = nodes.deleteNodeAt(context.getCenter().atY(0));

            // manually return the tunnel
            if (ret) {
                giveItemsAtCenter(context, ItemInit.TRANSITORY_TUNNEL.get().getDefaultInstance());
            }

            return ret;
        }
    }
}
