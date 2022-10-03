package yt.bebr0.minecraftserverbot.bot.event;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import yt.bebr0.minecraftserverbot.bot.Bot;
import yt.bebr0.minecraftserverbot.data.Database;

import java.util.UUID;

/**
 * File is part of BeBrAPI. Thank you for using it! Also check out my YouTube channel where you can also leave your suggestions! https://www.youtube.com/c/BeBr0
 *
 * @author BeBr0
 */
public class RoleUpdateEvent implements EventListener {

    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if (genericEvent instanceof GuildMemberRoleAddEvent) {
            GuildMemberRoleAddEvent event = (GuildMemberRoleAddEvent) genericEvent;

            Database.BotUser botUser = Database.getInstance().getUserByDiscordID(event.getUser().getId());
            if (botUser != null) {
                String id = botUser.getMinecraftId();

                if (id != null) {
                    replaceTabListName(botUser.getMinecraftId(), event.getUser());
                }
            }
        }
        else if (genericEvent instanceof GuildMemberRoleRemoveEvent) {
            GuildMemberRoleRemoveEvent event = (GuildMemberRoleRemoveEvent) genericEvent;

            Database.BotUser botUser = Database.getInstance().getUserByDiscordID(event.getUser().getId());
            if (botUser != null) {
                String id = botUser.getMinecraftId();

                if (id != null) {
                    replaceTabListName(botUser.getMinecraftId(), event.getUser());
                }
            }
        }
    }

    public void replaceTabListName(String id, User user) {
        Player player = Bukkit.getPlayer(UUID.fromString(id));
        Role role = Bot.getInstance().getTopRole(user.getId());

        if (role != null && player != null) {
            player.playerListName(
                    Component.text("[" + role.getName() + "]")
                            .append(player.displayName())
                            .color(TextColor.color(
                                    role.getColor().getRed(),
                                    role.getColor().getGreen(),
                                    role.getColor().getBlue()
                            ))
            );
        }
    }
}
