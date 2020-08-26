package de.serverone.discordbot.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;

import de.robotix_00.serverone.source.util.ServerOneConfig;
import de.serverone.discordbot.ServerOneDiscordBot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

public class ChatConnectionListener extends net.dv8tion.jda.api.hooks.ListenerAdapter
	implements org.bukkit.event.Listener {
    JDA jda = ServerOneDiscordBot.getJDA();

    @EventHandler
    public void sendMessage(org.bukkit.event.player.AsyncPlayerChatEvent event) {
	TextChannel channel = jda
		.getTextChannelById(ServerOneConfig.getConfig(ServerOneDiscordBot.getPlugin(), "bot-settings.yml")
			.getString("ids.channels.mc-chat"));

	channel.sendMessage(deleteParaphrase(event.getFormat())).queue();
    }

    public void onMessageReceived(net.dv8tion.jda.api.events.message.MessageReceivedEvent event) {
	if (!event.getChannel().getName().equals("minecraft"))
	    return;
	if (event.getAuthor().isBot())
	    return;

	Bukkit.broadcastMessage("§9[Discord]§b" + event.getAuthor().getName() + "§r: " + ChatColor.WHITE
		+ event.getMessage().getContentRaw());
    }
    private String deleteParaphrase(String message) {
	String out = "";
	char[] arr = message.toCharArray();
	
	for(int i=0; i<arr.length; i++) {
	    if(arr[i] == '§') {
		i++;
	    } else {
		out+=arr[i];
	    }
	}
	
	return out;
    }
}