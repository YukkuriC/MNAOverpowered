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
import org.spongepowered.asm.mixin.injection.At;

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

        @WrapOperation(method = "renderLabels", at = @At(value = "INVOKE", target = "Lcom/aranaira/magichem/block/entity/CirclePowerBlockEntity;getGenRate(I)I"), remap = false)
        private int wrapGenRate(int reagentCount, Operation<Integer> original) {
            if (MNAOPConfig.CirclePowerStackMultRatio() <= 1) return original.call(reagentCount);
            var self = menu.blockEntity;
            return increasedRate(self, reagentCount);
        }
    }
}
