package io.yukkuric.mnaop.mixin.magichem.ext;

import com.aranaira.magichem.block.entity.CircleFabricationBlockEntity;
import com.aranaira.magichem.block.entity.GrandCircleFabricationBlockEntity;
import com.aranaira.magichem.block.entity.ext.AbstractFabricationBlockEntity;
import com.aranaira.magichem.recipe.DistillationFabricationRecipe;
import com.aranaira.magichem.recipe.FluidDistillationFabricationRecipe;
import io.yukkuric.mnaop.mixin_interface.magichem.ICoFEx;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({
        CircleFabricationBlockEntity.class,
        GrandCircleFabricationBlockEntity.class
})
public abstract class MixinCircleFabrication extends AbstractFabricationBlockEntity implements ICoFEx {
    protected MixinCircleFabrication() {
        super(null, null, null);
    }

    public boolean hasAnyRecipe() {
        return currentItemRecipe != null || currentFluidRecipe != null;
    }
    public void setRecipes(DistillationFabricationRecipe rItem, FluidDistillationFabricationRecipe rFluid, int batchCount) {
        currentItemRecipe = rItem;
        currentFluidRecipe = rFluid;
        batchSize = batchCount;
        doDeferredRecipeCheck = false;
    }
    public DistillationFabricationRecipe queryRecipe(Item src) {
        return getRecipeForItem(src);
    }
    public FluidDistillationFabricationRecipe queryRecipe(Fluid src) {
        return getRecipeForFluid(src);
    }
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }
}
