package tfar.fancymessages.network;

import net.minecraft.resources.ResourceLocation;
import tfar.fancymessages.FancyMessages;
import tfar.fancymessages.network.client.S2CMultilinePacket;
import tfar.fancymessages.platform.Services;

import java.util.Locale;

public class PacketHandler {


    public static void registerPackets() {
        ///////server to client

        Services.PLATFORM.registerClientPlayPacket(S2CMultilinePacket.TYPE, S2CMultilinePacket.STREAM_CODEC);

    }

    public static ResourceLocation packet(Class<?> clazz) {
        return FancyMessages.id(clazz.getName().toLowerCase(Locale.ROOT));
    }
}
