package io.yukkuric.mnaop.mixin_interface.magichem;

import com.aranaira.magichem.recipe.DistillationFabricationRecipe;
import com.aranaira.magichem.recipe.FluidDistillationFabricationRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.items.ItemStackHandler;

public interface ICoFEx {
    boolean hasAnyRecipe();
    void setRecipes(DistillationFabricationRecipe rItem, FluidDistillationFabricationRecipe rFluid, int batchCount);
    DistillationFabricationRecipe queryRecipe(Item src);
    FluidDistillationFabricationRecipe queryRecipe(Fluid src);
    ItemStackHandler getItemHandler();
}
