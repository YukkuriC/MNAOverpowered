package io.yukkuric.mnaop.magichem.ae2.recipes.jei;

import com.aranaira.magichem.item.MateriaItem;
import com.aranaira.magichem.registry.ItemRegistry;
import io.yukkuric.mnaop.magichem.ae2.recipes.MateriaBottlingRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.LazyOptional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static io.yukkuric.mnaop.magichem.ae2.AEHelpers.GLOB_NBT;

public record MateriaBottlingRecipeCategoryExt(MateriaBottlingRecipe recipe) implements ICraftingCategoryExtension {
    @Override
    public ResourceLocation getRegistryName() {
        return recipe.getId();
    }

    static LazyOptional<List<ItemStack>> allMateria = LazyOptional.of(() -> {
        List<ItemStack> out = new ArrayList<>();
        ItemRegistry.ESSENTIA.getEntries().stream().forEach(mat -> out.add(mat.get().getDefaultInstance()));
        ItemRegistry.ADMIXTURES.getEntries().stream().forEach(mat -> out.add(mat.get().getDefaultInstance()));
        return out;
    });
    static LazyOptional<List<ItemStack>> allMateriaUnbottled = LazyOptional.of(() -> {
        List<ItemStack> out = new ArrayList<>();
        for (var stack : allMateria.orElse(null)) {
            stack = stack.copy();
            stack.setTag(GLOB_NBT);
            out.add(stack);
        }
        return out;
    });

    static ItemStack materiaFromFocus(Stream<IFocus<ItemStack>> focus) {
        var raw = focus.map(x -> x.getTypedValue().getItemStack().get())
                .filter(x -> x.getItem() instanceof MateriaItem).toList();
        return raw.size() > 0 ? raw.get(0) : null;
    }
    static ItemStack setVisuals(ItemStack original, boolean bottled) {
        if (original == null) return null;
        var ret = original.copy();
        ret.setTag(bottled ? null : GLOB_NBT);
        return ret;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper helper, IFocusGroup group) {
        var focusedInput = materiaFromFocus(group.getFocuses(VanillaTypes.ITEM_STACK, RecipeIngredientRole.INPUT));
        var focusedOutput = materiaFromFocus(group.getFocuses(VanillaTypes.ITEM_STACK, RecipeIngredientRole.OUTPUT));
        var matchedOutput = focusedInput;
        var matchedInput = focusedOutput;
        focusedInput = setVisuals(focusedInput, false);
        matchedInput = setVisuals(matchedInput, false);
        focusedOutput = setVisuals(focusedOutput, true);
        matchedOutput = setVisuals(matchedOutput, true);

        helper.createAndSetInputs(builder, VanillaTypes.ITEM_STACK, List.of(
                focusedInput != null ? List.of(focusedInput) :
                        matchedInput != null ? List.of(matchedInput) : allMateriaUnbottled.orElse(null),
                List.of(Items.GLASS_BOTTLE.getDefaultInstance())), 0, 0);
        helper.createAndSetOutputs(builder, VanillaTypes.ITEM_STACK,
                focusedOutput != null ? List.of(focusedOutput) :
                        matchedOutput != null ? List.of(matchedOutput) : allMateria.orElse(null));
    }
}
