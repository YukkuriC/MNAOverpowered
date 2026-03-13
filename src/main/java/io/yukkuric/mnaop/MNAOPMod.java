package io.yukkuric.mnaop;

import com.mna.Registries;
import com.mojang.logging.LogUtils;
import io.yukkuric.mnaop.command.MNAOPCommands;
import io.yukkuric.mnaop.gui.GuideBookQueryHandler;
import io.yukkuric.mnaop.magichem.MagiChemEvents;
import io.yukkuric.mnaop.magichem.ae2.MagiChemAE2Interop;
import io.yukkuric.mnaop.magichem.cc.MagiChemCCInterop;
import io.yukkuric.mnaop.ritual.MNAOPRituals;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;

import static io.yukkuric.mnaop.MNAOPHelpers.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MNAOPMod.MODID)
public class MNAOPMod {
    public static final String MODID = "mnaoverpowered";
    public static final Logger LOGGER = LogUtils.getLogger();

    @SuppressWarnings("removal")
    public MNAOPMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus eventBus = MinecraftForge.EVENT_BUS;
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MNAOPConfig.SPEC);
        eventBus.addListener((RegisterCommandsEvent event) -> MNAOPCommands.register(event.getDispatcher()));

        // register
        modBus.addListener((RegisterEvent e) -> {
            e.register(Registries.RitualEffect.get().getRegistryKey(), MNAOPRituals::register);
        });

        // interop
        if (IsMagiChemLoaded()) {
            if (IsModLoaded("computercraft")) MagiChemCCInterop.Init();
            if (IsAE2Loaded()) MagiChemAE2Interop.Init();
        }
    }

    public static boolean ConfigGroupActive(String grp) {
        if (grp.contains("_")) {
            for (var sub : grp.split("_")) if (!ConfigGroupActive(sub)) return false;
            return true;
        }
        switch (grp) {
            case "MagiChem":
                return IsMagiChemLoaded();
            case "AE2":
                return IsAE2Loaded();
        }
        return true;
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // client spec
            if (IsMagiChemLoaded()) {
                MinecraftForge.EVENT_BUS.addListener(MagiChemEvents::HandleTooltips);
                MinecraftForge.EVENT_BUS.addListener(MagiChemEvents::HandleLoggedIn);
            }

            // book shift-click
            MinecraftForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock e) -> GuideBookQueryHandler.pushQueryEntry(e.getEntity(), e.getLevel()));
        }
        @SubscribeEvent
        public static void registerOverlays(RegisterGuiOverlaysEvent e) {
            e.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "codex_query",
                    (gui, poseStack, partialTick, width, height) -> GuideBookQueryHandler.renderQueryEntry(poseStack)
            );
        }
    }
}
