package io.yukkuric.mnaop.mixin.magichem;

import com.aranaira.magichem.block.entity.ext.AbstractFabricationBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractFabricationBlockEntity.class)
public interface AccessorCircleFabrication {
    @Accessor(remap = false)
    boolean getDoDeferredRecipeCheck();
    @Accessor(remap = false)
    void setDoDeferredRecipeCheck(boolean val);
    @Accessor(remap = false)
    boolean getDeferredRecipeIsFluid();
    @Accessor(remap = false)
    void setDeferredRecipeIsFluid(boolean val);
    @Accessor(remap = false)
    ResourceLocation getDeferredRecipeQuery();
    @Accessor(remap = false)
    void setDeferredRecipeQuery(ResourceLocation val);

    @Accessor(remap = false)
    ItemStackHandler getItemHandler();
}
