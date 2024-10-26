package tfar.fancymessages;


import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;

@Mod(FancyMessages.MOD_ID)
public class FancyMessagesNeoForge {

    public FancyMessagesNeoForge(IEventBus eventBus) {
        NeoForge.EVENT_BUS.addListener(this::commands);
        NeoForge.EVENT_BUS.addListener(this::reload);
        NeoForge.EVENT_BUS.addListener(this::onAdvancement);
        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.

        // Use NeoForge to bootstrap the Common mod.
        FancyMessages.init();
    }

    void commands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher(), event.getBuildContext());
    }


    void reload(AddReloadListenerEvent event) {
        event.addListener(new FancyMessagesReloadListener());
    }

    void onAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
        ResourceLocation resourceLocation = event.getAdvancement().id();
        Component message = MessageHandler.getAdvancementMessages().get(resourceLocation);
        ServerPlayer player = (ServerPlayer) event.getEntity();
        if (message != null) {
            try {
                ClientboundSetSubtitleTextPacket packet = new ClientboundSetSubtitleTextPacket(ComponentUtils.updateForEntity(player.getServer().createCommandSourceStack(), message, player, 0));
                player.connection.send(packet);
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}