package io.yukkuric.mnaop.mixin.magichem.ae2;

import com.aranaira.magichem.interop.JEIPlugin;
import io.yukkuric.mnaop.magichem.ae2.recipes.MateriaBottlingRecipe;
import io.yukkuric.mnaop.magichem.ae2.recipes.jei.MateriaBottlingRecipeCategoryExt;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(JEIPlugin.class)
public abstract class MixinJEIPlugin implements IModPlugin {
    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addCategoryExtension(MateriaBottlingRecipe.class, MateriaBottlingRecipeCategoryExt::new);
    }
}
