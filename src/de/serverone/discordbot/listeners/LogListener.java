package de.serverone.discordbot.listeners;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.serverone.discordbot.ChannelSummary;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class LogListener extends ListenerAdapter implements Listener {
    static TextChannel channel = ChannelSummary.getTextChannel("ids.channels.log");

    /* Minecraft */
    // ServerStart
    public static void onServerStart() {
	EmbedBuilder eb = new EmbedBuilder().setTitle("Der Server wurde gestartet").setColor(Color.RED).addField("Zeit",
		getTime(), true);

	channel.sendMessage(eb.build()).queue();
	eb.clear();
    }

    public static void onServerStop() {
	EmbedBuilder eb = new EmbedBuilder().setTitle("Der Server wurde gestoppt").setColor(Color.RED).addField("Zeit",
		getTime(), true);

	channel.sendMessage(eb.build()).queue();
	eb.clear();
    }

    public static void onPlayerUnlock(Player player, String key) {
	EmbedBuilder eb = new EmbedBuilder().setTitle(player.getName() + " hat sich freigeschaltet")
		.setColor(Color.BLUE).addField("Spieler", player.getName(), true).addField("Schlüssel", key, true)
		.addField("Zeit", getTime(), true);

	channel.sendMessage(eb.build()).queue();

	eb.clear();
    }

    // PlayerJoin
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
	EmbedBuilder eb = new EmbedBuilder().setTitle(event.getPlayer().getName() + " ist dem Server beigetreten")
		.setColor(Color.GREEN).addField("Spieler", event.getPlayer().getName(), true)
		.addField("Zeit", getTime(), true);

	channel.sendMessage(eb.build()).queue();

	eb.clear();
    }

    // PlayerQuit
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
	EmbedBuilder eb = new EmbedBuilder().setTitle(event.getPlayer().getName() + " hat den Server verlassen")
		.setColor(Color.GREEN).addField("Spieler", event.getPlayer().getName(), true)
		.addField("Zeit", getTime(), true);

	channel.sendMessage(eb.build()).queue();
	eb.clear();
    }

    // PlayerDeath
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
	EmbedBuilder eb = new EmbedBuilder().setTitle(event.getEntity().getName() + " ist gestorben")
		.setColor(Color.PINK).addField("Spieler", event.getEntity().getName(), true)
		.addField("Zeit", getTime(), true).addField("Todesnachricht", event.getDeathMessage(), false);

	channel.sendMessage(eb.build()).queue();
	eb.clear();
    }
    
    private static String getTime() {
	return DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy").format(LocalDateTime.now());
    }
}
