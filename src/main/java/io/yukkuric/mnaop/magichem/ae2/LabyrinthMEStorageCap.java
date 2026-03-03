package io.yukkuric.mnaop.magichem.ae2;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.*;
import appeng.api.storage.MEStorage;
import appeng.capabilities.Capabilities;
import com.aranaira.magichem.block.entity.MirrorLabyrinthBlockEntity;
import com.aranaira.magichem.foundation.MagiChemBlockStateProperties;
import io.yukkuric.mnaop.MNAOPConfig;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static io.yukkuric.mnaop.magichem.ae2.AEHelpers.*;

public class LabyrinthMEStorageCap implements MEStorage, ICapabilityProvider {
    LazyOptional<?> myProvider;
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        if (!capability.equals(Capabilities.STORAGE)) return LazyOptional.empty();
        if (myProvider == null) myProvider = LazyOptional.of(() -> this);
        return myProvider.cast();
    }

    protected final MirrorLabyrinthBlockEntity master;
    protected boolean voidExcess;

    public LabyrinthMEStorageCap(MirrorLabyrinthBlockEntity storage) {
        master = storage;
    }

    static CompoundTag GLOB_NBT = new CompoundTag() {{
        putInt("CustomModelData", 1);
    }};

    public boolean isVoidExcess() {
        return MNAOPConfig.LabyrinthMEStorageVoidInput();
    }
    public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
        return toMateria(what) != null;
    }
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        var mat = toMateria(what);
        if (mat == null) return 0;
        var voidExcess = isVoidExcess();
        if (mode.isSimulate()) {
            if (voidExcess) return amount;
            return Math.max(0, Math.min(amount, master.getStorageLimit(mat) - master.getCurrentStock(mat)));
        }
        var inserted = amount - master.fill(mat, (int) amount, voidExcess); // inserted = target - overflow
        if (inserted > 0 && isBottled((AEItemKey) what)) { // handle bottles left
            var targetBottle = new ItemStack(Items.GLASS_BOTTLE, (int) inserted);
            if (source.player().isPresent()) {
                var player = source.player().get();
                if (!player.addItem(targetBottle))
                    popBottle(targetBottle, player.level(), player.position());
            } else {
                // SPLIT THEM MUAHAHAHA
                var centerPos = master.getBlockPos().offset(master.getBlockState().getValue(MagiChemBlockStateProperties.FACING).getNormal());
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
            return Math.min(amount, master.getCurrentStock(mat));
        }
        var extracted = master.drain(mat, (int) amount, false);
        return extracted;
    }
    public void getAvailableStacks(KeyCounter out) {
        for (var mat : master.getMateriaTypes())
            out.add(AEItemKey.of(mat, GLOB_NBT), master.getCurrentStock(mat));
    }
    public Component getDescription() {
        return Component.empty();
    }
}
