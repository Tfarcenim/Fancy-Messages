package tfar.fancymessages;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import tfar.fancymessages.network.PacketHandler;
import tfar.fancymessages.platform.NeoForgePlatformHelper;

public class PacketHandlerNeoForge {

    public static void register(RegisterPayloadHandlersEvent event){
        NeoForgePlatformHelper.registrar = event.registrar(FancyMessages.MOD_ID);
        PacketHandler.registerPackets();
    }

    public static void sendToClient(CustomPacketPayload packet, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player,packet);
    }

    public static void sendToServer(CustomPacketPayload packet) {
        PacketDistributor.sendToServer(packet);
    }
}
