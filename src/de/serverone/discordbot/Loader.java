package de.serverone.discordbot;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.serverone.discordbot.listeners.ChatConnectionListener;
import de.serverone.discordbot.listeners.CommandListener;
import de.serverone.discordbot.listeners.LogListener;
import de.serverone.discordbot.listeners.PlayerUnlockListener;
import de.serverone.discordbot.listeners.SupportListener;
import de.serverone.discordbot.listeners.TmpChannelListener;
import net.dv8tion.jda.api.JDA;

public class Loader {
    public static void load() {
	JavaPlugin plugin = ServerOneDiscordBot.getPlugin();
	JDA jda = ServerOneDiscordBot.getJDA();
	
	
	// Bukkit
	PluginManager pluginManager = plugin.getServer().getPluginManager();
	pluginManager.registerEvents(new ChatConnectionListener(), plugin);
	pluginManager.registerEvents(new LogListener(), plugin);

	// Discord
	
	//jda.addEventListener(new ChatConnectionListener());
	jda.addEventListener(new TmpChannelListener());
	jda.addEventListener(new CommandListener());
	jda.addEventListener(new LogListener());
	jda.addEventListener(new PlayerUnlockListener());

	// Commands
	plugin.getCommand("helpop").setExecutor(new SupportListener());
	plugin.getCommand("unlock").setExecutor(new PlayerUnlockListener());
	
	LogListener.onServerStart();
    }
}
