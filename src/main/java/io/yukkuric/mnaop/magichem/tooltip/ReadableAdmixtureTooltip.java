package io.yukkuric.mnaop.magichem.tooltip;

import com.aranaira.magichem.foundation.NameCountPair;
import com.aranaira.magichem.item.AdmixtureItem;
import com.aranaira.magichem.registry.ItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import java.lang.reflect.Field;
import java.util.*;

import static io.yukkuric.mnaop.magichem.tooltip.TooltipHelpers.*;

// https://github.com/YukkuriC/kubejs_playground/blob/main/client_scripts/MNA/magichem/FormulaReadableTooltip.js
public class ReadableAdmixtureTooltip {
    public static class NameCountPairWithMark extends NameCountPair {
        public final boolean isAdmixture;
        public NameCountPairWithMark(boolean isAdm, String pName, byte pCount) {
            super(pName, pCount);
            isAdmixture = isAdm;
        }
    }
    public static Map<String, List<NameCountPairWithMark>> admixtureRaw = new HashMap<>();
    static Field fEss, fAdm;

    static {
        try {
            fEss = AdmixtureItem.class.getDeclaredField("formulaEssentiaPortions");
            fAdm = AdmixtureItem.class.getDeclaredField("formulaAdmixturePortions");
            fEss.setAccessible(true);
            fAdm.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    static void initAdmixtureRaw() throws IllegalAccessException {
        if (admixtureRaw.size() > 0) return;
        for (var reg : ItemRegistry.ADMIXTURES.getEntries()) {
            var mat = (AdmixtureItem) reg.get();
            var key = mat.getMateriaName();
            var data = new ArrayList<NameCountPairWithMark>();
            for (var pair : (List<NameCountPair>) fEss.get(mat))
                data.add(new NameCountPairWithMark(false, pair.getName(), pair.getCount()));
            for (var pair : (List<NameCountPair>) fAdm.get(mat))
                data.add(new NameCountPairWithMark(true, pair.getName(), pair.getCount()));
            admixtureRaw.put(key, data);
        }
    }

    // get readable formula
    public static Map<String, Component> cachedFormula = new HashMap<>();
    public static Map<String, Component> cachedFormulaDeep = new HashMap<>();
    static Component getFormula(String name, boolean deep) {
        var cacheMap = deep ? cachedFormulaDeep : cachedFormula;
        if (!cacheMap.containsKey(name)) {
            var raw = admixtureRaw.get(name);
            if (raw == null) return NOPE;
            var output = Component.literal("").withStyle(ChatFormatting.DARK_AQUA);
            cacheMap.put(name, output);
            var first = true;
            for (var pair : raw) {
                if (!first) output.append(SPACE);
                if (pair.isAdmixture) {
                    if (deep) {
                        output.append(BR_L);
                        output.append(getFormula(pair.getName(), deep));
                        output.append(BR_R);
                    } else {
                        output.append(Component.translatable(String.format("item.magichem.admixture_%s.truncated", pair.getName())));
                    }
                } else {
                    output.append(Component.translatable(String.format("item.magichem.essentia_%s.truncated", pair.getName())));
                }
                if (deep || pair.getCount() > 1) {
                    output.append(Component.literal(toSmallNum(pair.getCount())).withStyle(pair.isAdmixture ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.GOLD));
                }
                first = false;
            }
        }
        return cacheMap.get(name);
    }

    // get overall counts
    public static Map<String, Map<String, Integer>> cachedAdmixtureCounts = new HashMap<>();
    public static Map<String, Component> cachedAdmixtureCountsDisplay = new HashMap<>();
    static Map<String, Integer> getEssentiaCounts(String name) {
        var raw = admixtureRaw.get(name);
        if (raw == null) return new HashMap<>();
        if (!cachedAdmixtureCounts.containsKey(name)) {
            var res = new HashMap<String, Integer>();
            cachedAdmixtureCounts.put(name, res);
            for (var pair : raw) {
                if (pair.isAdmixture) {
                    for (var kv : getEssentiaCounts(pair.getName()).entrySet()) {
                        res.put(kv.getKey(), (res.getOrDefault(kv.getKey(), 0)) + kv.getValue() * pair.getCount());
                    }
                } else {
                    res.put(pair.getName(), (res.getOrDefault(pair.getName(), 0)) + pair.getCount());
                }
            }
        }
        return cachedAdmixtureCounts.get(name);
    }
    static Component getEssentiaCountsDisplay(String name) {
        var raw = admixtureRaw.get(name);
        if (raw == null) return NOPE;
        if (!cachedAdmixtureCountsDisplay.containsKey(name)) {
            var output = Component.literal("").withStyle(ChatFormatting.DARK_AQUA);
            var first = true;
            for (var kv : getEssentiaCounts(name).entrySet()) {
                if (!first) output.append(SPACE);
                output.append(Component.translatable(String.format("item.magichem.essentia_%s.truncated", kv.getKey())));
                if (kv.getValue() > 1)
                    output.append(Component.literal(toSmallNum(kv.getValue())).withStyle(ChatFormatting.GOLD));
                first = false;
            }
            cachedAdmixtureCountsDisplay.put(name, output);
        }
        return cachedAdmixtureCountsDisplay.get(name);
    }

    public static void HandleTooltips(ItemTooltipEvent e) {
        if (!(e.getItemStack().getItem() instanceof AdmixtureItem adm)) return;
        var name = adm.getMateriaName();
        var tooltips = e.getToolTip();
        var isShift = Screen.hasShiftDown();
        try {
            initAdmixtureRaw();
            var formula = getFormula(name, isShift);
            tooltips.add(Component.translatable("tooltip.magichem.admixture_formula").withStyle(ChatFormatting.DARK_GRAY).append(" [ ").append(formula).append(" ]"));
            if (isShift) {
                var counts = getEssentiaCountsDisplay(name);
                tooltips.add(Component.literal("= [ ").withStyle(ChatFormatting.DARK_GRAY).append(counts).append(" ]"));
            }
        } catch (Throwable ex) {
            tooltips.add(Component.literal(ex.toString()));
        }
    }
}
