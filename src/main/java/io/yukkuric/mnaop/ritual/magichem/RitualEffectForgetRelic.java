package io.yukkuric.mnaop.ritual.magichem;

import com.aranaira.magichem.entities.ShlorpEntity;
import com.aranaira.magichem.foundation.enums.ShlorpParticleMode;
import com.aranaira.magichem.item.MateriaItem;
import com.aranaira.magichem.registry.EntitiesRegistry;
import com.aranaira.magichem.util.InventoryHelper;
import com.mna.api.rituals.IRitualContext;
import com.mna.api.rituals.RitualEffect;
import com.mna.tools.math.Vector3;
import io.yukkuric.mnaop.ritual.IRitualHelpers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class RitualEffectForgetRelic extends RitualEffect implements IRitualHelpers {
    public RitualEffectForgetRelic(ResourceLocation ritualName) {
        super(ritualName);
    }

    static final Component FAIL_MSG = Component.translatable("mnaop.ritual.msg.forget_relic.fail");
    Advancement theTarget(IRitualContext context) {
        var level = (ServerLevel) context.getLevel();
        return level.getServer().getAdvancements().getAdvancement(ResourceLocation.parse("magichem:craft_a_relic"));
    }
    AdvancementProgress theProgress(IRitualContext context) {
        var player = (ServerPlayer) context.getCaster();
        var advManagerPlayer = player.getAdvancements();
        return advManagerPlayer.getOrStartProgress(theTarget(context));
    }

    public synchronized Component canRitualStart(IRitualContext context) {
        var progress = theProgress(context);
        if (progress.isDone()) return null;
        return FAIL_MSG;
    }

    @Override
    protected boolean applyRitualEffect(IRitualContext context) {
        // revoke advancement
        var player = (ServerPlayer) context.getCaster();
        var advManagerPlayer = player.getAdvancements();
        var advancement = theTarget(context);
        { // AdvancementCommands REVOKE
            var progress = advManagerPlayer.getOrStartProgress(advancement);
            if (progress.hasProgress()) {
                for (var crit : progress.getCompletedCriteria()) {
                    advManagerPlayer.revoke(advancement, crit);
                }
            }
        }

        // summon shlorps
        var level = context.getLevel();
        var center = context.getCenter();
        var centerVec = Vec3.atLowerCornerWithOffset(center, 0.5, 0, 0.5);
        for (var radius = 2; radius <= 4; radius += 2) {
            for (var dx = -1; dx <= 1; dx += 2) {
                for (var dz = -1; dz <= 1; dz += 2) {
                    var src = center.offset(radius * dx, 0, radius * dz);
                    var srcVec = Vec3.atLowerCornerWithOffset(src, 0.5, 1, 0.5);
                    shlorpTo(level, srcVec, centerVec, "legend", 1);
                }
            }
        }
        shlorpTo(level, player.getEyePosition(), centerVec, "color", 3);
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 20, 1));

        // return bottles used in reagents
        int bottleCount = 0;
        for (var stack : context.getCollectedReagents()) {
            if (stack.getItem() instanceof MateriaItem && !InventoryHelper.hasCustomModelData(stack))
                bottleCount += stack.getCount();
        }
        if (bottleCount > 0) giveItemsAtCenter(context, new ItemStack(Items.GLASS_BOTTLE, bottleCount));

        return true;
    }
    @Override
    protected int getApplicationTicks(IRitualContext context) {
        return 20;
    }

    void shlorpTo(Level level, Vec3 src, Vec3 dst, String materiaId, float height) {
        MateriaItem materia = (MateriaItem) BuiltInRegistries.ITEM.get(ResourceLocation.parse("magichem:admixture_" + materiaId));

        var shlorp = new ShlorpEntity(EntitiesRegistry.SHLORP_ENTITY.get(), level);
        shlorp.setPos(dst);
        shlorp.configure(
                // Vector3 pStartLocation, Vector3 pStartOrigin, Vector3 pStartTangent
                new Vector3(src), Vector3.zero(), new Vector3(Math.random() * 2 - 1, Math.random() * height + height, Math.random() * 2 - 1),
                // Vector3 pEndLocation, Vector3 pEndOrigin, Vector3 pEndTangent
                new Vector3(dst), Vector3.zero(), new Vector3(Math.random() * 2 - 1, Math.random() * height + height, Math.random() * 2 - 1),
                // float pSpeed, float pDistanceBetweenClusters, int pClusterCount
                0.3f, 0.125f, 60,
                // MateriaItem pMateriaType, int pMateriaCount
                materia, 1,
                // ShlorpParticleMode pParticleMode
                ShlorpParticleMode.INVERSE_ENTRY_TANGENT
        );
        level.addFreshEntity(shlorp);
    }
}
