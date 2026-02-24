package io.yukkuric.mnaop.mixin.magichem.ext;

import com.aranaira.magichem.block.entity.AlchemicalNexusBlockEntity;
import io.yukkuric.mnaop.mixin_interface.magichem.IHasLastUser;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(AlchemicalNexusBlockEntity.class)
public class MixinNexusLastUser extends BlockEntity implements IHasLastUser {
    @Shadow(remap = false)
    protected UUID initiatingPlayer;

    public MixinNexusLastUser() {
        super(null, null, null);
    }
    @Override
    public UUID getLastUserUUID() {
        return initiatingPlayer;
    }
}
