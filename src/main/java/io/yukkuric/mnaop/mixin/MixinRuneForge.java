package io.yukkuric.mnaop.mixin;

import com.mna.api.blocks.tile.TileEntityWithInventory;
import com.mna.blocks.runeforging.RuneforgeBlock;
import com.mna.blocks.tileentities.RuneForgeTile;
import io.yukkuric.mnaop.MNAOPConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;

@Mixin(RuneForgeTile.class)
public abstract class MixinRuneForge extends TileEntityWithInventory implements IForgeBlockEntity {
    public MixinRuneForge(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state, int inventorySize) {
        super(tileEntityTypeIn, pos, state, inventorySize);
    }

    private static Method m_getPedestalUpgradeType;
    private String wrapGetPedestalUpgradeType(BlockPos offset) {
        try {
            if (m_getPedestalUpgradeType == null)
                m_getPedestalUpgradeType = RuneForgeTile.class.getDeclaredMethod("getPedestalUpgradeType", BlockPos.class);
            return m_getPedestalUpgradeType.invoke(this, offset).toString();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    private void generalHook(String target, CallbackInfoReturnable<Boolean> cir) {
        if (!MNAOPConfig.RuneForgeSingleCrystalUpgrade()) return;
        switch (getBlockState().getValue(RuneforgeBlock.FACING)) {
            case EAST, WEST -> generalHookSided(target, Direction.NORTH, Direction.SOUTH, cir);
            case NORTH, SOUTH -> generalHookSided(target, Direction.EAST, Direction.WEST, cir);
        }
    }
    private void generalHookSided(String target, Direction side1, Direction side2, CallbackInfoReturnable<Boolean> cir) {
        var up1 = this.wrapGetPedestalUpgradeType(new BlockPos(side1.getNormal()));
        var up2 = this.wrapGetPedestalUpgradeType(new BlockPos(side2.getNormal()));
        if (up1.equals("NONE") || up2.equals("NONE"))
            cir.setReturnValue(false); // sry I really want to avoid rewriting particles
        else cir.setReturnValue(target.equals(up1) || target.equals(up2));
    }

    @Inject(method = "hasMultiUpgrade", at = @At("HEAD"), remap = false, cancellable = true)
    void hookMulti(CallbackInfoReturnable<Boolean> cir) {
        generalHook("DOUBLE", cir);
    }
    @Inject(method = "hasSpeedUpgrade", at = @At("HEAD"), remap = false, cancellable = true)
    void hookSpeed(CallbackInfoReturnable<Boolean> cir) {
        generalHook("SPEED", cir);
    }
    @Inject(method = "hasRepairUpgrade", at = @At("HEAD"), remap = false, cancellable = true)
    void hookRepair(CallbackInfoReturnable<Boolean> cir) {
        generalHook("REPAIR", cir);
    }
}
