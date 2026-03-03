package io.yukkuric.mnaop.magichem.ae2;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import com.aranaira.magichem.item.MateriaItem;
import com.aranaira.magichem.util.InventoryHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;

import java.util.*;

public class AEHelpers {
    public static MateriaItem toMateria(AEKey what) {
        if (what instanceof AEItemKey ik && ik.getItem() instanceof MateriaItem mat) return mat;
        return null;
    }
    public static boolean isBottled(AEItemKey what) {
        return !InventoryHelper.hasCustomModelData(what.getReadOnlyStack());
    }
    public static void popBottle(ItemStack bottle, Level level, Vec3 pos) {
        level.addFreshEntity(new ItemEntity(level, pos.x, pos.y, pos.z, bottle));
    }

    public static class MateriaInventoryFiller {
        final ItemStackHandler handler;
        final int slotStart, slotCount;
        final Map<MateriaItem, Integer> mapFree, mapBottled;
        final Set<Integer> deadSlots = new HashSet<>();
        public MateriaInventoryFiller(ItemStackHandler handler, int slotStart, int slotCount) {
            this.handler = handler;
            this.slotStart = slotStart;
            this.slotCount = slotCount;
            mapFree = new HashMap<>();
            mapBottled = new HashMap<>();
        }
        public boolean receive(MateriaItem materia, int count, boolean bottled) {
            // TODO
            return false;
        }
        public void actuallyFill() {
            // TODO
        }
    }
}
