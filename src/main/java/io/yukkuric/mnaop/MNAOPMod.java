package io.yukkuric.mnaop;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MNAOPMod.MODID)
public class MNAOPMod {
    public static final String MODID = "mnaoverpowered";
    private static final Logger LOGGER = LogUtils.getLogger();

    public MNAOPMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MNAOPConfig.SPEC);
    }

    public static boolean ConfigGroupActive(String grp) {
        switch (grp) {
            case "MagiChem":
                return ModList.get().isLoaded("magichem");
        }
        return true;
    }

    /*@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // TODO client spec
        }
    }*/
}
