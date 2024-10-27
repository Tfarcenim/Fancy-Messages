package tfar.fancymessages;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class ModClient {

    public static List<Component> displayLines = new ArrayList<>();

    public static void setDisplayLines(List<Component> displayLines) {
        ModClient.displayLines = displayLines;
        Minecraft.getInstance().gui.overlayMessageTime = 100;
    }

    public static void displayLineMessage(Gui gui, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Font font = gui.getFont();
        if (!displayLines.isEmpty() && gui.overlayMessageTime > 0) {
            gui.minecraft.getProfiler().push("displayLines");
            float f = gui.overlayMessageTime - deltaTracker.getGameTimeDeltaPartialTick(false);
            int i = (int)(f * 255.0F / 20.0F);
            if (i > 255) {
                i = 255;
            }

            if (i > 8) {
                int lines = displayLines.size();
                for (int l = 0; l < lines; l++) {
                    Component component = displayLines.get(l);

                    //increasing this shifts it up
                    int yShift = 65 + lines * 6 - l * 9;//Math.max(leftHeight, rightHeight) + (68 - 59);
                    guiGraphics.pose().pushPose();

                    guiGraphics.pose().translate((float) (guiGraphics.guiWidth() / 2), (float) (guiGraphics.guiHeight() - yShift), 0.0F);
                    int color;
                    if (gui.animateOverlayMessageColor) {
                        color = Mth.hsvToArgb(f / 50.0F, 0.7F, 0.6F, i);
                    } else {
                        color = FastColor.ARGB32.color(i, -1);
                    }

                    int width = font.width(component);
                    guiGraphics.drawStringWithBackdrop(font, component, -width / 2, -4, width, color);
                    guiGraphics.pose().popPose();
                }
            }
            gui.minecraft.getProfiler().pop();
        }
    }

}
