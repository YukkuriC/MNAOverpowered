package io.yukkuric.mnaop.magichem.ae2;

import com.aranaira.magichem.block.entity.PrimeAggregatorBlockEntity;
import com.aranaira.magichem.block.entity.ext.AbstractFabricationBlockEntity;
import io.yukkuric.mnaop.MNAOPHelpers;
import io.yukkuric.mnaop.magichem.ae2.impl.FabricationCraftingCap;
import io.yukkuric.mnaop.magichem.ae2.impl.PrimeAggregatorCraftingCap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.Bindings;

public class MagiChemAE2Interop {
    public static void Init() {
        var forgeBus = Bindings.getForgeBus().get();
        forgeBus.addGenericListener(BlockEntity.class, MagiChemAE2Interop::OnAttachForgeCap);
    }

    static ResourceLocation resLocCrafterCap = MNAOPHelpers.modLoc("crafter");
    static void OnAttachForgeCap(AttachCapabilitiesEvent<BlockEntity> event) {
        var attachedBlock = event.getObject();
        if (attachedBlock instanceof PrimeAggregatorBlockEntity pa)
            event.addCapability(resLocCrafterCap, new PrimeAggregatorCraftingCap(pa));
        if (attachedBlock instanceof AbstractFabricationBlockEntity cf)
            event.addCapability(resLocCrafterCap, new FabricationCraftingCap(cf));
    }
}
