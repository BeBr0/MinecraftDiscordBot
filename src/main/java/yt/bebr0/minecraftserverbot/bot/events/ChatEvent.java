package yt.bebr0.minecraftserverbot.bot.events;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import yt.bebr0.minecraftserverbot.Plugin;
import yt.bebr0.minecraftserverbot.bot.Bot;

public class ChatEvent implements EventListener {
    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof MessageReceivedEvent) {
            MessageReceivedEvent messageReceivedEvent = (MessageReceivedEvent) event;

            messageReceivedEvent.getAuthor()
            if (messageReceivedEvent.getMessage().getChannel().getName().equals(Bot.getInstance().getSyncChatName())) {
                Bot.getInstance().sendMessageToMinecraft();
            }
        }
    }
}
