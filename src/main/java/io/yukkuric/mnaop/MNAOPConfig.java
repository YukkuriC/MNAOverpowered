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
            Cfg_InstantConstructShlorps = BUILDER.comment("Constructs with advanced transport ability moves materia shlorps as fast as max-powered labyrinth navigators").define("InstantConstructShlorps", true);
            BUILDER.pop();
        }
    }

    static final ForgeConfigSpec SPEC = BUILDER.build();

    // interfaces
    public static boolean FastRegenManaIgnoresSaturation() {
        return Cfg_FastRegenManaIgnoresSaturation.get();
    }
    public static boolean NonDiminishingEldrinMatrix() {
        return Cfg_NonDiminishingEldrinMatrix.get();
    }
    public static boolean EmpoweredEldrinMatrix() {
        return Cfg_EmpoweredEldrinMatrix.get();
    }
    public static double ConstructMilkingCooldown() {
        return Cfg_ConstructMilkingCooldown.get();
    }
    public static double NaturalWellspringMinStrength() {
        return Cfg_NaturalWellspringMinStrength.get();
    }
    public static double NaturalWellspringMaxStrength() {
        return Cfg_NaturalWellspringMaxStrength.get();
    }
    public static boolean UnlimitedOrrery() {
        return Cfg_UnlimitedOrrery.get();
    }
    public static boolean InstantConstructShlorps() {
        return Cfg_InstantConstructShlorps.get();
    }
}
