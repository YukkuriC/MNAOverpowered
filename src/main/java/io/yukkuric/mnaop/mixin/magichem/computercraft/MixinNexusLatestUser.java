package io.yukkuric.mnaop.mixin.magichem.computercraft;

import com.aranaira.magichem.block.entity.AlchemicalNexusBlockEntity;
import io.yukkuric.mnaop.mixin_interface.magichem.IHasLastUser;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(AlchemicalNexusBlockEntity.class)
public class MixinNexusLatestUser extends BlockEntity implements IHasLastUser {
    @Shadow
    protected UUID initiatingPlayer;

    public MixinNexusLatestUser() {
        super(null, null, null);
    }
    @Override
    public UUID getLastUserUUID() {
        return initiatingPlayer;
    }
}
