package io.yukkuric.mnaop.magichem.ae2;

import appeng.capabilities.Capabilities;
import com.aranaira.magichem.block.entity.*;
import com.aranaira.magichem.block.entity.ext.AbstractFabricationBlockEntity;
import com.aranaira.magichem.block.entity.ext.AbstractMateriaStorageMultiTypeBlockEntity;
import io.yukkuric.mnaop.MNAOPHelpers;
import io.yukkuric.mnaop.magichem.ae2.impl.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.Bindings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MagiChemAE2Interop {
    public static void Init() {
        var forgeBus = Bindings.getForgeBus().get();
        forgeBus.addGenericListener(BlockEntity.class, MagiChemAE2Interop::OnAttachForgeCap);
    }

    static ResourceLocation resLocCrafterCap = MNAOPHelpers.modLoc("crafter");
    static ResourceLocation resLocMateriaStorageCap = MNAOPHelpers.modLoc("storage");
    static void OnAttachForgeCap(AttachCapabilitiesEvent<BlockEntity> event) {
        var attachedBlock = event.getObject();
        if (attachedBlock instanceof PrimeAggregatorBlockEntity pa)
            event.addCapability(resLocCrafterCap, new PrimeAggregatorCraftingCap(pa));
        if (attachedBlock instanceof AbstractFabricationBlockEntity cf)
            event.addCapability(resLocCrafterCap, new FabricationCraftingCap(cf));
        if (attachedBlock instanceof AbstractMateriaStorageMultiTypeBlockEntity matStorage) {
            if (matStorage instanceof MirrorLabyrinthBlockEntity labyrinth) {
                event.addCapability(resLocMateriaStorageCap, new LabyrinthMEStorageCap(labyrinth));
            } else if (matStorage instanceof MagicMirrorBlockEntity mirror) {
                event.addCapability(resLocMateriaStorageCap, new ICapabilityProvider() {
                    @Override
                    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
                        if (capability == Capabilities.STORAGE) {
                            var labyrinth = mirror.getMaster();
                            if (labyrinth != null) return labyrinth.getCapability(capability, direction);
                        }
                        return LazyOptional.empty();
                    }
                });
            }
        }
    }
}
