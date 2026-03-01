package io.yukkuric.mnaop.mixin.magichem;

import com.aranaira.magichem.block.entity.PrimeAggregatorBlockEntity;
import io.yukkuric.mnaop.MNAOPConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PrimeAggregatorBlockEntity.class)
public class MixinPrimeAggregator {
    @Inject(method = "tick", at = @At("HEAD"), remap = false)
    private static void fasterProgress(Level pLevel, BlockPos pPos, BlockState pBlockState, PrimeAggregatorBlockEntity pEntity, CallbackInfo ci) {
        var extraAnimTick = MNAOPConfig.PrimeAggregatorExtraAnimationSpeed();
        var animStage = pEntity.getAnimStage();
        if (extraAnimTick > 0 && animStage > 0 && animStage % 2 == 0) {
            var ex = (AccessorAggregator) pEntity;
            ex.setProgress(pEntity.getProgress() + 4);
        }
    }

    @Mixin(PrimeAggregatorBlockEntity.class)
    public interface AccessorAggregator {
        @Accessor(remap = false)
        void setProgress(int val);
    }
}
