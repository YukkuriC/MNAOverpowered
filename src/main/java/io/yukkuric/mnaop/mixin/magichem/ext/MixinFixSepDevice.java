package io.yukkuric.mnaop.mixin.magichem.ext;

import com.aranaira.magichem.block.entity.ext.AbstractFixationBlockEntity;
import com.aranaira.magichem.block.entity.ext.AbstractSeparationBlockEntity;
import com.aranaira.magichem.recipe.FixationSeparationRecipe;
import io.yukkuric.mnaop.mixin_interface.magichem.IFixSepEx;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({AbstractFixationBlockEntity.class, AbstractSeparationBlockEntity.class,})
public class MixinFixSepDevice implements IFixSepEx {
    @Shadow(remap = false)
    protected FixationSeparationRecipe currentRecipe;
    @Shadow(remap = false)
    protected ItemStackHandler itemHandler;
    @Shadow(remap = false)
    protected int batchSize;

    @Override
    public boolean hasAnyRecipe() {
        return currentRecipe != null;
    }
    @Override
    public void setRecipe(FixationSeparationRecipe recipe) {
        currentRecipe = recipe;
    }
    @Override
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }
    @Override
    public int getBatchSize() {
        return batchSize;
    }
}
