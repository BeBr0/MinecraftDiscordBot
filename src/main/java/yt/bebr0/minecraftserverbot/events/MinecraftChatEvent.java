package yt.bebr0.minecraftserverbot.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import yt.bebr0.minecraftserverbot.bot.Bot;
import yt.bebr0.minecraftserverbot.data.Database;
import yt.bebr0.minecraftserverbot.util.ChatUtil;

public class MinecraftChatEvent implements Listener {

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Database.BotUser botUser = Database.getInstance().getUserByMinecraftID(event.getPlayer().getUniqueId());

        if (botUser != null) {
            Bot.getInstance().sendMessageToDiscord(
                    botUser.getDiscordId(),
                    botUser.getMinecraftId(),
                    ((TextComponent) event.message()).content()
            );
        }
        else {
            Bot.getInstance().sendMessageToDiscord(
                    "",
                    event.getPlayer().getUniqueId().toString(),
                    ((TextComponent) event.message()).content()
            );
        }

        TextComponent textComponent = (TextComponent) event.message();

        ChatUtil.getInstance().broadcast(textComponent.content(), event.getPlayer().getUniqueId(), event.getPlayer().displayName());

        event.setCancelled(true);
    }
}
