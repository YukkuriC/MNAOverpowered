package io.yukkuric.mnaop.construct;

import com.aranaira.magichem.MagiChemMod;
import com.mna.api.ManaAndArtificeMod;
import io.yukkuric.mnaop.MNAOPHelpers;
import io.yukkuric.mnaop.construct.magichem.TaskExMagiChemRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(
        modid = MagiChemMod.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class TaskExRegistry {
    // vanilla ones here

    @SubscribeEvent
    public static void registerTasks(RegisterEvent event) {
        event.register(ManaAndArtificeMod.getConstructTaskRegistry().getRegistryKey(), (helper) -> {
            // vanilla ones

            // magichem ones
            if (MNAOPHelpers.IsMagiChemLoaded())
                TaskExMagiChemRegistry.registerTasks(helper);
        });
    }
}
