package io.yukkuric.mnaop.mixin.fixes;

import com.mna.api.items.ITieredItem;
import com.mna.api.recipes.IMARecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ITieredItem.class)
public interface FixTieredRecipeSorter {
    /**
     * @author YukkuriC
     * @reason because "Injector in interface is unsupported" damn
     */
    @Overwrite(remap = false)
    default int resolveTier(Level world, ItemStack stack) {
        // same as original
        var recipes = world.getRecipeManager().getRecipes().parallelStream().filter((r) -> {
            if (r == null) return false;
            ItemStack res = r.getResultItem(world.registryAccess());
            return res != null && res.getItem() == this;
        }).toList();
        if (recipes.isEmpty()) return -2;

        // get lowest
        IMARecipe lowestTieredRecipe = null;
        for (var r : recipes) {
            if (!(r instanceof IMARecipe ima)) continue;
            if (lowestTieredRecipe == null || ima.getTier() < lowestTieredRecipe.getTier()) lowestTieredRecipe = ima;
        }
        return lowestTieredRecipe == null ? 0 : lowestTieredRecipe.getTier();
    }
}
