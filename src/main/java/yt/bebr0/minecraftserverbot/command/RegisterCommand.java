package yt.bebr0.minecraftserverbot.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import yt.bebr0.minecraftserverbot.bot.Bot;
import yt.bebr0.minecraftserverbot.util.ChatUtil;

/**
 * File is part of BeBrAPI. Thank you for using it! Also check out my YouTube channel where you can also leave your suggestions! https://www.youtube.com/c/BeBr0
 *
 * @author BeBr0
 */
public class RegisterCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Команда только для игрока!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            ChatUtil.getInstance().sendMessage(player, "Ты не указал свой &lDiscord ID", true);
            return true;
        }

        if (Bot.getInstance().requestVerification(player.getUniqueId(), args[0])) {
            ChatUtil.getInstance().sendMessage(
                    player,
                    "Вам отправлен запрос на верификацию, нажмите на соответствующую реакцию под сообщением в Discord!",
                    false
            );
        }
        else {
            ChatUtil.getInstance().sendMessage(
                    player,
                    "Отправить запрос не удалось, проверьте правильность ID, находится ли ваш дискорд аккаунт на " +
                            "сервере, либо запрос уже отправлен",
                    true
            );
        }

        return true;
    }
}
