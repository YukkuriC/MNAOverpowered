package io.yukkuric.mnaop.ritual;

import com.mna.api.rituals.RitualEffect;
import io.yukkuric.mnaop.MNAOPMod;
import io.yukkuric.mnaop.mixin_interface.MNAOPMixinPlugin;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegisterEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static io.yukkuric.mnaop.MNAOPHelpers.modLoc;

public class MNAOPRituals {
    protected static final Map<ResourceLocation, RitualEffect> RITUALS = new HashMap<>();
    protected static RitualEffect create(String path, Function<ResourceLocation, RitualEffect> getter) {
        if (MNAOPMixinPlugin.isRitualEffectDenied(path)) {
            MNAOPMod.LOGGER.warn("ritual {} denied; will not take effect", path);
            return null;
        }
        var key = modLoc("rituals/" + path);
        var effect = getter.apply(key);
        RITUALS.put(key, effect);
        return effect;
    }

    public static final RitualEffect CREATE_WELLSPRING = create("create_wellspring", RitualEffectChangeWellspring.Create::new);
    public static final RitualEffect DESTROY_WELLSPRING = create("destroy_wellspring", RitualEffectChangeWellspring.Destroy::new);

    public static void register(RegisterEvent.RegisterHelper<RitualEffect> helper) {
        for (var pair : RITUALS.entrySet()) helper.register(pair.getKey(), pair.getValue());
    }
}
