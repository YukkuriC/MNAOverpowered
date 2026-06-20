package io.yukkuric.mnaop.mixin.magichem.waste_pollution;

import com.aranaira.magichem.block.entity.ActuatorEarthBlockEntity;
import com.aranaira.magichem.config.ServerConfig;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.yukkuric.mnaop.MNAOPConfig;
import io.yukkuric.mnaop.magichem.waste_pollution.WastePollutionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ActuatorEarthBlockEntity.class)
public class MixinActuatorPollution {
    @WrapMethod(method = "tick")
    private static <T extends BlockEntity> void wrapTick(Level level, BlockPos pos, BlockState blockState, T t, Operation<Void> original) {
        if (level.isClientSide() || !MNAOPConfig.EnablesAlchemicalWastePollution() || !(t instanceof ActuatorEarthBlockEntity actuator) || actuator.getPaused()) {
            original.call(level, pos, blockState, t);
            return;
        }

        // count old grime progress
        var oldGrime = actuator.getGrimeInTank();
        var oldSuperGrime = actuator.getRarefiedGrimeInTank();
        original.call(level, pos, blockState, t);

        // normal grime
        var cnt = (oldGrime - actuator.getGrimeInTank()) / ServerConfig.grimePerWaste;
        if (cnt > 0) WastePollutionUtils.gen(level, pos, (float) MNAOPConfig.PollutionPerActuatorWaste(), cnt);
        // purified grime
        cnt = (oldSuperGrime - actuator.getRarefiedGrimeInTank()) / ServerConfig.grimePerWaste;
        if (cnt > 0) WastePollutionUtils.gen(level, pos, (float) MNAOPConfig.PollutionPerActuatorPurifiedWaste(), cnt);
    }
}
