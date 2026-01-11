package io.yukkuric.mnaop.magichem.tooltip;

import com.aranaira.magichem.item.EssentiaItem;
import com.aranaira.magichem.item.MateriaItem;
import com.aranaira.magichem.recipe.DistillationFabricationRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.HashMap;
import java.util.Map;

import static io.yukkuric.mnaop.magichem.tooltip.TooltipHelpers.*;

// https://github.com/YukkuriC/kubejs_playground/blob/main/client_scripts/MNA/magichem/DistillationRecipeDisplay.js
public class DistillationResultsTooltip {
    static final Map<ResourceLocation, Component> cache = new HashMap<>();
    static Map<ResourceLocation, DistillationFabricationRecipe> recipes = null;

    static void buildRecipeMap() {
        recipes = new HashMap<>();
        for (var r : DistillationFabricationRecipe.getAllDistillingRecipes(Minecraft.getInstance().level)) {
            var output = r.getResultItem(null);
            recipes.put(getItemId(output), r);
        }
    }
    static Component getDescripFor(ItemStack stack) {
        var id = getItemId(stack);
        if (cache.containsKey(id)) return cache.get(id);
        if (recipes == null) buildRecipeMap();

        var recipe = recipes.get(id);
        if (recipe == null) {
            cache.put(id, null);
            return null;
        }
        var inner = Component.literal("").withStyle(ChatFormatting.AQUA);
        var ret = Component.literal("= [").withStyle(ChatFormatting.DARK_GRAY).append(inner).append("]");
        var first = true;
        for (var m : recipe.getComponentMateria()) {
            var item = (MateriaItem) m.getItem();
            var count = m.getCount();
            var mid = Component.translatable(String.format("item.magichem.%s_%s.truncated", (item instanceof EssentiaItem ? "essentia" : "admixture"), item.getMateriaName()));
            if (count > 1) mid = mid.append(toSmallNum(count));
            if (first) first = false;
            else inner.append(SPACE);
            inner.append(mid);
        }
        if (recipe.getOutputRate() < 1)
            ret = ret.append(String.format(" (%.1f%%)", recipe.getOutputRate() * 100));
        cache.put(id, ret);
        return ret;
    }

    public static void HandleTooltips(ItemTooltipEvent e) {
        var tooltips = e.getToolTip();
        try {
            var line = getDescripFor(e.getItemStack());
            if (line != null) tooltips.add(line);
        } catch (Throwable ex) {
            tooltips.add(Component.literal(ex.toString()));
        }
    }
    public static void HandleLoggedIn(PlayerEvent.PlayerLoggedInEvent e) {
        cache.clear();
    }
}
