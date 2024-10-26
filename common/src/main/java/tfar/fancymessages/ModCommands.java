package tfar.fancymessages;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.AdvancementCommands;

public class ModCommands {

    ///prettymsg create onAchievement (achievement name) (custom message)
    //So when a player gets an achievement, they now see a popup with this message
    //
    ///prettymsg create biome (biome name) (custom message)
    //While this one checks for a player entering a biome, and then sends a popup message.

    //I'd like this to have optional support for FTB Quests so it can detect when a player finishes a quest, and send a message for example:
    ///prettymsg create quest (quest name) (custom message)

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal("prettymsg")
                .then(Commands.literal("create")
                        .then(Commands.literal("advancement")
                                .then(Commands.argument("advancement", ResourceLocationArgument.id())
                                        .suggests(AdvancementCommands.SUGGEST_ADVANCEMENTS)
                                        .then(Commands.argument("message", ComponentArgument.textComponent(context))
                                                .executes(ModCommands::addAchievement)
                                        )
                                )
                        )
                        .then(Commands.literal("biome")
                                .then(Commands.argument("biome", ResourceArgument.resource(context, Registries.BIOME))
                                        .then(Commands.argument("message", ComponentArgument.textComponent(context))
                                                .executes(ModCommands::addBiome)
                                        )
                                )
                        )
                        .then(Commands.literal("quest")
                                .then(Commands.argument("quest", ResourceLocationArgument.id())
                                        .then(Commands.argument("message", ComponentArgument.textComponent(context))
                                                .suggests(AdvancementCommands.SUGGEST_ADVANCEMENTS)
                                                .executes(ModCommands::addQuest)
                                        )
                                )
                        )
                )
        );
    }

    static int addAchievement(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        AdvancementHolder advancement = ResourceLocationArgument.getAdvancement(context, "advancement");
        Component component = ComponentArgument.getComponent(context,"message");
        MessageHandler.getAdvancementMessages().put(advancement.id(),component);

        MessageHandler.saveToFile();
        return 1;
    }

    static int addBiome(CommandContext<CommandSourceStack> context) {
        return 1;
    }

    static int addQuest(CommandContext<CommandSourceStack> context) {
        return 1;
    }

}
