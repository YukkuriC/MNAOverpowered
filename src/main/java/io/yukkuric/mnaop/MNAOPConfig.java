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
            Cfg_FastRegenManaIgnoresSaturation,
            Cfg_NonDiminishingEldrinMatrix,
            Cfg_EmpoweredEldrinMatrix,
            Cfg_UnlimitedOrrery,
            Cfg_InstantConstructShlorps;
    public static ForgeConfigSpec.DoubleValue
            Cfg_ConstructMilkingCooldown,
            Cfg_NaturalWellspringMinStrength,
            Cfg_NaturalWellspringMaxStrength;

    static {
        Cfg_FastRegenManaIgnoresSaturation = BUILDER.comment("Speed-up casting resource regeneration, regardless of player's food saturation").define("FastRegenManaIgnoresSaturation", true);
        Cfg_NonDiminishingEldrinMatrix = BUILDER.comment("Eldrin Matrices no more diminish when placed more than 1").define("NonDiminishingEldrinMatrix", true);
        Cfg_EmpoweredEldrinMatrix = BUILDER.comment("Eldrin Matrices will receive multiplier from Eldrin Wellsprings").define("EmpoweredEldrinMatrix", true);
        Cfg_ConstructMilkingCooldown = BUILDER.comment("Configurable construct's milking cooldown (in minutes);\nset to values < 0 to disable this feature;\nthe real cooldown will be a random value between 1-2 times of this set value").defineInRange("ConstructMilkingCooldown", 3, Double.MIN_VALUE, Double.MAX_VALUE);
        Cfg_NaturalWellspringMinStrength = BUILDER.comment("Minimum strength for natural random wellsprings").defineInRange("NaturalWellspringMinStrength", 5, 0, Double.MAX_VALUE);
        Cfg_NaturalWellspringMaxStrength = BUILDER.comment("Maximum strength for natural random wellsprings").defineInRange("NaturalWellspringMaxStrength", 25, 0, Double.MAX_VALUE);
        if (MNAOPMod.ConfigGroupActive("MagiChem")) {
            BUILDER.push("MagiChem");
            Cfg_UnlimitedOrrery = BUILDER.comment("Unlocks the limit that a player can only have 1 orrery").define("UnlimitedOrrery", true);
            Cfg_InstantConstructShlorps = BUILDER.comment("Constructs with advanced transport ability moves materia shlorps as fast as max-powered labyrinth routers").define("InstantConstructShlorps", true);
            BUILDER.pop();
        }
    }

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
    public static double NaturalWellspringMinStrength() {
        return cache_NaturalWellspringMinStrength;
    }
    public static double NaturalWellspringMaxStrength() {
        return cache_NaturalWellspringMaxStrength;
    }
    public static boolean UnlimitedOrrery() {
        return cache_UnlimitedOrrery;
    }
    public static boolean InstantConstructShlorps() {
        return cache_InstantConstructShlorps;
    }

    // caches
    private static boolean
            cache_FastRegenManaIgnoresSaturation,
            cache_NonDiminishingEldrinMatrix,
            cache_EmpoweredEldrinMatrix,
            cache_UnlimitedOrrery,
            cache_InstantConstructShlorps;
    private static double
            cache_ConstructMilkingCooldown,
            cache_NaturalWellspringMinStrength,
            cache_NaturalWellspringMaxStrength;
    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        // cache values according to template
        cache_FastRegenManaIgnoresSaturation = Cfg_FastRegenManaIgnoresSaturation.get();
        cache_NonDiminishingEldrinMatrix = Cfg_NonDiminishingEldrinMatrix.get();
        cache_EmpoweredEldrinMatrix = Cfg_EmpoweredEldrinMatrix.get();
        cache_ConstructMilkingCooldown = Cfg_ConstructMilkingCooldown.get();
        cache_NaturalWellspringMinStrength = Cfg_NaturalWellspringMinStrength.get();
        cache_NaturalWellspringMaxStrength = Cfg_NaturalWellspringMaxStrength.get();
        cache_UnlimitedOrrery = Cfg_UnlimitedOrrery.get();
        cache_InstantConstructShlorps = Cfg_InstantConstructShlorps.get();
    }
}
