package yt.bebr0.minecraftserverbot.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import yt.bebr0.minecraftserverbot.bot.Bot;
import yt.bebr0.minecraftserverbot.database.Database;

/**
 * File is part of BeBrAPI. Thank you for using it! Also check out my YouTube channel where you can also leave your suggestions! <a href="https://www.youtube.com/c/BeBr0">...</a>
 *
 * @author BeBr0
 */
public class MinecraftChatEvent implements Listener {

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Database.BotUser botUser = Database.getInstance().getUserByMinecraftID(event.getPlayer().getUniqueId());

        if (botUser != null) {
            Bot.getInstance().sendMessageToDiscord(botUser.getDiscordId(), botUser.getMinecraftId(), event.message().toString());
        }
    }
}
