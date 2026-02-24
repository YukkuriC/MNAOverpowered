package io.yukkuric.mnaop.magichem.ae2;

import appeng.capabilities.Capabilities;
import com.aranaira.magichem.block.entity.PrimeAggregatorBlockEntity;
import com.aranaira.magichem.block.entity.routers.IRouterBlockEntity;
import com.aranaira.magichem.block.entity.routers.PrimeAggregatorRouterBlockEntity;
import io.yukkuric.mnaop.MNAOPHelpers;
import io.yukkuric.mnaop.magichem.ae2.impl.PrimeAggregatorCraftingCap;
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
    static void OnAttachForgeCap(AttachCapabilitiesEvent<BlockEntity> event) {
        var attachedBlock = event.getObject();
        if (attachedBlock instanceof PrimeAggregatorRouterBlockEntity router)
            event.addCapability(resLocCrafterCap, new RouterCapJumper(router));
        if (attachedBlock instanceof PrimeAggregatorBlockEntity pa)
            event.addCapability(resLocCrafterCap, new PrimeAggregatorCraftingCap(pa));
    }
    static class RouterCapJumper implements ICapabilityProvider {
        final IRouterBlockEntity router;
        RouterCapJumper(IRouterBlockEntity router) {
            this.router = router;
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
            if (!capability.equals(Capabilities.CRAFTING_MACHINE)) return LazyOptional.empty();
            return router.getMaster().getCapability(capability, direction);
        }
    }
}
