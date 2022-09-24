package yt.bebr0.minecraftserverbot.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.User;
import net.kyori.adventure.text.Component;
import yt.bebr0.minecraftserverbot.Plugin;
import yt.bebr0.minecraftserverbot.Variables;

public class Bot {

    private static final Bot instance = new Bot();

    public static Bot getInstance() {
        return instance;
    }

    private String syncChatName = Plugin.getInstance().getConfig().getString("sync_chat_name");

    private final JDA jda = JDABuilder.createDefault(Variables.token).build();

    public void sendMessageToMinecraft(String text, long userId) {
        // TODO: send message
        Plugin.getInstance().getServer().broadcast(message);
    }

    public void setSyncChatName(String syncChatName) {
        this.syncChatName = syncChatName;
    }

    public String getSyncChatName() {
        return syncChatName;
    }

    public JDA getJda() {
        return jda;
    }
}
