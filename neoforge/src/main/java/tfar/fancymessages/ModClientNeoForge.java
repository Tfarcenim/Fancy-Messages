package tfar.fancymessages;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.LayeredDraw;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public class ModClientNeoForge {

    static final LayeredDraw.Layer layer = (guiGraphics, deltaTracker) -> ModClient.displayLineMessage(Minecraft.getInstance().gui,guiGraphics,deltaTracker);

    static void init(IEventBus bus) {
        bus.addListener(ModClientNeoForge::layers);
    }

    static void layers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.OVERLAY_MESSAGE,FancyMessages.id("overlay"),layer);
    }
}
