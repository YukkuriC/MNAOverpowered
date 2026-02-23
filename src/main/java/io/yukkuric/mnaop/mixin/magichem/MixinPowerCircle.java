package io.yukkuric.mnaop.mixin.magichem;

import com.aranaira.magichem.block.entity.CirclePowerBlockEntity;
import com.aranaira.magichem.gui.CirclePowerMenu;
import com.aranaira.magichem.gui.CirclePowerScreen;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.yukkuric.mnaop.MNAOPConfig;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import static io.yukkuric.mnaop.mixin_interface.magichem.PowerCircleExt.increasedRate;

@Mixin(CirclePowerBlockEntity.class)
public abstract class MixinPowerCircle {
    @WrapOperation(method = {
            "getEnergyLimit",
            "generatePower",
    }, at = @At(value = "INVOKE", target = "Lcom/aranaira/magichem/block/entity/CirclePowerBlockEntity;getGenRate(I)I"), remap = false)
    private static int wrapGenRate(int reagentCount, Operation<Integer> original, @Local(argsOnly = true) CirclePowerBlockEntity self) {
        if (MNAOPConfig.CirclePowerStackMultRatio() <= 1) return original.call(reagentCount);
        return increasedRate(self, reagentCount);
    }

    @Mixin(CirclePowerScreen.class)
    static abstract class Screen extends AbstractContainerScreen<CirclePowerMenu> {
        public Screen() {
            super(null, null, null);
        }

        @Redirect(method = "renderLabels", at = @At(value = "INVOKE", target = "Lcom/aranaira/magichem/block/entity/CirclePowerBlockEntity;getGenRate(I)I"), remap = false)
        private int wrapGenRate(int reagentCount) {
            var self = menu.blockEntity;
            return increasedRate(self, reagentCount);
        }

        @ModifyArg(method = "renderLabels", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"))
        String shorterRateText(String literal) {
            if (!literal.matches("\\d+")) return literal;
            var raw = (float) Integer.parseInt(literal);
            if (raw < 1e4) return literal;
            String postfix;
            if (raw >= 1e9) {
                raw /= 1e9;
                postfix = "G";
            } else if (raw >= 1e6) {
                raw /= 1e6;
                postfix = "M";
            } else {
                raw /= 1e3;
                postfix = "K";
            }
            return String.format("%.1f %s", raw, postfix);
        }
    }
}
