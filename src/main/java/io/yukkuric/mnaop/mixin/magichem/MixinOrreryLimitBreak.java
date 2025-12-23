package io.yukkuric.mnaop.mixin.magichem;

import com.aranaira.magichem.foundation.saveddata.EldrinOrreryLimiterSD;
import io.yukkuric.mnaop.MNAOPConfig;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EldrinOrreryLimiterSD.class)
public class MixinOrreryLimitBreak {
    @Inject(method = "playerHasOrrery", at = @At("HEAD"), remap = false, require = 0, cancellable = true)
    private void NoLimitCheck(Player pPlayer, CallbackInfoReturnable<Boolean> cir) {
        if (MNAOPConfig.UnlimitedOrrery()) cir.setReturnValue(false);
    }
}
