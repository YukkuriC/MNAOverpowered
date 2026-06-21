package io.yukkuric.mnaop.mixin.magichem.waste_pollution;

import com.aranaira.magichem.block.entity.ext.AbstractBlockEntityWithEfficiency;
import com.aranaira.magichem.events.CommonEventHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.yukkuric.mnaop.MNAOPConfig;
import io.yukkuric.mnaop.magichem.waste_pollution.WastePollutionUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CommonEventHelper.class)
public class MixinManualPollution {
    @WrapOperation(method = "generateWasteFromCleanedApparatus", at = @At(value = "INVOKE", target = "Lcom/aranaira/magichem/block/entity/ext/AbstractBlockEntityWithEfficiency;clean()I"))
    private static int genPollution(AbstractBlockEntityWithEfficiency instance, Operation<Integer> original) {
        var ret = original.call(instance);
        if (MNAOPConfig.EnablesAlchemicalWastePollution())
            WastePollutionUtils.gen(instance.getLevel(), instance.getBlockPos(), (float) MNAOPConfig.PollutionPerManualWaste(), ret);
        return ret;
    }
}
