package io.yukkuric.mnaop.mixin.magichem;

import com.aranaira.magichem.entities.constructs.ai.ConstructProvideMateria;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ConstructProvideMateria.class, remap = false)
public interface AccessorConstructProvideMateria {
    @Accessor("area")
    AABB getArea();
    @Accessor("craftCount")
    int getCraftCount();
    @Accessor("deviceTargetPos")
    BlockPos getDeviceTargetPos();
    @Accessor("leaveOneInContainer")
    boolean getLeaveOneInContainer();
    @Accessor("area")
    @Mutable
    void setArea(AABB v);
    @Accessor("craftCount")
    @Mutable
    void setCraftCount(int v);
    @Accessor("deviceTargetPos")
    @Mutable
    void setDeviceTargetPos(BlockPos v);
    @Accessor("leaveOneInContainer")
    @Mutable
    void setLeaveOneInContainer(boolean v);
}
