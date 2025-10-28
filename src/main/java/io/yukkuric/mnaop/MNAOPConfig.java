package io.yukkuric.mnaop;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = MNAOPMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MNAOPConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // entries
    private static final ForgeConfigSpec.BooleanValue Cfg_FastRegenManaIgnoresSaturation = BUILDER.comment("Whether to log the dirt block on common setup").define("FastRegenManaIgnoresSaturation", true);
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
