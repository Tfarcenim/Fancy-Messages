package tfar.fancymessages;


import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import tfar.fancymessages.network.client.S2CMultilinePacket;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod(FancyMessages.MOD_ID)
public class FancyMessagesNeoForge {

    public FancyMessagesNeoForge(IEventBus eventBus, Dist dist) {
        eventBus.addListener(PacketHandlerNeoForge::register);
        NeoForge.EVENT_BUS.addListener(this::commands);
        NeoForge.EVENT_BUS.addListener(this::reload);
        NeoForge.EVENT_BUS.addListener(this::onAdvancement);
        NeoForge.EVENT_BUS.addListener(this::onBiomeEnter);
        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.
        if (dist.isClient()) {
            ModClientNeoForge.init(eventBus);
        }
        // Use NeoForge to bootstrap the Common mod.
        FancyMessages.init();
    }

    void commands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher(), event.getBuildContext());
    }

    void reload(AddReloadListenerEvent event) {
        event.addListener(new FancyMessagesReloadListener(event.getRegistryAccess()));
    }

    void onAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
        ResourceLocation resourceLocation = event.getAdvancement().id();
        ServerPlayer player = (ServerPlayer) event.getEntity();
        sendMessage(MessageHandler.ADV,resourceLocation,player);
    }

    Map<UUID, Holder<Biome>> previous = new HashMap<>();

    void onBiomeEnter(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            UUID uuid = player.getUUID();
            BlockPos pos = serverPlayer.blockPosition();
            Holder<Biome> biomeHolder = serverPlayer.serverLevel().getBiome(pos);
            if (previous.containsKey(uuid)) {
                if (previous.get(uuid) != biomeHolder) {
                    sendMessage("biomes",ResourceLocation.parse(biomeHolder.getRegisteredName()), serverPlayer);
                    previous.put(uuid, biomeHolder);
                }
            } else {
                previous.put(uuid,biomeHolder);
            }
        }
    }

    void sendMessage(String category,ResourceLocation location,ServerPlayer player) {

        MessageDisplay display = null;
        switch (category) {
            case MessageHandler.ADV ->{
               display = MessageHandler.getAdvancementMessages().get(location);
            }
            case "biomes" ->{
                display = MessageHandler.getBiomeMessages().get(location);
            }
            case "quests"-> {

            }
        }


        if (display != null) {
            try {
                ClientboundSetTitleTextPacket tpacket = new ClientboundSetTitleTextPacket(ComponentUtils.updateForEntity(player.getServer().createCommandSourceStack(), Component.empty(), player, 0));
                ClientboundSetSubtitleTextPacket packet = new ClientboundSetSubtitleTextPacket(ComponentUtils.updateForEntity(player.getServer().createCommandSourceStack(),display.subtitle(), player, 0));

                S2CMultilinePacket.send(display.messages(),player);

                player.connection.send(tpacket);
                player.connection.send(packet);
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}