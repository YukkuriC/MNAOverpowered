package io.yukkuric.mnaop.magichem;

import io.yukkuric.mnaop.MNAOPConfig;
import io.yukkuric.mnaop.magichem.tooltip.DistillationResultsTooltip;
import io.yukkuric.mnaop.magichem.tooltip.ReadableAdmixtureTooltip;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MagiChemEvents {
    @SubscribeEvent
    public static void HandleTooltips(ItemTooltipEvent e) {
        if (MNAOPConfig.ShowReadableAdmixtureFormula()) ReadableAdmixtureTooltip.HandleTooltips(e);
        if (MNAOPConfig.ShowDistillationResults()) DistillationResultsTooltip.HandleTooltips(e);
    }

    @SubscribeEvent
    public static void HandleRecipesUpdate(RecipesUpdatedEvent e) {
        // no formula hotswap yet
        DistillationResultsTooltip.RefreshRecipes();
    }

    public static void HandleResourcesReload() {
        ReadableAdmixtureTooltip.RefreshDisplayCache();
        DistillationResultsTooltip.RefreshDisplayCache();
    }
}
