package io.yukkuric.mnaop.ritual.magichem;

import com.mna.api.rituals.RitualEffect;
import io.yukkuric.mnaop.ritual.MNAOPRituals;

public class MagiChemOPRituals extends MNAOPRituals {
    public static void load() {
    }

    public static final RitualEffect FORGET_RELIC = create("magichem/forget_relic", RitualEffectForgetRelic::new);
}
