package io.yukkuric.mnaop.mixin_interface.magichem;

import com.aranaira.magichem.block.entity.CirclePowerBlockEntity;
import com.aranaira.magichem.config.ServerConfig;
import io.yukkuric.mnaop.MNAOPConfig;
import net.minecraft.core.Direction;

public class PowerCircleExt {
    public static int reagentLevel(CirclePowerBlockEntity self) {
        int res = 0;
        for (int i = 1; i <= 4; i++)
            if (self.hasReagent(i)) res++;
        return res;
    }
    public static int increasedRate(CirclePowerBlockEntity self) {
        return increasedRate(self, reagentLevel(self));
    }
    public static int increasedRate(CirclePowerBlockEntity self, int level) {
        var base = CirclePowerBlockEntity.getGenRate(level);
        CirclePowerBlockEntity stacked = null;
        var ptr = self.getBlockPos().mutable().move(Direction.DOWN);
        for (int i = 0; i < 3; i++) {
            var be = self.getLevel().getBlockEntity(ptr);
            if (be instanceof CirclePowerBlockEntity circle) {
                stacked = circle;
                break;
            }
            ptr.move(Direction.DOWN);
        }
        if (stacked == null) return base;
        var ratio = (MNAOPConfig.CirclePowerStackMultRatio() * increasedRate(stacked)) / ServerConfig.circlePowerGen4Reagent;
        return (int) (ratio * base);
    }
}
