package io.yukkuric.mnaop.magichem.cc;

import com.aranaira.magichem.block.entity.AlchemicalNexusBlockEntity;
import com.aranaira.magichem.block.entity.routers.AlchemicalNexusRouterBlockEntity;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.GenericPeripheral;
import dan200.computercraft.api.peripheral.IComputerAccess;
import io.yukkuric.mnaop.MNAOPMod;

import java.util.List;
import java.util.Map;

public class AlchemicalNexusRouterPeripheral implements GenericPeripheral {
    public static final String ID = MNAOPMod.MODID + ":alchemical_nexus_router";
    public static AlchemicalNexusRouterPeripheral INSTANCE = new AlchemicalNexusRouterPeripheral();
    public static AlchemicalNexusPeripheral MASTER = AlchemicalNexusPeripheral.INSTANCE;

    @Override
    public String id() {
        return ID;
    }

    // general get master
    AlchemicalNexusBlockEntity master(AlchemicalNexusRouterBlockEntity router) throws LuaException {
        var master = router.getMaster();
        if (master == null) throw new LuaException("Router missing master");
        return master;
    }

    // stages
    @LuaFunction(mainThread = true)
    public List<Integer> getStage(AlchemicalNexusRouterBlockEntity be) throws LuaException {
        return MASTER.getStage(master(be));
    }
    @LuaFunction(mainThread = true)
    public int getTotalStages(AlchemicalNexusRouterBlockEntity be) throws LuaException {
        return MASTER.getTotalStages(master(be));
    }

    // item requirements & supply
    @LuaFunction(mainThread = true)
    public List<Map<String, Object>> getStageItemRequirements(AlchemicalNexusRouterBlockEntity be) throws LuaException {
        return MASTER.getStageItemRequirements(master(be));
    }
    @LuaFunction(mainThread = true)
    public int fillRequiredItems(AlchemicalNexusRouterBlockEntity be, IComputerAccess computer, String fromName) throws LuaException {
        return MASTER.fillRequiredItems(master(be), computer, fromName);
    }
}
