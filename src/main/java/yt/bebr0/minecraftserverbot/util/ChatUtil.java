package yt.bebr0.minecraftserverbot.util;

import net.dv8tion.jda.api.entities.Role;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import yt.bebr0.minecraftserverbot.Plugin;
import yt.bebr0.minecraftserverbot.bot.Bot;
import yt.bebr0.minecraftserverbot.data.Database;
import net.kyori.adventure.text.TextComponent;

public class ChatUtil {

    private static final ChatUtil instance = new ChatUtil();

    public static ChatUtil getInstance() {
        return instance;
    }

    private final String signature = "&4[" + Plugin.getInstance().getName() + "] >>> ";
    private final String errorSignature = "&4[" + Plugin.getInstance().getName() + " &cERROR] >>> ";

    public void sendMessage(CommandSender player, String msg, Boolean isError) {
        if (isError) player.sendMessage(
                Component.text(
                        ChatColor.translateAlternateColorCodes(
                                '&',
                                errorSignature + "&6&l" + msg
                        )
                )
        );
        else player.sendMessage(
                Component.text(ChatColor.translateAlternateColorCodes('&', signature + "&1&l" + msg))
        );
    }

    public void broadcast(String msg, Player author) {

        Database.BotUser discordUser = Database.getInstance().getUserByMinecraftID(author.getUniqueId());

        TextComponent.Builder resultMessage = Component.text();

        // TODO CHECK REGISTRATION
        if (discordUser != null) {
            Role role = Bot.getInstance().getTopRole(discordUser.getDiscordId());

            if (role != null) {
                if (role.getColor() != null) {
                    resultMessage.append(
                            Component.text("[" + role.getName() + "] ")
                                    .color(TextColor.color(role.getColor().getRed(), role.getColor().getGreen(), role.getColor().getBlue()))
                    );
                } else {

                    resultMessage.append(
                            Component.text("[" + role.getName() + "] ")
                    );
                }
            }
        }

        resultMessage
                .append(author.displayName())
                .append(Component.text(ChatColor.translateAlternateColorCodes('&', ": " + msg)));

        for (Player player : Plugin.getInstance().getServer().getOnlinePlayers()) {
            player.sendMessage(resultMessage.build());
        }
    }
}