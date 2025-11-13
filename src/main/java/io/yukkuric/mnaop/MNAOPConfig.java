package io.yukkuric.mnaop;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = MNAOPMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MNAOPConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // entries
    public static ForgeConfigSpec.BooleanValue
            Cfg_FastRegenManaIgnoresSaturation = BUILDER.comment("Speed-up casting resource regeneration, regardless of player's food saturation").define("FastRegenManaIgnoresSaturation", true),
            Cfg_NonDiminishingEldrinMatrix = BUILDER.comment("Eldrin Matrices no more diminish when placed more than 1").define("NonDiminishingEldrinMatrix", true),
            Cfg_EmpoweredEldrinMatrix = BUILDER.comment("Eldrin Matrices will receive multiplier from Eldrin Wellsprings").define("EmpoweredEldrinMatrix", true);
    public static ForgeConfigSpec.DoubleValue
            Cfg_ConstructMilkingCooldown = BUILDER.comment("Configurable construct's milking cooldown (in minutes);\nset to values < 0 to disable this feature;\nthe real cooldown will be a random value between 1-2 times of this set value").defineInRange("ConstructMilkingCooldown", 3, Double.MIN_VALUE, Double.MAX_VALUE);
    static final ForgeConfigSpec SPEC = BUILDER.build();

    // interfaces
    public static boolean FastRegenManaIgnoresSaturation() {
        return cache_FastRegenManaIgnoresSaturation;
    }
    public static boolean NonDiminishingEldrinMatrix() {
        return cache_NonDiminishingEldrinMatrix;
    }
    public static boolean EmpoweredEldrinMatrix() {
        return cache_EmpoweredEldrinMatrix;
    }
    public static double ConstructMilkingCooldown() {
        return cache_ConstructMilkingCooldown;
    }

    // caches
    private static boolean cache_FastRegenManaIgnoresSaturation, cache_NonDiminishingEldrinMatrix, cache_EmpoweredEldrinMatrix;
    private static double cache_ConstructMilkingCooldown;
    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        // cache values according to template
        cache_FastRegenManaIgnoresSaturation = Cfg_FastRegenManaIgnoresSaturation.get();
        cache_NonDiminishingEldrinMatrix = Cfg_NonDiminishingEldrinMatrix.get();
        cache_EmpoweredEldrinMatrix = Cfg_EmpoweredEldrinMatrix.get();
        cache_ConstructMilkingCooldown = Cfg_ConstructMilkingCooldown.get();
    }
}
