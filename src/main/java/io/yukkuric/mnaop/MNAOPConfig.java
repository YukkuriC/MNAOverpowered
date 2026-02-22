package io.yukkuric.mnaop;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MNAOPMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MNAOPConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // entries
    public static ForgeConfigSpec.BooleanValue
            Cfg_FastRegenManaIgnoresSaturation,
            Cfg_NonDiminishingEldrinMatrix,
            Cfg_EmpoweredEldrinMatrix,
            Cfg_RuneForgeHybridCrystalUpgrade,
            Cfg_CodexArcanaShiftQuery,
            Cfg_UnlimitedOrrery,
            Cfg_InstantConstructShlorps,
            Cfg_ShowReadableAdmixtureFormula,
            Cfg_ShowDistillationResults;
    public static ForgeConfigSpec.DoubleValue
            Cfg_ConstructMilkingCooldown,
            Cfg_NaturalWellspringMinStrength,
            Cfg_NaturalWellspringMaxStrength,
            Cfg_CirclePowerStackMultRatio;
    public static ForgeConfigSpec.EnumValue
            Cfg_EnablesLocateWellspringCommand;

    static {
        Cfg_FastRegenManaIgnoresSaturation = BUILDER.comment("Speed-up casting resource regeneration, regardless of player's food saturation").define("FastRegenManaIgnoresSaturation", true);
        Cfg_ConstructMilkingCooldown = BUILDER.comment("Configurable construct's milking cooldown (in minutes);\nset to values < 0 to disable this feature;\nthe real cooldown will be a random value between 1-2 times of this set value").defineInRange("ConstructMilkingCooldown", 3, Double.MIN_VALUE, Double.MAX_VALUE);
        Cfg_NaturalWellspringMinStrength = BUILDER.comment("Minimum strength for natural random wellsprings").defineInRange("NaturalWellspringMinStrength", 5, 0, Double.MAX_VALUE);
        Cfg_NaturalWellspringMaxStrength = BUILDER.comment("Maximum strength for natural random wellsprings").defineInRange("NaturalWellspringMaxStrength", 25, 0, Double.MAX_VALUE);
        if (MNAOPMod.ConfigGroupActive("Blocks")) {
            BUILDER.push("Blocks");
            Cfg_NonDiminishingEldrinMatrix = BUILDER.comment("Eldrin Matrices no more diminish when placed more than 1").define("NonDiminishingEldrinMatrix", true);
            Cfg_EmpoweredEldrinMatrix = BUILDER.comment("Eldrin Matrices will receive multiplier from Eldrin Wellsprings").define("EmpoweredEldrinMatrix", true);
            Cfg_RuneForgeHybridCrystalUpgrade = BUILDER.comment("Allowing Rune Forges to enable multiple upgrades at once; note: still need a pair of pedestals and crystals to work").define("RuneForgeHybridCrystalUpgrade", true);
            BUILDER.pop();
        }
        if (MNAOPMod.ConfigGroupActive("Display")) {
            BUILDER.push("Display");
            Cfg_CodexArcanaShiftQuery = BUILDER.comment("Shift-right click blocks using Codex Arcana to open related entry (like what Patchouli does)").define("CodexArcanaShiftQuery", true);
            BUILDER.pop();
        }
        if (MNAOPMod.ConfigGroupActive("Commands")) {
            BUILDER.push("Commands");
            Cfg_EnablesLocateWellspringCommand = BUILDER.comment("Whether this command is available to normal players or OPs").defineEnum("EnablesLocateWellspringCommand", MNAOPEnums.CommandStatus.OP_ONLY);
            BUILDER.pop();
        }
        if (MNAOPMod.ConfigGroupActive("MagiChem")) {
            BUILDER.push("MagiChem");
            Cfg_UnlimitedOrrery = BUILDER.comment("Unlocks the limit that a player can only have 1 orrery").define("UnlimitedOrrery", true);
            Cfg_InstantConstructShlorps = BUILDER.comment("Constructs with advanced transport ability moves materia shlorps as fast as max-powered labyrinth navigators").define("InstantConstructShlorps", true);
            Cfg_CirclePowerStackMultRatio = BUILDER.comment("Circle of Power vertically stacked on another gets its output rate multiplied by (this value * bottom output rate / base maxa output rate)").defineInRange("CirclePowerStackMultRatio", 10, 1, Double.MAX_VALUE);
            Cfg_ShowReadableAdmixtureFormula = BUILDER.comment("Displays each admixture's translated formula and overall essentia value").define("ShowReadableAdmixtureFormula", true);
            Cfg_ShowDistillationResults = BUILDER.comment("Displays distillation contents for all items supported").define("ShowDistillationResults", true);
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
    public static boolean RuneForgeHybridCrystalUpgrade() {
        return Cfg_RuneForgeHybridCrystalUpgrade.get();
    }
    public static boolean CodexArcanaShiftQuery() {
        return Cfg_CodexArcanaShiftQuery.get();
    }
    public static MNAOPEnums.CommandStatus EnablesLocateWellspringCommand() {
        return (MNAOPEnums.CommandStatus) Cfg_EnablesLocateWellspringCommand.get();
    }
    public static boolean UnlimitedOrrery() {
        return Cfg_UnlimitedOrrery.get();
    }
    public static boolean InstantConstructShlorps() {
        return Cfg_InstantConstructShlorps.get();
    }
    public static double CirclePowerStackMultRatio() {
        return Cfg_CirclePowerStackMultRatio.get();
    }
    public static boolean ShowReadableAdmixtureFormula() {
        return Cfg_ShowReadableAdmixtureFormula.get();
    }
    public static boolean ShowDistillationResults() {
        return Cfg_ShowDistillationResults.get();
    }
}
