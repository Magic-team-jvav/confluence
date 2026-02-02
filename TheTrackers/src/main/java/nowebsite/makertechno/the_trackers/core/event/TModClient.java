package nowebsite.makertechno.the_trackers.core.event;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import nowebsite.makertechno.the_trackers.TheTrackers;
import nowebsite.makertechno.the_trackers.client.gui.TGui;
import nowebsite.makertechno.the_trackers.client.gui.TGuiStatics;
import nowebsite.makertechno.the_trackers.core.config.ConfigProcessor;
import nowebsite.makertechno.the_trackers.core.config.TConfig;
import nowebsite.makertechno.the_trackers.core.track.EntityTracker;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = TheTrackers.MOD_ID, value = Dist.CLIENT)
public final class TModClient {
    public static boolean isLoaded = false;
    @SubscribeEvent
    public static void onLoadComplete(FMLLoadCompleteEvent event) {
        isLoaded = true;
        TConfig.CRCursorWithEntities = ConfigProcessor.collectCREntityBindCursor(TConfig.CENTER_RELATIVE_BIND.get());
        TConfig.DTCursorWithEntities = ConfigProcessor.collectDTEntityBindCursor(TConfig.TRACK_FULL_BIND.get());
        EntityTracker.reCalcAllEntityGroups();
    }
    @SubscribeEvent
    public static void regRenderer(@NotNull RegisterGuiLayersEvent event){
        event.registerBelow(VanillaGuiLayers.CAMERA_OVERLAYS, TGuiStatics.POINTER, new TGui(Minecraft.getInstance()));
    }
}
