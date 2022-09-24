package yt.bebr0.minecraftserverbot.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import yt.bebr0.minecraftserverbot.bot.Bot;
import yt.bebr0.minecraftserverbot.data.Database;

/**
 * File is part of BeBrAPI. Thank you for using it! Also check out my YouTube channel where you can also leave your suggestions! <a href="https://www.youtube.com/c/BeBr0">...</a>
 *
 * @author BeBr0
 */
public class MinecraftChatEvent implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Database.BotUser botUser = Database.getInstance().getUserByMinecraftID(event.getPlayer().getUniqueId());

        if (botUser != null) {
            Bot.getInstance().sendMessageToDiscord(botUser.getDiscordId(), botUser.getMinecraftId(), event.getMessage());
        }
    }
}
