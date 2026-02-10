package io.yukkuric.mnaop;

import com.mojang.logging.LogUtils;
import io.yukkuric.mnaop.command.MNAOPCommands;
import io.yukkuric.mnaop.gui.GuideBookQueryHandler;
import io.yukkuric.mnaop.magichem.MagiChemEvents;
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
import org.slf4j.Logger;

import static io.yukkuric.mnaop.MNAOPHelpers.IsMagiChemLoaded;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MNAOPMod.MODID)
public class MNAOPMod {
    public static final String MODID = "mnaoverpowered";
    public static final Logger LOGGER = LogUtils.getLogger();

    @SuppressWarnings("removal")
    public MNAOPMod() {
        // IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus eventBus = MinecraftForge.EVENT_BUS;
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MNAOPConfig.SPEC);
        eventBus.addListener((RegisterCommandsEvent event) -> MNAOPCommands.register(event.getDispatcher()));
    }

    public static boolean ConfigGroupActive(String grp) {
        switch (grp) {
            case "MagiChem":
                return IsMagiChemLoaded();
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
