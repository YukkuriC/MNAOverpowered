package io.yukkuric.mnaop.mixin;

import com.mna.api.affinity.Affinity;
import com.mna.api.capabilities.IWellspringNodeRegistry;
import com.mna.capabilities.worlddata.WellspringNodeRegistry;
import io.yukkuric.mnaop.MNAOPConfig;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.UUID;

@Mixin(WellspringNodeRegistry.class)
public abstract class MixinWellspringSystem implements IWellspringNodeRegistry {
    @Shadow
    @Final
    private static HashMap<UUID, HashMap<Affinity, Float>> player_diminishing_returns;
    @Inject(method = "insertPowerDiminishing", at = @At("HEAD"), cancellable = true, remap = false)
    private void EmpoweredMatrix(UUID player, Level world, Affinity type, float amount, float diminish, CallbackInfoReturnable<Float> cir) {
        var empowered = MNAOPConfig.EmpoweredEldrinMatrix();
        var nonDiminishing = MNAOPConfig.NonDiminishingEldrinMatrix();
        if (!empowered && !nonDiminishing) return;
        float ret;
        if (nonDiminishing) {
            var mult = empowered ? getEldrinGenerationMultiplierFor(player, world, type) : 1;
            ret = insertPower(player, world, type, mult * amount);
        } else {
            // almost original implementation
            HashMap<Affinity, Float> levels;
            if (player_diminishing_returns.containsKey(player)) levels = player_diminishing_returns.get(player);
            else levels = player_diminishing_returns.getOrDefault(player, new HashMap<>());
            float factor = levels.getOrDefault(type, 1f);
            ret = this.insertPower(player, world, type, amount * factor);
            levels.put(type, factor * diminish);
            player_diminishing_returns.put(player, levels);
        }
        cir.setReturnValue(ret);
        cir.cancel();
    }
}
