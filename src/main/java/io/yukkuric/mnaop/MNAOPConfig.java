package io.yukkuric.mnaop;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = MNAOPMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MNAOPConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // entries
    public ForgeConfigSpec.BooleanValue
            Cfg_FastRegenManaIgnoresSaturation = BUILDER.comment("Speed-up casting resource regeneration, regardless of player's food saturation").define("FastRegenManaIgnoresSaturation", true);
    static final ForgeConfigSpec SPEC = BUILDER.build();

    // interfaces
    public static boolean FastRegenManaIgnoresSaturation() {
        return cache_FastRegenManaIgnoresSaturation;
    }

    // caches
    private static boolean cache_FastRegenManaIgnoresSaturation;
    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        // cache values according to template
        cache_FastRegenManaIgnoresSaturation = Cfg_FastRegenManaIgnoresSaturation.get();
    }
}
