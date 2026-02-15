package io.yukkuric.mnaop.magichem.cc;

import com.aranaira.magichem.block.entity.AlchemicalNexusBlockEntity;
import com.aranaira.magichem.block.entity.routers.AlchemicalNexusRouterBlockEntity;
import com.aranaira.magichem.foundation.IHasDeviceRecipeSlot;
import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.detail.VanillaDetailRegistries;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.GenericPeripheral;
import io.yukkuric.mnaop.MNAOPMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class AlchemicalRecipePeripheral implements GenericPeripheral {
    public static final String ID = MNAOPMod.MODID + ":recipe_holder";
    public static AlchemicalRecipePeripheral INSTANCE = new AlchemicalRecipePeripheral();
    private static FakePlayer workerSetRecipe;

    @Override
    public String id() {
        return ID;
    }

    @LuaFunction(mainThread = true)
    public Map<String, ?> getRecipe(IHasDeviceRecipeSlot be) {
        var stack = be.getRecipeItem();
        if (stack.isEmpty()) return null;
        return VanillaDetailRegistries.ITEM_STACK.getBasicDetails(stack);
    }
    @LuaFunction(mainThread = true)
    public int setRecipe(IHasDeviceRecipeSlot be, String input) throws LuaException {
        var stack = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(input));
        if (stack == null) throw new LuaException("Invalid item id: " + input);
        if (!(be instanceof BlockEntity beReal)) throw new LuaException("Peripheral not Block Entity, why?");
        if (workerSetRecipe == null) {
            workerSetRecipe = new FakePlayer(beReal.getLevel().getServer().overworld(), new GameProfile(UUID.randomUUID(), "worker"));
        }
        return be.setRecipe(new ItemStack(stack), workerSetRecipe);
    }

    // nexus stages
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
