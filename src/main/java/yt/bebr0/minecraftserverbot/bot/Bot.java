package yt.bebr0.minecraftserverbot.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import yt.bebr0.minecraftserverbot.Plugin;
import yt.bebr0.minecraftserverbot.Variables;
import yt.bebr0.minecraftserverbot.bot.events.ChatEvent;
import yt.bebr0.minecraftserverbot.bot.verification.VerificationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Bot {

    private static final Bot instance = new Bot();

    public static Bot getInstance() {
        return instance;
    }

    private final JDA jda = JDABuilder.createDefault(Variables.token)
            .addEventListeners(new ChatEvent())
            .build();
    private final Guild guild = jda.getGuildById(Plugin.getInstance().getConfig().getLong("guild_id"));
    private TextChannel channel;

    private List<VerificationManager.Request> requests = new ArrayList<>();

    private Bot() {
        if (guild == null) {
            Plugin.getInstance().getLogger().severe("Guild is NULL!");
        }
        else {
            channel = guild.getTextChannelById(Plugin.getInstance().getConfig().getLong("channel_id"));
            if (channel == null) {
                Plugin.getInstance().getLogger().severe("Channel is NULL!");
            }
        }
    }

    public void sendMessageToMinecraft(String uuid, String userId, String message) {
        Player player = Bukkit.getPlayer(UUID.fromString(uuid));

        if (player != null) {
            Plugin.getInstance().getServer().broadcast(
                    Component.text(
                            "<" + player.displayName() + ">: " + message
                    )
            );
        }
        else {
            User user = jda.getUserById(userId);

            assert user != null; // NEVER NULL CAUSE CALLED FROM DISCORD

            Plugin.getInstance().getServer().broadcast(
                    Component.text("<" + user.getName() + ">: " + message
                    )
            );
        }
    }

    public void sendMessageToDiscord(String userId, String uuid, String text) {
        User user = jda.getUserById(userId);

        if (user != null) {
            channel.sendMessage(
                    "```\n" +
                            user.getName() + "\n" +
                            text + "\n" +
                            "```"
            );
        }
        else {
            Player player = Bukkit.getPlayer(uuid);

            assert player != null; // NEVER NULL CAUSE CALLED FROM MINECRAFT

            channel.sendMessage(
                    "```\n" +
                            player.displayName() + "\n" +
                            text + "\n" +
                            "```"
            );
        }
    }

    public JDA getJda() {
        return jda;
    }

    public TextChannel getChannel() {
        return channel;
    }
}
