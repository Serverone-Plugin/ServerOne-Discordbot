package de.serverone.discordbot;

import javax.security.auth.login.LoginException;

import org.bukkit.plugin.java.JavaPlugin;

import de.serverone.discordbot.listeners.LogListener;
import de.serverone.source.util.ServerOneConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;

public class ServerOneDiscordBot extends JavaPlugin {
    ServerOneConfig config;
    private static ServerOneDiscordBot plugin;
    private static Thread botThread;
    private static JDA jda;
    public static String prefix;
    public static String token;
    public static boolean enabled;
    public static boolean loaded = false;
    
    private static Runnable runBot = new Runnable() {
	public void run() {
	    try {
		JDABuilder builder = JDABuilder.createDefault(token);
		builder.setStatus(OnlineStatus.ONLINE);
		plugin.getLogger().info("Bot runs on " + botThread.getName());
		jda = builder.build();
		loaded = true;
	    } catch (LoginException e) {
		System.out.println("Der Serverone-Bot konnte nicht geladen werden");
	    }
	}
    };

    // onEnable
    public void onEnable() {
	plugin = this;
	ServerOneConfig.loadConfig(this, "plugins/ServerOne/DiscordBot", "bot-settings.yml");
	config = ServerOneConfig.getConfig(this, "bot-settings.yml");
	
	if (config.getString("enabled") != "true")
	    return;

	token = config.getString("token");
	prefix = config.getString("commandPrefix");
	try {
	    load();
	} catch (InterruptedException e) {
	    System.out.println("Fehler beim Laden des Bots");
	    e.printStackTrace();
	}

	this.getLogger().info("ServerOne-Bot enabled");
    }

    // onDisable
    public void onDisable() {
	if (jda == null)
	    return;
	LogListener.onServerStop();
	jda.shutdown();
	jda = null;

	this.getLogger().info("ServerOne-Bot disabled");
    }

    /* Bot */
    public static void load() throws InterruptedException {
	botThread = new Thread(runBot);
	botThread.start();

	while (true) {
	    if (loaded)
		break;
	    Thread.sleep(200);
	}
	Thread.sleep(1000);
	Loader.load();
    }

    // getter
    public static JDA getJDA() {
	return jda;
    }

    public static ServerOneDiscordBot getPlugin() {
	return plugin;
    }
}