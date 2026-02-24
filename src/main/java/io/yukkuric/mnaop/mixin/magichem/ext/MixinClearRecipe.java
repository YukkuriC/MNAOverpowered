package io.yukkuric.mnaop.mixin.magichem.ext;

import com.aranaira.magichem.block.entity.AlchemicalNexusBlockEntity;
import com.aranaira.magichem.block.entity.PrimeAggregatorBlockEntity;
import com.aranaira.magichem.block.entity.ext.*;
import io.yukkuric.mnaop.mixin_interface.magichem.IClearRecipe;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({
        AbstractFixationBlockEntity.class,
        AbstractSeparationBlockEntity.class,
        AbstractFabricationBlockEntity.class,
        AlchemicalNexusBlockEntity.class,
        PrimeAggregatorBlockEntity.class,
})
public abstract class MixinClearRecipe implements IClearRecipe {
}
