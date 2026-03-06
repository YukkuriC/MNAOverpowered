package io.yukkuric.mnaop.mixin.magichem.ae2;

import appeng.capabilities.Capabilities;
import com.aranaira.magichem.block.entity.ActuatorEnderBlockEntity;
import com.aranaira.magichem.entities.ShlorpEntity;
import com.aranaira.magichem.foundation.enums.ShlorpParticleMode;
import com.aranaira.magichem.item.MateriaItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mna.tools.math.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ActuatorEnderBlockEntity.class)
public class MixinNavigatorTarget {
    @Unique
    private BlockEntity picked = null;
    @WrapOperation(method = "getMirrorTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    BlockEntity manualCatcher(Level instance, BlockPos pos, Operation<BlockEntity> original) {
        picked = original.call(instance, pos);
        return picked;
    }

    @Inject(method = "getMirrorTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;", shift = At.Shift.AFTER), cancellable = true)
    void checkTargetInv(CallbackInfoReturnable<BlockEntity> cir/*, @Local BlockEntity entityQuery*/) {
        // @Local does not match any or matched multiple local variables in the target method
        // wtf MixinExtras ??
        if (cir.getReturnValue() == null && picked != null) {
            var invCap = picked.getCapability(Capabilities.GENERIC_INTERNAL_INV);
            if (invCap.isPresent()) cir.setReturnValue(picked);
        }
    }

    @WrapOperation(method = "createShlorpToTarget", at = @At(value = "INVOKE", target = "Lcom/aranaira/magichem/entities/ShlorpEntity;configure(Lnet/minecraft/core/BlockPos;Lcom/mna/tools/math/Vector3;Lcom/mna/tools/math/Vector3;Lnet/minecraft/core/BlockPos;Lcom/mna/tools/math/Vector3;Lcom/mna/tools/math/Vector3;FFILcom/aranaira/magichem/item/MateriaItem;ILcom/aranaira/magichem/foundation/enums/ShlorpParticleMode;)V"), remap = false)
    void setDefaultOriginTangent(ShlorpEntity instance, BlockPos pStartLocation, Vector3 pStartOrigin, Vector3 pStartTangent, BlockPos pEndLocation, Vector3 pEndOrigin, Vector3 pEndTangent, float pSpeed, float pDistanceBetweenClusters, int pClusterCount, MateriaItem pMateriaType, int pMateriaCount, ShlorpParticleMode pParticleMode, Operation<Void> original) {
        if (pEndOrigin.isZero() && pEndTangent.isZero()) {
            pEndOrigin = new Vector3(0.5, 0.5, 0.5);
            pEndTangent = new Vector3(0, 1, 0);
        }
        original.call(instance, pStartLocation, pStartOrigin, pStartTangent, pEndLocation, pEndOrigin, pEndTangent, pSpeed, pDistanceBetweenClusters, pClusterCount, pMateriaType, pMateriaCount, pParticleMode);
    }
}
