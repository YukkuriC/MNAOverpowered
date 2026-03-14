package io.yukkuric.mnaop.ritual;

import com.mna.api.rituals.IRitualContext;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

public interface IRitualHelpers {
    default void giveItemsAtCenter(IRitualContext context, ItemStack... stacks) {
        giveItemsAtCenter(context, Arrays.asList(stacks));
    }
    default void giveItemsAtCenter(IRitualContext context, Iterable<ItemStack> stacks) {
        var center = context.getCenter().getCenter();
        var level = context.getLevel();
        for (var stack : stacks) {
            var entity = new ItemEntity(level, center.x, center.y, center.z, stack);
            level.addFreshEntity(entity);
        }
    }
}
