package yt.bebr0.minecraftserverbot.bot.event;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import yt.bebr0.minecraftserverbot.bot.Bot;
import yt.bebr0.minecraftserverbot.data.Database;

public class ChatEvent implements EventListener {

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof MessageReceivedEvent) {
            MessageReceivedEvent messageReceivedEvent = (MessageReceivedEvent) event;

            if (messageReceivedEvent.getMessage().getChannel() == Bot.getInstance().getChannel()) {

                Database.BotUser botUser = Database.getInstance().getUserByDiscordID(
                        messageReceivedEvent.getAuthor().getId()
                );

                if (botUser != null) {
                    Bot.getInstance().sendMessageToMinecraft(
                            botUser.getMinecraftId(),
                            botUser.getDiscordId(),
                            messageReceivedEvent.getMessage().getContentRaw()
                    );
                }
            }
        }
    }
}
