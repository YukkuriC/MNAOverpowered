package io.yukkuric.mnaop.mixin.magichem;

import com.aranaira.magichem.block.entity.PrimeAggregatorBlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PrimeAggregatorBlockEntity.class)
public interface AccessorPrimeAggregator {
    @Accessor(remap = false)
    int getItemsDelivered();
    @Accessor(remap = false)
    void setItemsDelivered(int val);
    @Accessor(remap = false)
    int getMateriaDelivered();
    @Accessor(remap = false)
    void setMateriaDelivered(int val);
    @Accessor(remap = false)
    int getSlurryDelivered();
    @Accessor(remap = false)
    void setSlurryDelivered(int val);

    @Accessor(remap = false)
    ItemStackHandler getItemHandler();
    @Accessor(remap = false)
    void setAnimStage(int val);
}
