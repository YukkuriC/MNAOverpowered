package io.yukkuric.mnaop.magichem.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class TooltipHelpers {
    private static final String[] smallNums = new String[]{"₀", "₁", "₂", "₃", "₄", "₅", "₆", "₇", "₈", "₉"};
    // AdmixtureItem.sub
    public static String toSmallNum(int num) {
        if (num < 0) num = 0;
        if (num < 10) return smallNums[num];
        return toSmallNum(num / 10) + smallNums[num % 10];
    }

    public static ResourceLocation getItemId(ItemStack stack) {
        var reg = ForgeRegistries.ITEMS;
        return reg.getKey(stack.getItem());
    }

    public static Component NOPE = Component.literal("NOPE").withStyle(ChatFormatting.GOLD);
    public static Component SPACE = Component.literal(" ");
    public static Component BR_L = Component.literal("(");
    public static Component BR_R = Component.literal(")");
}
