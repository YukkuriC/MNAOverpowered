package io.yukkuric.mnaop.mixin.magichem;

import com.aranaira.magichem.entities.ShlorpEntity;
import com.aranaira.magichem.entities.constructs.ai.ConstructProvideMateria;
import com.aranaira.magichem.entities.constructs.ai.ConstructSortMateriaFromDevice;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.yukkuric.mnaop.MNAOPConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ConstructProvideMateria.class)
public class MixinInstantConstructShlorper {
    @WrapOperation(method = {"tick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"), require = 0)
    boolean highSpeedShlorp(Level instance, Entity entity, Operation<Boolean> original) {
        if (entity instanceof ShlorpEntity shlorp)
            EditShlorp(shlorp);
        return original.call(instance, entity);
    }

    private static void EditShlorp(ShlorpEntity shlorp) {
        if (!MNAOPConfig.InstantConstructShlorps()) return;
        shlorp.setInstantPayload();
        shlorp.speed *= 10;
    }

    @Mixin(ConstructSortMateriaFromDevice.class)
    public static class MateriaSort {
        @WrapOperation(method = {"doShlorpCreation"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"), require = 0)
        boolean highSpeedShlorp(Level instance, Entity entity, Operation<Boolean> original) {
            if (entity instanceof ShlorpEntity shlorp)
                EditShlorp(shlorp);
            return original.call(instance, entity);
        }
    }
}
