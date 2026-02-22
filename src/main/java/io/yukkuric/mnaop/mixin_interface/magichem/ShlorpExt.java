package io.yukkuric.mnaop.mixin_interface.magichem;

import com.aranaira.magichem.entities.ShlorpEntity;
import io.yukkuric.mnaop.MNAOPConfig;

public class ShlorpExt {
    public static void EditShlorp(ShlorpEntity shlorp) {
        if (!MNAOPConfig.InstantConstructShlorps()) return;
        shlorp.setInstantPayload();
        shlorp.speed *= 10;
    }
}
