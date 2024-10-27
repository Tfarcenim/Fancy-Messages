package tfar.fancymessages.network.client;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import tfar.fancymessages.ModClient;
import tfar.fancymessages.network.ModPacket;
import tfar.fancymessages.network.S2CModPacket;
import tfar.fancymessages.platform.Services;

import java.util.List;


public record S2CMultilinePacket(List<Component> lines) implements S2CModPacket<RegistryFriendlyByteBuf> {

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CMultilinePacket> STREAM_CODEC =
            ModPacket.streamCodec(S2CMultilinePacket::new);


    public static final CustomPacketPayload.Type<S2CMultilinePacket> TYPE = ModPacket.type(S2CMultilinePacket.class);


    public S2CMultilinePacket(RegistryFriendlyByteBuf buf) {
        this(buf.readList(buffer -> ComponentSerialization.TRUSTED_STREAM_CODEC.decode(buf)));
    }

    @Override
    public void handleClient() {
        ModClient.setDisplayLines(lines);
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeCollection(lines, (buffer, value) -> ComponentSerialization.TRUSTED_STREAM_CODEC.encode(buf,value));
    }

    public static void send(List<Component> lines, ServerPlayer player) {
        Services.PLATFORM.sendToClient(new S2CMultilinePacket(lines),player);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}