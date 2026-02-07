package io.yukkuric.mnaop.construct;

import com.mna.api.ManaAndArtificeMod;
import com.mna.api.entities.construct.ai.ConstructTask;
import io.yukkuric.mnaop.MNAOPHelpers;
import io.yukkuric.mnaop.MNAOPMod;
import io.yukkuric.mnaop.construct.magichem.TaskExMagiChemRegistry;
import io.yukkuric.mnaop.mixin_interface.MNAOPMixinPlugin;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

import java.util.function.BiConsumer;

import static io.yukkuric.mnaop.MNAOPHelpers.modLoc;

@Mod.EventBusSubscriber(
        modid = MNAOPMod.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class TaskExRegistry {
    // vanilla ones here

    @SubscribeEvent
    public static void registerTasks(RegisterEvent event) {
        event.register(ManaAndArtificeMod.getConstructTaskRegistry().getRegistryKey(), (helper) -> {
            BiConsumer<String, ConstructTask> doRegister = (String id, ConstructTask task) -> {
                if (MNAOPMixinPlugin.isConstructTaskDenied(id)) {
                    MNAOPMod.LOGGER.warn("Denied construct task: {}", id);
                    return;
                }
                helper.register(modLoc(id), task);
            };

            // vanilla ones

            // magichem ones
            if (MNAOPHelpers.IsMagiChemLoaded())
                TaskExMagiChemRegistry.registerTasks(doRegister);
        });
    }
}
