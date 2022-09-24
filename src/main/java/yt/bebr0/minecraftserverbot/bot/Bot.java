package yt.bebr0.minecraftserverbot.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
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

    private static Bot instance = new Bot();

    public static Bot getInstance() {
        return instance;
    }

    private final JDA jda = JDABuilder.createDefault(Variables.token)
            .addEventListeners(new ChatEvent())
            .build();
    private final Guild guild = jda.getGuildById(Plugin.getInstance().getConfig().getLong("guild_id"));
    private TextChannel channel;

    private final List<VerificationManager.Request> requests = new ArrayList<>();

    private Bot() {
        if (guild == null) {
            Plugin.getInstance().getLogger().severe("Guild is NULL!");a
            instance = null;
        }
        else {
            channel = guild.getTextChannelById(Plugin.getInstance().getConfig().getLong("channel_id"));
            if (channel == null) {
                Plugin.getInstance().getLogger().severe("Channel is NULL!");
                instance = null;
            }
        }
    }

    public void requestVerification(UUID requester, String requestedDiscordId) {
        VerificationManager.Request request = VerificationManager.createRequest(requester, requestedDiscordId);



        if (request != null) {
            User user = jda.getUserById(requestedDiscordId);
            Player player = Bukkit.getPlayer(requester);

            assert user != null; // CHECKED PREVIOUSLY
            assert player != null;
            assert guild != null;

            if (guild.getMemberById(requestedDiscordId) != null) {
                user.openPrivateChannel()
                        .flatMap(
                                channel -> channel.sendMessage(
                                        "```Вам был отправлен запрос на верификацию от дискорд сервера '" + guild.getName() + "'." +
                                                " Запрос создан игроком " + player.displayName() + ". " +
                                                "После принятия запроса он получит возможность слать сообщения в чат от вашего имени.\n\n" +
                                                "\t❗️❗️ ЕСЛИ ВЫ НЕ СОЗДАВАЛИ ДАННЫЙ ЗАПРОС, ТО НАЖМИТЕ КРЕСТИК СНИЗУ ❗️❗\n\n️" +
                                                "\t✅ Если запрос создали вы, нажмите галочку внизу```"
                                )
                        ).queue(
                                message -> {
                                    message.addReaction(Emoji.fromUnicode("✅"));
                                    message.addReaction(Emoji.fromUnicode("❌"));
                                }
                        );

                requests.add(request);
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
