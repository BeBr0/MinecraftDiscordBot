package yt.bebr0.minecraftserverbot

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import yt.bebr0.minecraftserverbot.bot.Bot
import yt.bebr0.minecraftserverbot.data.Database

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

    fun broadcast(msg: String, author: Player) {

        val discordUser = Database.getInstance().getUserByMinecraftID(author.uniqueId)
        val prefix = Component.text()

        if (discordUser != null) {
            val role = Bot.getInstance().getTopRole(discordUser.discordId)

            if (role != null) {
                if (role.color != null) {
                    prefix.append(
                        Component.text("[${role.name}]")
                            .color(TextColor.color(role.color!!.red, role.color!!.green, role.color!!.blue))
                    ).build()
                } else {
                    prefix.append(
                        Component.text("[${role.name}]")
                    ).build()
                }
            }
        }

        val resultMessage = Component.text()

        if (prefix.content() != "") {
            resultMessage
                .append(prefix)
                .append(Component.text(" "))
        }

        resultMessage
            .append(author.displayName())
            .append(Component.text(ChatColor.translateAlternateColorCodes('&', ": $msg")))

        Plugin.getInstance().server.broadcast(
            resultMessage.build()
        )
    }
}