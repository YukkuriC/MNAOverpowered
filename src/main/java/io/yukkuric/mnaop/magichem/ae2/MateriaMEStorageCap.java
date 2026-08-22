package io.yukkuric.mnaop.magichem.ae2;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.*;
import appeng.api.storage.MEStorage;
import appeng.capabilities.Capabilities;
import com.aranaira.magichem.block.entity.MirrorLabyrinthBlockEntity;
import com.aranaira.magichem.block.entity.ext.AbstractMateriaStorageMultiTypeBlockEntity;
import com.aranaira.magichem.block.entity.ext.AbstractMateriaStorageSingleTypeBlockEntity;
import com.aranaira.magichem.foundation.MagiChemBlockStateProperties;
import com.aranaira.magichem.item.MateriaItem;
import io.yukkuric.mnaop.MNAOPConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static io.yukkuric.mnaop.magichem.ae2.AEHelpers.*;

public abstract class MateriaMEStorageCap<T extends BlockEntity> implements MEStorage, ICapabilityProvider {
    LazyOptional<?> myProvider;
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        if (!capability.equals(Capabilities.STORAGE)) return LazyOptional.empty();
        if (myProvider == null) myProvider = LazyOptional.of(() -> this);
        return myProvider.cast();
    }

    public T master;

    public MateriaMEStorageCap(T src) {
        master = src;
    }

    public BlockPos getPopBottlePos() {
        return master.getBlockPos();
    }
    public boolean isVoidExcess() {
        return false;
    }

    public abstract int getStorageLimit(MateriaItem mat);
    public abstract int getCurrentStock(MateriaItem mat);
    public abstract int fill(MateriaItem mat, int amount, boolean isVoid);
    public abstract int drain(MateriaItem mat, int amount);
    public abstract Collection<MateriaItem> getMateriaTypes();

    public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
        return toMateria(what) != null;
    }
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        var mat = toMateria(what);
        if (mat == null) return 0;
        var voidExcess = isVoidExcess();
        if (mode.isSimulate()) {
            if (voidExcess) return amount;
            return Math.max(0, Math.min(amount, getStorageLimit(mat) - getCurrentStock(mat)));
        }
        var inserted = amount - fill(mat, (int) amount, voidExcess); // inserted = target - overflow
        if (inserted > 0 && isBottled((AEItemKey) what)) { // handle bottles left
            var targetBottle = new ItemStack(Items.GLASS_BOTTLE, (int) inserted);
            if (source.player().isPresent()) {
                var player = source.player().get();
                if (!player.addItem(targetBottle)) popBottle(targetBottle, player.level(), player.position());
            } else {
                // SPLIT THEM MUAHAHAHA
                var centerPos = getPopBottlePos();
                popBottle(targetBottle, master.getLevel(), centerPos.getCenter());
            }
        }
        return inserted;
    }
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        var mat = toMateria(what);
        if (mat == null) return 0;
        if (!Objects.equals(GLOB_NBT, ((AEItemKey) what).getTag()))
            return 0; // deny not-matching stacks, including bottled
        if (mode.isSimulate()) {
            return Math.min(amount, getCurrentStock(mat));
        }
        return drain(mat, (int) amount);
    }
    public void getAvailableStacks(KeyCounter out) {
        for (var mat : getMateriaTypes())
            out.add(AEItemKey.of(mat, GLOB_NBT), getCurrentStock(mat));
    }
    public Component getDescription() {
        return Component.empty();
    }

    // 1-jar
    public static class Single<T extends AbstractMateriaStorageSingleTypeBlockEntity> extends MateriaMEStorageCap<T> {
        public Single(T src) {
            super(src);
        }
        boolean matchMaterial(MateriaItem mat) {
            var myMat = master.getMateriaType();
            return myMat == null || myMat == mat;
        }
        public int getStorageLimit(MateriaItem mat) {
            if (!matchMaterial(mat)) return 0;
            return master.getStorageLimit();
        }
        public int getCurrentStock(MateriaItem mat) {
            return master.getCurrentStock();
        }
        public int fill(MateriaItem mat, int amount, boolean isVoid) {
            if (!matchMaterial(mat)) return amount;
            return amount - master.insertMateria(new ItemStack(mat, amount));
        }
        public int drain(MateriaItem mat, int amount) {
            if (!matchMaterial(mat)) return 0;
            return master.extractMateria(amount, false).getCount();
        }
        public Collection<MateriaItem> getMateriaTypes() {
            var myMat = master.getMateriaType();
            if (myMat == null) return List.of();
            return List.of(myMat);
        }
    }

    // 4-jar & labyrinth
    public static class Multi<T extends AbstractMateriaStorageMultiTypeBlockEntity> extends MateriaMEStorageCap<T> {
        public Multi(T src) {
            super(src);
        }
        public int getStorageLimit(MateriaItem mat) {
            return master.getStorageLimit(mat);
        }
        public int getCurrentStock(MateriaItem mat) {
            return master.getCurrentStock(mat);
        }
        public int fill(MateriaItem mat, int amount, boolean isVoid) {
            return master.fill(mat, amount, isVoid);
        }
        public int drain(MateriaItem mat, int amount) {
            return master.drain(mat, amount, false);
        }
        public Collection<MateriaItem> getMateriaTypes() {
            return master.getMateriaTypes();
        }
    }
    public static class Labyrinth extends Multi<MirrorLabyrinthBlockEntity> {
        public Labyrinth(MirrorLabyrinthBlockEntity storage) {
            super(storage);
        }
        public boolean isVoidExcess() {
            return MNAOPConfig.LabyrinthMEStorageVoidInput();
        }
        public BlockPos getPopBottlePos() {
            return master.getBlockPos().offset(master.getBlockState().getValue(MagiChemBlockStateProperties.FACING).getNormal());
        }
    }
}
