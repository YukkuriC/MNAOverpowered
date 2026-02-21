package io.yukkuric.mnaop.magichem.cc;

import com.aranaira.magichem.block.entity.routers.IRouterBlockEntity;
import com.aranaira.magichem.foundation.IHasDeviceRecipeSlot;
import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.detail.VanillaDetailRegistries;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.GenericPeripheral;
import io.yukkuric.mnaop.MNAOPMod;
import io.yukkuric.mnaop.mixin_interface.magichem.IHasLastUser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.UUID;

public class RecipePeripheral implements GenericPeripheral {
    public static final String ID = MNAOPMod.MODID + ":recipe_holder";
    public static RecipePeripheral INSTANCE = new RecipePeripheral();
    private static FakePlayer workerSetRecipe;

    @Override
    public String id() {
        return ID;
    }

    @LuaFunction(mainThread = true)
    public Map<String, Object> getRecipe(IHasDeviceRecipeSlot be) {
        ItemStack stack = null;
        try {
            stack = be.getRecipeItem();
        } catch (Throwable e) {
            MNAOPMod.LOGGER.error("error in {}.getRecipe: {}", be.getClass().getSimpleName(), e);
        }
        if (stack == null || stack.isEmpty()) return null;

        return VanillaDetailRegistries.ITEM_STACK.getBasicDetails(stack);
    }
    @LuaFunction(mainThread = true)
    public int setRecipe(IHasDeviceRecipeSlot be, String input) throws LuaException {
        // validate target
        var stack = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(input));
        if (stack == null) throw new LuaException("Invalid item id: " + input);

        // validate block
        if (be instanceof IRouterBlockEntity router) {
            var master = router.getMaster();
            if (!(master instanceof IHasDeviceRecipeSlot masterBE))
                throw new LuaException("Router has no valid master, why?");
            be = masterBE;
        }
        if (!(be instanceof BlockEntity beReal)) throw new LuaException("Peripheral not Block Entity, why?");

        // grab user
        ServerPlayer worker = null;
        if (be instanceof IHasLastUser holder) worker = holder.getLastUser((ServerLevel) beReal.getLevel());
        if (worker == null) {
            if (workerSetRecipe == null) {
                workerSetRecipe = new FakePlayer(beReal.getLevel().getServer().overworld(), new GameProfile(UUID.randomUUID(), "worker"));
            }
            worker = workerSetRecipe;
        }

        try {
            return be.setRecipe(new ItemStack(stack), worker);
        } catch (Throwable e) {
            MNAOPMod.LOGGER.error("error in {}.setRecipe: {}", be.getClass().getSimpleName(), e);
            return -1;
        }
    }
}
