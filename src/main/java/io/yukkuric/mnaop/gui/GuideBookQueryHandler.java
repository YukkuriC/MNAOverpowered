// SPDX-License-Identifier: CC BY-NC-SA
// reference: https://github.com/VazkiiMods/Patchouli/blob/1.20.1/Xplat/src/main/java/vazkii/patchouli/client/handler/BookRightClickHandler.java

package io.yukkuric.mnaop.gui;

import com.mna.api.capabilities.CodexBreadcrumb.Type;
import com.mna.capabilities.playerdata.progression.PlayerProgressionProvider;
import com.mna.guide.GuideBookEntries;
import com.mna.guide.GuidebookEntry;
import com.mna.items.ItemInit;
import com.mojang.blaze3d.platform.Window;
import com.mojang.datafixers.util.Pair;
import io.yukkuric.mnaop.MNAOPConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class GuideBookQueryHandler {
    public static void renderQueryEntry(GuiGraphics graphics) {
        if (!MNAOPConfig.CodexArcanaShiftQuery()) return;
        var mc = Minecraft.getInstance();
        var player = mc.player;
        if (player == null || mc.screen != null) return;
        var bookStack = player.getMainHandItem();
        if (!bookStack.is(ItemInit.GUIDE_BOOK.get())) return;
        var hover = getHoveredEntry();
        if (hover == null) return;

        Window window = mc.getWindow();
        int x = window.getGuiScaledWidth() / 2 + 3;
        int y = window.getGuiScaledHeight() / 2 + 3;
        graphics.renderItem(hover.getSecond(), x, y);

        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 10);
        graphics.pose().scale(0.5F, 0.5F, 1);
        graphics.renderItem(bookStack, (x + 8) * 2, (y + 8) * 2);
        graphics.pose().popPose();

        graphics.drawString(mc.font, hover.getFirst().getFirstTitle(), x + 18, y + 3, 0xFFFFFF, false);

        graphics.pose().pushPose();
        graphics.pose().scale(0.75F, 0.75F, 1F);
        Component s = Component.translatable("mnaop.codex.query." + (player.isShiftKeyDown() ? "view" : "sneak")).withStyle(ChatFormatting.ITALIC);
        graphics.drawString(mc.font, s, (int) ((x + 18) / 0.75F), (int) ((y + 14) / 0.75F), 0xBBBBBB, false);
        graphics.pose().popPose();
    }

    public static void pushQueryEntry(Player player, Level world) {
        if (!MNAOPConfig.CodexArcanaShiftQuery()) return;
        if (!world.isClientSide || !player.isShiftKeyDown() || !player.getMainHandItem().is(ItemInit.GUIDE_BOOK.get()))
            return;
        var hover = getHoveredEntry();
        if (hover == null) return;
        player.getCapability(PlayerProgressionProvider.PROGRESSION).ifPresent((p) -> {
            p.clearCodexEntryHistory();
            p.pushCodexBreadcrumb(Type.ENTRY, hover.getFirst().getName(), 0);
        });
    }

    @Nullable
    private static Pair<GuidebookEntry, ItemStack> getHoveredEntry() {
        var mc = Minecraft.getInstance();
        if (mc.level == null) return null;

        // various hits
        ItemStack picked = ItemStack.EMPTY;
        if (mc.crosshairPickEntity != null) {
            var entity = mc.crosshairPickEntity;
            if (entity instanceof ItemFrame frame) picked = frame.getItem();
            else if (entity instanceof ItemEntity drop) picked = drop.getItem();
        } else if (mc.hitResult instanceof BlockHitResult hit) {
            var pos = hit.getBlockPos();
            var state = mc.level.getBlockState(pos);
            picked = state.getBlock().getCloneItemStack(mc.level, pos, state);
        }
        if (picked.isEmpty()) return null;

        // brute query from all entry recipes
        for (var kv : GuideBookEntries.INSTANCE.getAllEntries()) {
            var entry = kv.getValue();
            for (var recipe : entry.getRelatedRecipes())
                for (var output : recipe.getOutputItems(mc.level))
                    if (output.is(picked.getItem())) return Pair.of(entry, picked);
        }
        return null;
    }
}
