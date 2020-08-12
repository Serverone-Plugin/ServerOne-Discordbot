package de.robotix_00.serverone.discordbot.listeners;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.robotix_00.serverone.discordbot.ServerOneDiscordBot;
import de.robotix_00.serverone.source.util.ServerOneConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

public class SupportListener implements CommandExecutor {
    JDA jda = ServerOneDiscordBot.getJDA();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandlable, String[] args) {
	Player player = null;
	if (sender instanceof Player)
	    player = (Player) sender;
	if (args.length == 0) {
	    player.sendMessage(ChatColor.RED + "Es muss ein Grund angegeben werde");
	    return true;
	}

	TextChannel channel = ServerOneDiscordBot.getJDA()
		.getTextChannelById(ServerOneConfig.getConfig(ServerOneDiscordBot.getPlugin(), "bot-settings.yml")
			.getString("ids.channels.helpop"));
	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
	LocalDateTime nowTime = LocalDateTime.now();

	String message = "";
	for (String now : args) {
	    message += now + " ";
	}

	EmbedBuilder eb = new EmbedBuilder();
	eb.setTitle("Supportanfrage");
	eb.setColor(Color.CYAN);
	eb.addField("Spieler", player.getName(), true);
	eb.addField("Zeit", dtf.format(nowTime), true);
	eb.addField("Nachricht", message, false);

	channel.sendMessage(eb.build()).queue();
	eb.clear();

	player.sendMessage(ChatColor.GREEN + "Deine Anfrage wurde an den Support geschickt und wird bald bearbeitet");

	return true;
    }
}
