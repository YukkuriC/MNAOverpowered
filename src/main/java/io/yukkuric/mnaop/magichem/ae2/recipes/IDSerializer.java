package io.yukkuric.mnaop.magichem.ae2.recipes;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Function;

public record IDSerializer<R extends Recipe<?>>(Function<ResourceLocation, R> getter) implements RecipeSerializer<R> {
    @Override
    public R fromJson(ResourceLocation id, JsonObject foo) {
        return getter.apply(id);
    }
    @Override
    public R fromNetwork(ResourceLocation id, FriendlyByteBuf foo) {
        return getter.apply(id);
    }
    @Override
    public void toNetwork(FriendlyByteBuf friendlyByteBuf, R r) {
    }
}
