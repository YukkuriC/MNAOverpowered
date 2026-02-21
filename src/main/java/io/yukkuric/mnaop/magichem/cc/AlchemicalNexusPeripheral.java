package io.yukkuric.mnaop.magichem.cc;

import com.aranaira.magichem.block.entity.AlchemicalNexusBlockEntity;
import dan200.computercraft.api.detail.VanillaDetailRegistries;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.GenericPeripheral;
import dan200.computercraft.api.peripheral.IComputerAccess;
import io.yukkuric.mnaop.MNAOPMod;
import io.yukkuric.mnaop.mixin.computercraft.AccessorInventoryMethods;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

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
    private List<ItemStack> _getStageItems(AlchemicalNexusBlockEntity be) {
        var recipe = be.getCurrentRecipe();
        if (recipe == null) return List.of();
        var stage = recipe.getStages(false).get(be.getCraftingStage());
        return stage.componentItems;
    }
    @LuaFunction(mainThread = true)
    public List<Map<String, Object>> getStageItemRequirements(AlchemicalNexusBlockEntity be) {
        var itemList = _getStageItems(be);
        if (itemList.isEmpty()) return List.of();
        return itemList.stream().map(VanillaDetailRegistries.ITEM_STACK::getBasicDetails).toList();
    }
    @LuaFunction(mainThread = true)
    public int fillRequiredItems(AlchemicalNexusBlockEntity be, IComputerAccess computer, String fromName) throws LuaException {
        // pick items
        var targetItems = _getStageItems(be);
        if (targetItems.isEmpty()) return -1;

        // pick inv
        var peri = computer.getAvailablePeripheral(fromName);
        if (peri == null) throw new LuaException("Target '" + fromName + "' does not exist");
        var srcInv = AccessorInventoryMethods.callExtractHandler(peri);
        if (srcInv == null) throw new LuaException("Source '" + fromName + "' is not an inventory");
        int srcSlots = srcInv.getSlots();
        var nexusInv = be.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);

        // each slot
        int ret = 0;
        for (var offset = 0; offset < AlchemicalNexusBlockEntity.SLOT_INPUT_COUNT && offset < targetItems.size(); offset++) {
            int slot = AlchemicalNexusBlockEntity.SLOT_INPUT_START + offset;
            if (!nexusInv.getStackInSlot(slot).isEmpty()) continue;
            var required = targetItems.get(offset).getItem();

            // transfer item
            var transfered = false;
            for (int i = 0; i < srcSlots; i++) {
                var srcItem = srcInv.getStackInSlot(i);
                if (!srcItem.is(required)) continue;
                var pickOne = srcInv.extractItem(i, 1, false);
                if (pickOne.isEmpty()) continue;
                nexusInv.insertItem(slot, pickOne, false);
                transfered = true;
                break;
            }

            // add result
            if (transfered) ret += (1 << offset);
        }
        return ret;
    }
}
