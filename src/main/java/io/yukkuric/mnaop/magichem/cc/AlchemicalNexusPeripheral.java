package io.yukkuric.mnaop.magichem.cc;

import com.aranaira.magichem.block.entity.AlchemicalNexusBlockEntity;
import com.aranaira.magichem.block.entity.routers.AlchemicalNexusRouterBlockEntity;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.GenericPeripheral;
import io.yukkuric.mnaop.MNAOPMod;

import java.util.List;

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
    public List<Integer> getStage(AlchemicalNexusRouterBlockEntity be) throws LuaException {
        var master = be.getMaster();
        if (master == null) throw new LuaException("Router missing master");
        return getStage(master);
    }
}
