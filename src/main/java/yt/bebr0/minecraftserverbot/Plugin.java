package yt.bebr0.minecraftserverbot;

import org.bukkit.plugin.java.JavaPlugin;
import yt.bebr0.minecraftserverbot.bot.Bot;
import yt.bebr0.minecraftserverbot.command.RegisterCommand;
import yt.bebr0.minecraftserverbot.events.MinecraftChatEvent;
import yt.bebr0.minecraftserverbot.events.PlayerFirstTimeJoinEvent;

public final class Plugin extends JavaPlugin {

    private static Plugin instance = null;

    public static Plugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        getServer().getPluginManager().registerEvents(new MinecraftChatEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerFirstTimeJoinEvent(), this);

        getCommand("registerDiscord").setExecutor(new RegisterCommand());

        System.out.println(Bot.getInstance().toString());
    }

    @Override
    public void onDisable() {
        instance = null;
        // Plugin shutdown logic
    }
}
