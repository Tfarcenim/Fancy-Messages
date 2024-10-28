package tfar.fancymessages;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.architectury.event.EventResult;
import dev.ftb.mods.ftbquests.api.QuestFile;
import dev.ftb.mods.ftbquests.command.QuestObjectArgument;
import dev.ftb.mods.ftbquests.events.ObjectCompletedEvent;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestObjectBase;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class FTBQuestsCompatNeoForge {

    public static void setup() {
        ObjectCompletedEvent.QUEST.register(FTBQuestsCompatNeoForge::onQuestComplete);
    }

    public static EventResult onQuestComplete(ObjectCompletedEvent.QuestEvent questEvent) {
        Quest quest = questEvent.getQuest();
        QuestFile questFile = quest.getQuestFile();
        String codeString = quest.getCodeString();
        List<ServerPlayer> getPlayers = questEvent.getNotifiedPlayers();
        for (ServerPlayer player : getPlayers) {
            FancyMessagesNeoForge.sendMessageGeneric("quests",codeString,player);
        }
        return EventResult.pass();
    }

    public static void addCommand(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
        builder.then(questNode(context));
    }

    static LiteralArgumentBuilder<CommandSourceStack> questNode(CommandBuildContext context) {
        return Commands.literal("quest")
                .then(Commands.argument("quest_object", QuestObjectArgument.questObject())
                        .then(Commands.argument("subtitle", ComponentArgument.textComponent(context))
                                .then(Commands.argument("message1", ComponentArgument.textComponent(context))
                                        .executes(context1 -> addQuest(context1, ComponentArgument.getComponent(context1, "message1")))
                                        .then(Commands.argument("message2", ComponentArgument.textComponent(context))
                                                .executes(context1 -> addQuest(context1, ComponentArgument.getComponent(context1, "message1"), ComponentArgument.getComponent(context1, "message2")))
                                                .then(Commands.argument("message3", ComponentArgument.textComponent(context))
                                                        .executes(context1 -> addQuest(context1, ComponentArgument.getComponent(context1, "message1"), ComponentArgument.getComponent(context1, "message2"), ComponentArgument.getComponent(context1, "message3")))
                                                )
                                        )
                                )
                        )
                );
    }

    static int addQuest(CommandContext<CommandSourceStack> context, Component... messages) {

        QuestObjectBase questObject = context.getArgument("quest_object", QuestObjectBase.class);
        Component componentSubtitle = ComponentArgument.getComponent(context, "subtitle");
        MessageHandler.getQuestMessages().put(questObject.getCodeString(),new MessageDisplay(componentSubtitle, List.of(messages)));

        MessageHandler.saveToFile(context.getSource().registryAccess());
        return 1;
    }

}
