package io.yukkuric.mnaop.mixin;

import com.mna.api.entities.construct.IConstruct;
import com.mna.api.entities.construct.ai.ConstructEntityAreaTask;
import com.mna.entities.constructs.ai.ConstructMilkCow;
import io.yukkuric.mnaop.MNAOPConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(ConstructMilkCow.class)
public abstract class MixinConstructMilkCow extends ConstructEntityAreaTask<Cow, ConstructMilkCow> {
    public MixinConstructMilkCow(IConstruct<?> construct, ResourceLocation guiIcon, Class<Cow> entityClass) {
        super(construct, guiIcon, entityClass);
    }

    @Inject(method = "setNextMilkTime", at = @At("HEAD"), remap = false, cancellable = true)
    void setNextMilkTimeByMyself(Cow cow, CallbackInfo ci) {
        var cdInMinutes = MNAOPConfig.ConstructMilkingCooldown();
        if (cdInMinutes < 0) return;
        var base = 20 * 60 * cdInMinutes;
        cow.getPersistentData().putLong("last_milk_time", this.construct.asEntity().level().getGameTime() + (long) (base * (1 + Math.random())));
        ci.cancel();
    }
}
