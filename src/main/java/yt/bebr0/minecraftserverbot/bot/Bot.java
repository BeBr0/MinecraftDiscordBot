package yt.bebr0.minecraftserverbot.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import yt.bebr0.minecraftserverbot.Plugin;
import yt.bebr0.minecraftserverbot.Variables;
import yt.bebr0.minecraftserverbot.bot.event.ChatEvent;
import yt.bebr0.minecraftserverbot.bot.event.ReactionAddEvent;
import yt.bebr0.minecraftserverbot.bot.verify.VerificationManager;
import yt.bebr0.minecraftserverbot.data.Database;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Bot {

    private static Bot instance = new Bot();

    public static Bot getInstance() {
        return instance;
    }

    private final JDA jda = JDABuilder.createDefault(Variables.token)
            .addEventListeners(new ChatEvent(), new ReactionAddEvent())
            .enableIntents(GatewayIntent.GUILD_MEMBERS)
            .build();
    private final Guild guild;
    private TextChannel channel;

    private final List<VerificationManager.Request> requests = new ArrayList<>();

    private Bot() {
        try {
            jda.awaitReady();
        }
        catch (InterruptedException ignored) {}

        guild = jda.getGuildById(Plugin.getInstance().getConfig().getLong("guild_id"));

        if (guild == null) {
            Plugin.getInstance().getLogger().severe("Guild is NULL!");
            instance = null;
        }
        else {
            guild.loadMembers();
            channel = guild.getTextChannelById(Plugin.getInstance().getConfig().getLong("channel_id"));
            if (channel == null) {
                Plugin.getInstance().getLogger().severe("Channel is NULL!");
                instance = null;
            }
        }

        Plugin.getInstance().getLogger().severe("BOT LAUNCHED!");
    }

    public boolean requestVerification(UUID requester, String requestedDiscordId) {
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
                                                " Запрос создан игроком " + player.getDisplayName() + ". " +
                                                "После принятия запроса он получит возможность слать сообщения в чат от вашего имени.\n\n" +
                                                "\t❗️❗️ ЕСЛИ ВЫ НЕ СОЗДАВАЛИ ДАННЫЙ ЗАПРОС, ТО НАЖМИТЕ КРЕСТИК СНИЗУ ❗️❗\n\n️" +
                                                "\t✅ Если запрос создали вы, нажмите галочку внизу```"
                                )
                        ).queue(
                                message -> {
                                    message.addReaction(Emoji.fromFormatted(":white_check_mark:"));
                                    message.addReaction(Emoji.fromFormatted(":x:"));
                                }
                        );

                requests.add(request);
                return true;
            }
        }

        return false;
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


    public void grantVerification(String id) {
        for (VerificationManager.Request request : requests) {
            if (request.getRequestedDiscordId().equals(id)) {
                Bukkit.getPlayer(request.getRequester()).sendMessage("Верификация пройдена успешно!");

                Database.getInstance().writeUser(request.getRequester().toString(), request.getRequestedDiscordId());
                requests.remove(request);
                break;
            }
        }
    }

    public boolean isMemberRequested(String discordId) {
        for (VerificationManager.Request request : requests) {
            if (request.getRequestedDiscordId().equals(discordId)) {
                return true;
            }
        }

        return false;
    }

    public void shutdown() {
        jda.shutdown();
        try {
            jda.awaitStatus(JDA.Status.SHUTDOWN);
        }
        catch (InterruptedException ignored) {}

        instance = null;
    }

    public TextChannel getChannel() {
        return channel;
    }
}
