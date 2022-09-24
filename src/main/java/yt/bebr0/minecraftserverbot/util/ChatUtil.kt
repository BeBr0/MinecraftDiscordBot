package yt.bebr0.minecraftserverbot.util

import net.kyori.adventure.text.Component
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import yt.bebr0.minecraftserverbot.Plugin


/**
 * File is part of BeBrAPI. Thank you for using it! Also check out my YouTube channel where you can also leave your suggestions! https://www.youtube.com/c/BeBr0
 *
 * @author BeBr0
 */

object ChatUtil {
    private val signature = "&4[" + Plugin.getInstance().name + "] >>> "
    private val errorSignature = "&4[" + Plugin.getInstance().name + " &cERROR] >>> "
    fun sendMessage(player: CommandSender, msg: String, isError: Boolean) {
        if (isError) player.sendMessage(
            Component.text(
                ChatColor.translateAlternateColorCodes(
                    '&',
                    "$errorSignature&6&l$msg"
                )
            )
        )
        else player.sendMessage(
            Component.text(ChatColor.translateAlternateColorCodes('&', "$signature&1&l$msg"))
        )
    }
}