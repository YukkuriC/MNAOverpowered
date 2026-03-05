package io.yukkuric.mnaop.magichem.ae2;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import com.aranaira.magichem.item.MateriaItem;
import com.aranaira.magichem.util.InventoryHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;

import java.util.*;

public class AEHelpers {
    static CompoundTag GLOB_NBT = new CompoundTag() {{
        putInt("CustomModelData", 1);
    }};
    public static final List<Component> PLACEHOLDER_TOOLTIP = List.of(Component.translatable("mnaop.magichem.ae2.place_holder.tooltip"));
    public static ItemStack toStack(MateriaItem materia, int count, boolean bottled) {
        var stack = new ItemStack(materia, count);
        if (!bottled) stack.setTag(GLOB_NBT);
        return stack;
    }
    public static MateriaItem toMateria(AEKey what) {
        if (what instanceof AEItemKey ik && ik.getItem() instanceof MateriaItem mat) return mat;
        return null;
    }
    public static List<MateriaItem> toMateriaList(List<ItemStack> raw) {
        var ret = new ArrayList<MateriaItem>();
        for (var stack : raw)
            if (stack.getItem() instanceof MateriaItem materia) ret.add(materia);
        return ret;
    }
    public static boolean isBottled(AEItemKey what) {
        return !InventoryHelper.hasCustomModelData(what.getReadOnlyStack());
    }
    public static void popBottle(ItemStack bottle, Level level, Vec3 pos) {
        level.addFreshEntity(new ItemEntity(level, pos.x, pos.y, pos.z, bottle));
    }

    /**
     * always return whether this input is <b color='red'>DENIED</b><br>
     * IDEA told me to reverse its return value
     */
    public static class MateriaInventoryFiller {
        static final int SLOT_PER_ROW = 2;
        static final int STACK_CAP = 64;

        final ItemStackHandler handler;
        final int slotStart, slotCount;
        final Map<MateriaItem, Integer> mapFree, mapBottled;
        final List<MateriaItem> matList;
        final Map<MateriaItem, Integer>[] allMaps;

        public MateriaInventoryFiller(ItemStackHandler handler, int slotStart, int slotCount, List<ItemStack> recipeInputs) {
            this.handler = handler;
            this.slotStart = slotStart;
            this.slotCount = slotCount;
            mapFree = new HashMap<>();
            mapBottled = new HashMap<>();
            allMaps = new Map[]{mapBottled, mapFree};
            matList = toMateriaList(recipeInputs);
        }
        public boolean init() {
            // if recipe is larger than machine, why?
            if (matList.size() * SLOT_PER_ROW > slotCount) return true;

            // collect materia & search for invalid item
            for (int i = 0; i < slotCount; i++) {
                var stack = handler.getStackInSlot(slotStart + i);
                if (stack.isEmpty()) continue;
                if (receive(stack.getItem(), stack.getCount(), !InventoryHelper.hasCustomModelData(stack))) {
                    return true;
                }
            }
            return false;
        }
        public boolean receive(Item itemInput, int count, boolean bottled) {
            // fail if not materia or included in recipe
            if (!(itemInput instanceof MateriaItem mat) || !matList.contains(mat)) return true;
            var map = bottled ? mapBottled : mapFree;

            // insta-fail if count > 2 stacks
            var newCount = map.getOrDefault(mat, 0) + count;
            if (newCount > SLOT_PER_ROW * STACK_CAP) return true;

            // update counts
            map.put(mat, newCount);
            return false;
        }
        public boolean finalCheck() {
            for (var mat : matList) {
                int slots = 0;
                for (var map : allMaps) {
                    int count = map.getOrDefault(mat, 0);
                    slots += count / STACK_CAP;
                    if (slots % 64 > 0) slots++;
                }
                if (slots > SLOT_PER_ROW) return true;
            }
            return false;
        }
        public void actuallyFill() {
            for (int row = 0; row < matList.size(); row++) {
                var mat = matList.get(row);
                int rowStart = slotStart + row * SLOT_PER_ROW;
                int ptr = 0;
                for (var map : allMaps) {
                    int count = map.getOrDefault(mat, 0);
                    while (count > 0) {
                        if (ptr > SLOT_PER_ROW) break; // shouldn't happen
                        int step = Math.min(STACK_CAP, count);
                        count -= step;
                        handler.setStackInSlot(rowStart + ptr, toStack(mat, step, map == mapBottled));
                        ptr++;
                    }
                }
            }
        }
    }
}
