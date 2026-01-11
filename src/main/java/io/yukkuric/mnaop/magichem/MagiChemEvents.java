package io.yukkuric.mnaop.magichem;

import io.yukkuric.mnaop.MNAOPConfig;
import io.yukkuric.mnaop.magichem.tooltip.DistillationResultsTooltip;
import io.yukkuric.mnaop.magichem.tooltip.ReadableAdmixtureTooltip;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class MagiChemEvents {
    public static void HandleTooltips(ItemTooltipEvent e) {
        if (MNAOPConfig.ShowReadableAdmixtureFormula()) ReadableAdmixtureTooltip.HandleTooltips(e);
        if (MNAOPConfig.ShowDistillationResults()) DistillationResultsTooltip.HandleTooltips(e);
    }

    public static void HandleLoggedIn(PlayerEvent.PlayerLoggedInEvent e) {
        DistillationResultsTooltip.HandleLoggedIn(e);
    }
}
