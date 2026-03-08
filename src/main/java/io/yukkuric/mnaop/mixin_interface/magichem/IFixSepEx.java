package io.yukkuric.mnaop.mixin_interface.magichem;

import com.aranaira.magichem.recipe.FixationSeparationRecipe;
import net.minecraftforge.items.ItemStackHandler;

public interface IFixSepEx {
    boolean hasAnyRecipe();
    void setRecipe(FixationSeparationRecipe recipe);
    ItemStackHandler getItemHandler();
    int getBatchSize();
}
