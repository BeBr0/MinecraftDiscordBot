package yt.bebr0.minecraftserverbot.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import yt.bebr0.minecraftserverbot.data.Database;

/**
 * File is part of BeBrAPI. Thank you for using it! Also check out my YouTube channel where you can also leave your suggestions! https://www.youtube.com/c/BeBr0
 *
 * @author BeBr0
 */
public class PlayerFirstTimeJoinEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (Database.getInstance().getUserByMinecraftID(event.getPlayer().getUniqueId()) == null) {
            Database.getInstance().writeUser(event.getPlayer().getUniqueId().toString(), "");
        }
    }
}
