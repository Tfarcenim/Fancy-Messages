package tfar.fancymessages;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.AdvancementCommands;
import net.minecraft.world.level.biome.Biome;
import tfar.fancymessages.platform.Services;

import java.util.List;

public class ModCommands {

    ///prettymsg create onAchievement (achievement name) (custom messages)
    //So when a player gets an achievement, they now see a popup with this messages
    //
    ///prettymsg create biome (biome name) (custom messages)
    //While this one checks for a player entering a biome, and then sends a popup messages.

    //I'd like this to have optional support for FTB Quests so it can detect when a player finishes a quest, and send a messages for example:
    ///prettymsg create quest (quest name) (custom messages)

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {


        LiteralArgumentBuilder<CommandSourceStack> create =
                Commands.literal("create")
                        .then(advancementNode(context))
                        .then(biomeNode(context));

        if (FancyMessages.FTBQUESTS) {
            Services.PLATFORM.addOptionalCommands(create, context);
        }

        dispatcher.register(Commands.literal("prettymsg")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(Commands.LEVEL_ADMINS))
                .then(create));


    }

    static LiteralArgumentBuilder<CommandSourceStack> advancementNode(CommandBuildContext context) {
        return Commands.literal("advancement")
                .then(Commands.argument("advancement", ResourceLocationArgument.id())
                        .suggests(AdvancementCommands.SUGGEST_ADVANCEMENTS)
                        .then(Commands.argument("subtitle", ComponentArgument.textComponent(context))
                                .then(Commands.argument("message1", ComponentArgument.textComponent(context))
                                        .executes(context1 -> addAchievement(context1, ComponentArgument.getComponent(context1, "message1")))
                                        .then(Commands.argument("message2", ComponentArgument.textComponent(context))
                                                .executes(context1 -> addAchievement(context1, ComponentArgument.getComponent(context1, "message1"), ComponentArgument.getComponent(context1, "message2")))
                                                .then(Commands.argument("message3", ComponentArgument.textComponent(context))
                                                        .executes(context1 -> addAchievement(context1, ComponentArgument.getComponent(context1, "message1"), ComponentArgument.getComponent(context1, "message2"), ComponentArgument.getComponent(context1, "message3")))
                                                )
                                        )
                                )
                        )
                );
    }

    static LiteralArgumentBuilder<CommandSourceStack> biomeNode(CommandBuildContext context) {
        return Commands.literal("biome")
                .then(Commands.argument("biome", ResourceArgument.resource(context, Registries.BIOME))
                        .then(Commands.argument("subtitle", ComponentArgument.textComponent(context))
                                .then(Commands.argument("message1", ComponentArgument.textComponent(context))
                                        .executes(context1 -> addBiome(context1, ComponentArgument.getComponent(context1, "message1")))
                                        .then(Commands.argument("message2", ComponentArgument.textComponent(context))
                                                .executes(context1 -> addBiome(context1, ComponentArgument.getComponent(context1, "message1"), ComponentArgument.getComponent(context1, "message2")))
                                                .then(Commands.argument("message3", ComponentArgument.textComponent(context))
                                                        .executes(context1 -> addBiome(context1, ComponentArgument.getComponent(context1, "message1"), ComponentArgument.getComponent(context1, "message2"), ComponentArgument.getComponent(context1, "message3")))
                                                )
                                        )
                                )
                        )
                );
    }

    static LiteralArgumentBuilder<CommandSourceStack> questNode(CommandBuildContext context) {
        return Commands.literal("biome")
                .then(Commands.argument("biome", ResourceArgument.resource(context, Registries.BIOME))
                        .then(Commands.argument("subtitle", ComponentArgument.textComponent(context))
                                .then(Commands.argument("message1", ComponentArgument.textComponent(context))
                                        .executes(context1 -> addBiome(context1, ComponentArgument.getComponent(context1, "message1")))
                                        .then(Commands.argument("message2", ComponentArgument.textComponent(context))
                                                .executes(context1 -> addBiome(context1, ComponentArgument.getComponent(context1, "message1"), ComponentArgument.getComponent(context1, "message2")))
                                                .then(Commands.argument("message3", ComponentArgument.textComponent(context))
                                                        .executes(context1 -> addBiome(context1, ComponentArgument.getComponent(context1, "message1"), ComponentArgument.getComponent(context1, "message2"), ComponentArgument.getComponent(context1, "message3")))
                                                )
                                        )
                                )
                        )
                );
    }

    static int addAchievement(CommandContext<CommandSourceStack> context, Component... messages) throws CommandSyntaxException {
        AdvancementHolder advancement = ResourceLocationArgument.getAdvancement(context, "advancement");
        Component componentSubtitle = ComponentArgument.getComponent(context, "subtitle");

        MessageHandler.getAdvancementMessages().put(advancement.id(), new MessageDisplay(componentSubtitle, List.of(messages)));

        MessageHandler.saveToFile(context.getSource().registryAccess());
        return 1;
    }

    static int addBiome(CommandContext<CommandSourceStack> context, Component... messages) throws CommandSyntaxException {
        Holder.Reference<Biome> biome = ResourceArgument.getResource(context, "biome", Registries.BIOME);
        Component componentSubtitle = ComponentArgument.getComponent(context, "subtitle");

        MessageHandler.getBiomeMessages().put(biome.key().location(), new MessageDisplay(componentSubtitle, List.of(messages)));

        MessageHandler.saveToFile(context.getSource().registryAccess());
        return 1;
    }

}
