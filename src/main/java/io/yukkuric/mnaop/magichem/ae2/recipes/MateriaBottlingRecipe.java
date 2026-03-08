package io.yukkuric.mnaop.magichem.ae2.recipes;

import com.aranaira.magichem.item.MateriaItem;
import com.aranaira.magichem.util.InventoryHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class MateriaBottlingRecipe extends CustomRecipe {
    public static IDSerializer<MateriaBottlingRecipe> SERIALIZER = new IDSerializer<>(MateriaBottlingRecipe::new);

    public MateriaBottlingRecipe(ResourceLocation id) {
        super(id, CraftingBookCategory.MISC);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack materia = null, bottle = null;
        for (int i = 0; i < container.getContainerSize(); i++) {
            var stack = container.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.is(Items.GLASS_BOTTLE)) {
                if (bottle != null) return false;
                bottle = stack;
            } else if (stack.getItem() instanceof MateriaItem) {
                if (materia != null) return false;
                materia = stack;
                if (!InventoryHelper.hasCustomModelData(materia)) return false;
            } else return false;
        }
        return materia != null && bottle != null;
    }
    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess foo) {
        ItemStack materia = null, bottle = null;
        for (int i = 0; i < container.getContainerSize(); i++) {
            var stack = container.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.is(Items.GLASS_BOTTLE)) {
                if (bottle != null) return ItemStack.EMPTY;
                bottle = stack;
            } else if (stack.getItem() instanceof MateriaItem) {
                if (materia != null) return ItemStack.EMPTY;
                materia = stack;
            } else return ItemStack.EMPTY;
        }
        if (materia == null) return ItemStack.EMPTY;
        var ret = materia.copyWithCount(1);
        ret.setTag(null);
        return ret;
    }
    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return w * h >= 2;
    }
    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}
