package io.yukkuric.mnaop.magichem.ae2;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import com.aranaira.magichem.item.MateriaItem;
import com.aranaira.magichem.util.InventoryHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class AEHelpers {
    public static MateriaItem toMateria(AEKey what) {
        if (what instanceof AEItemKey ik && ik.getItem() instanceof MateriaItem mat) return mat;
        return null;
    }
    public static boolean isBottled(AEKey what) {
        return !InventoryHelper.hasCustomModelData(((AEItemKey) what).getReadOnlyStack());
    }
    public static void popBottle(ItemStack bottle, Level level, Vec3 pos) {
        level.addFreshEntity(new ItemEntity(level, pos.x, pos.y, pos.z, bottle));
    }
}
