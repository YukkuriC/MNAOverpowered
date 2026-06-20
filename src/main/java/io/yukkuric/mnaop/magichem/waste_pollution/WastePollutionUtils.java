package io.yukkuric.mnaop.magichem.waste_pollution;

import com.mna.capabilities.chunkdata.ChunkMagicProvider;
import io.yukkuric.mnaop.MNAOPConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;

public class WastePollutionUtils {
    static int getChunkRandomOffset() {
        int r = MNAOPConfig.WastePollutionSpreadChunkRange();
        if (r <= 0) return 0;
        int range = r * 2 + 1;
        return -r + (int) Math.floor(Math.random() * range);
    }

    public static void gen(Level level, BlockPos pos, float amount, int repeat) {
        int cx = SectionPos.blockToSectionCoord(pos.getX());
        int cz = SectionPos.blockToSectionCoord(pos.getZ());
        for (; repeat > 0; repeat--) {
            int ccx = cx + getChunkRandomOffset();
            int ccz = cz + getChunkRandomOffset();
            var chunk = level.getChunk(ccx, ccz);
            chunk.getCapability(ChunkMagicProvider.MAGIC).ifPresent(cm -> {
                cm.addResidualMagic(amount);
            });
        }
    }
}
