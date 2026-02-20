package io.yukkuric.mnaop.magichem.cc;

import com.aranaira.magichem.block.entity.AlchemicalNexusBlockEntity;
import dan200.computercraft.api.detail.VanillaDetailRegistries;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.GenericPeripheral;
import io.yukkuric.mnaop.MNAOPMod;

import java.util.List;
import java.util.Map;

public class AlchemicalNexusPeripheral implements GenericPeripheral {
    public static final String ID = MNAOPMod.MODID + ":alchemical_nexus";
    public static AlchemicalNexusPeripheral INSTANCE = new AlchemicalNexusPeripheral();

    @Override
    public String id() {
        return ID;
    }

    // stages
    @LuaFunction(mainThread = true)
    public List<Integer> getStage(AlchemicalNexusBlockEntity be) {
        return List.of(be.getCraftingStage(), be.getAnimStage());
    }
    @LuaFunction(mainThread = true)
    public int getTotalStages(AlchemicalNexusBlockEntity be) {
        var recipe = be.getCurrentRecipe();
        if (recipe == null) return 0;
        return recipe.getStages(false).size();
    }

    // item requirements & supply
    @LuaFunction(mainThread = true)
    public List<Map<String, Object>> getStageItemRequirements(AlchemicalNexusBlockEntity be) {
        var recipe = be.getCurrentRecipe();
        if (recipe == null) return List.of();
        var stage = recipe.getStages(false).get(be.getCraftingStage());
        return stage.componentItems.stream().map(VanillaDetailRegistries.ITEM_STACK::getBasicDetails).toList();
    }
    // TODO auto supply
}
