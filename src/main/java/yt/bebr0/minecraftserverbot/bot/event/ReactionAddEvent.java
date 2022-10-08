package yt.bebr0.minecraftserverbot.bot.event;

import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import yt.bebr0.minecraftserverbot.bot.Bot;

public class ReactionAddEvent implements EventListener {

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof MessageReactionAddEvent) {
            MessageReactionAddEvent messageReactionAddEvent = (MessageReactionAddEvent) event;

            if (Bot.getInstance().isMemberRequested(messageReactionAddEvent.getUserId())) {
                if (messageReactionAddEvent.getChannel() instanceof PrivateChannel) {
                    PrivateChannel privateChannel = (PrivateChannel) messageReactionAddEvent.getChannel();

                    if (privateChannel.getUser().getId().equals(messageReactionAddEvent.getUserId())) {
                        if (messageReactionAddEvent.getEmoji().equals(Emoji.fromFormatted("✅"))) {
                            Bot.getInstance().grantVerification(privateChannel.getUser().getId());

                            messageReactionAddEvent.retrieveMessage().queue(message -> {
                                message.delete().queue();
                            });
                        }
                        else if (messageReactionAddEvent.getEmoji().equals(Emoji.fromFormatted("❌"))) {
                            Bot.getInstance().rejectVerification(privateChannel.getUser().getId());

                            messageReactionAddEvent.retrieveMessage().queue(message -> {
                                message.delete().queue();
                            });
                        }
                    }
                }
            }
        }
    }
}
