package io.yukkuric.mnaop.event;

import com.mna.api.faction.FactionIDs;
import com.mna.capabilities.playerdata.progression.PlayerProgressionProvider;
import io.yukkuric.mnaop.MNAOPConfig;
import io.yukkuric.mnaop.MNAOPMod;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MNAOPMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MNAOPEntityEvents {
    @SubscribeEvent
    static void onEntityChangeTarget(LivingChangeTargetEvent event) {
        var entity = event.getEntity();

        // undead inner neutral
        if (MNAOPConfig.UndeadFactionMobFriendly()
                && entity.getMobType() == MobType.UNDEAD
                && !entity.getType().is(Tags.EntityTypes.BOSSES)
                && event.getOriginalTarget() instanceof Player player) {
            player.getCapability(PlayerProgressionProvider.PROGRESSION).ifPresent((prog) -> {
                var faction = prog.getAlliedFaction();
                if (faction == null || !faction.is(FactionIDs.UNDEAD)) return;
                event.setNewTarget(null);
                event.setCanceled(true);
            });
        }
    }
}
