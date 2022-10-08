package yt.bebr0.minecraftserverbot.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import yt.bebr0.minecraftserverbot.Plugin;
import yt.bebr0.minecraftserverbot.Variables;
import yt.bebr0.minecraftserverbot.bot.event.ChatEvent;
import yt.bebr0.minecraftserverbot.bot.event.ReactionAddEvent;
import yt.bebr0.minecraftserverbot.bot.event.RoleUpdateEvent;
import yt.bebr0.minecraftserverbot.bot.verify.VerificationManager;
import yt.bebr0.minecraftserverbot.data.Database;
import yt.bebr0.minecraftserverbot.util.ChatUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Bot {

    private static Bot instance = null;

    public static Bot getInstance() {
        if (instance == null) {
            instance = new Bot();
        }

        return instance;
    }

    private final JDA jda = JDABuilder.createDefault(Variables.token)
            .addEventListeners(new ChatEvent(), new ReactionAddEvent(), new RoleUpdateEvent())
            .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
            .build();
    private final Guild guild;
    private TextChannel channel;
    private TextChannel commandChannel;

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

            commandChannel = guild.getTextChannelById(Plugin.getInstance().getConfig().getLong("command_channel_id"));

            if (commandChannel == null) {
                Plugin.getInstance().getLogger().severe("Command channel is NULL!");
                instance = null;
            }
        }

        Plugin.getInstance().getLogger().severe("BOT LAUNCHED!");
    }

    public boolean requestVerification(UUID requester, String requestedDiscordId) {
        VerificationManager.Request request = VerificationManager.createRequest(requester, requestedDiscordId);

        for (VerificationManager.Request req: requests) {
            if (req.getRequester().equals(requester) || req.getRequestedDiscordId().equals(requestedDiscordId)) {
                return false;
            }
        }

        if (request != null) {
            Player player = Bukkit.getPlayer(requester);

            assert player != null;
            assert guild != null;

            if (isUserOnServer(requestedDiscordId)) {

                jda.retrieveUserById(requestedDiscordId).queue(currentUser -> {
                    currentUser.openPrivateChannel()
                            .flatMap(
                                    channel -> channel.sendMessage(
                                            "```Вам был отправлен запрос на верификацию от дискорд сервера '" + guild.getName() + "'." +
                                                    " Запрос создан игроком " + player.getDisplayName() + ". " +
                                                    "После принятия запроса он получит возможность слать сообщения в чат от вашего имени.\n\n" +
                                                    "\t❗️❗️ ЕСЛИ ВЫ НЕ СОЗДАВАЛИ ДАННЫЙ ЗАПРОС, ТО НАЖМИТЕ КРЕСТИК СНИЗУ ❗️❗\n\n️" +
                                                    "\t✅ Если запрос создали вы, нажмите галочку внизу```"
                                    )
                            )
                            .queue(
                                    message -> {
                                        message.addReaction(Emoji.fromFormatted("✅")).queue();
                                        message.addReaction(Emoji.fromFormatted("❌")).queue();
                                    }
                            );
                });

                requests.add(request);
                return true;
            }
        }
        return false;
    }

    public void announceMessage(String message) {
        for (Player player : Plugin.getInstance().getServer().getOnlinePlayers()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c[!!!]: &r" + message));
        }
    }

    public boolean isUserOnServer(String discordId) {
        for (Member member : guild.loadMembers().get()) {
            if (member.getId().equals(discordId)) {
                return true;
            }
        }

        return false;
    }

    public void sendMessageToMinecraft(String uuid, String userId, String message) {
        if (!uuid.equals("")) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
            guild.retrieveMemberById(userId).queue(member -> {
                ChatUtil.getInstance().broadcast(message, player.getUniqueId(), Component.text(player.getName()));
            });
        }
        else {
            guild.retrieveMemberById(userId).queue(member -> {
                ChatUtil.getInstance().broadcast(message, UUID.randomUUID(), Component.text(member.getEffectiveName()));
            });
        }


    }

    public void sendMessageToDiscord(String userId, String uuid, String text) {

        Player player = Bukkit.getPlayer(UUID.fromString(uuid));

        assert player != null;  // NEVER NULL CAUSE CALLED FROM MINECRAFT

        if (!userId.equals("")) {
            jda.retrieveUserById(userId).queue(user -> {
                textToDiscordAs(user.getName(), player.getName(), text);
            });
        }
        else {
            textToDiscordAs("Без регистрации", player.getName(), text);
        }
    }

    public void textToDiscordAs(String userName, String playerName, String message) {
        channel.sendMessage(
                "```\n" +
                        userName + " (" + playerName + ")\n" +
                        "Написал: " + message + "\n" +
                        "```"
        ).queue();

        // TODO add time to message
    }


    @Nullable
    public Role getTopRole(String discordId) {
        for (Member member: guild.loadMembers().get()) {
            if (member.getId().equals(discordId)) {
                return member.getRoles().get(0);
            }
        }

        return null;
    }

    public void grantVerification(String id) {

        for (VerificationManager.Request request : requests) {
            if (request.getRequestedDiscordId().equals(id)) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(request.getRequester());

                if (player.getPlayer() != null) {
                    player.getPlayer().sendMessage("Верификация пройдена успешно!");
                }

                jda.retrieveUserById(request.getRequestedDiscordId()).queue(user -> {
                    guild.retrieveMemberById(request.getRequestedDiscordId()).queue(member -> {
                        guild.modifyNickname(member, user.getName() + " (" + player.getName() + ")").queue();
                    });
                });

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

        instance = null;
    }

    public TextChannel getChannel() {
        return channel;
    }

    public void rejectVerification(String id) {
        for (VerificationManager.Request request : requests) {
            if (request.getRequestedDiscordId().equals(id)) {
                ChatUtil.getInstance().sendMessage(Bukkit.getPlayer(request.getRequester()), "Верификация отклонена!", false);
                requests.remove(request);
                break;
            }
        }

    }

    public TextChannel getCommandChannel() {
        return commandChannel;
    }
}
