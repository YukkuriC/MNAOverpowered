package io.yukkuric.mnaop.mixin.magichem.computercraft;

import com.aranaira.magichem.block.entity.*;
import com.aranaira.magichem.gui.*;
import io.yukkuric.mnaop.mixin_interface.magichem.IHasLastUser;
import net.minecraft.nbt.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin({
        CircleFabricationBlockEntity.class,
        GrandCircleFabricationBlockEntity.class,
        FuseryBlockEntity.class,
        GrandFuseryBlockEntity.class,
        PrimeAggregatorBlockEntity.class,
})
public class MixinLatestUserExtended extends BlockEntity implements IHasLastUser {
    private static final String KEY_USER_SAVE = "mnaop_lastUser";
    private UUID lastUser;

    public MixinLatestUserExtended() {
        super(null, null, null);
    }

    @Override
    public UUID getLastUserUUID() {
        return lastUser;
    }
    @Override
    public void setLastUser(Player newUser) {
        if (newUser.level().isClientSide) return;
        lastUser = newUser == null ? null : newUser.getUUID();
        setChanged();
    }

    @Inject(method = "load", at = @At("HEAD"))
    void injectLoad(CompoundTag nbt, CallbackInfo ci) {
        if (nbt.contains(KEY_USER_SAVE, Tag.TAG_INT_ARRAY)) {
            lastUser = NbtUtils.loadUUID(nbt.get(KEY_USER_SAVE));
        }
    }
    @Inject(method = "saveAdditional", at = @At("HEAD"))
    void injectSave(CompoundTag nbt, CallbackInfo ci) {
        if (lastUser != null) nbt.put(KEY_USER_SAVE, NbtUtils.createUUID(lastUser));
    }

    @Mixin({
            CircleFabricationMenu.class,
            GrandCircleFabricationMenu.class,
    })
    static class Menu_Style1 {
        @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/level/block/entity/BlockEntity;)V", at = @At("RETURN"), remap = false)
        void injectOpenMenu(int id, Inventory inv, BlockEntity entity, CallbackInfo ci) {
            ((IHasLastUser) entity).setLastUser(inv.player);
        }
    }
    @Mixin({
            FuseryMenu.class,
            GrandFuseryMenu.class,
            PrimeAggregatorMenu.class,
    })
    static class Menu_Style2 {
        @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/inventory/ContainerData;)V", at = @At("RETURN"), remap = false)
        void injectOpenMenu(int id, Inventory inv, BlockEntity entity, ContainerData data, CallbackInfo ci) {
            ((IHasLastUser) entity).setLastUser(inv.player);
        }
    }
}
