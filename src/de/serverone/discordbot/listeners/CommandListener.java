package de.serverone.discordbot.listeners;

import java.awt.Color;
import java.util.List;

import de.serverone.discordbot.ServerOneDiscordBot;
import de.serverone.source.util.ServerOneConfig;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {
    private static ServerOneConfig config = ServerOneConfig.getConfig(ServerOneDiscordBot.getPlugin(), "bot-settings.yml");

    public void onMessageReceived(MessageReceivedEvent event) {
	if (!event.getChannel().getId().equals(config.getString("ids.channels.commands")))
	    return;

	String[] args = event.getMessage().getContentRaw().split("\\s+");
	if (!args[0].startsWith(ServerOneDiscordBot.prefix))
	    return;
	args[0] = args[0].replace(ServerOneDiscordBot.prefix, "");

	Guild guild = event.getGuild();
	MessageChannel channel = event.getChannel();
	Member member = event.getMember();

	// args-switch
	switch (args[0]) {
	case "bot":
	    if (args.length <= 1)
		return;
	    switch (args[1]) {
	    case "prepare":
		if (!event.getMember().isOwner())
		    return;

		event.getMessage().delete().queue();
		if (guild.getRolesByName("Besucher", true).isEmpty())
		    guild.createRole().setName("Besucher").setColor(Color.LIGHT_GRAY).complete();
		if (guild.getRolesByName("Spieler", true).isEmpty())
		    guild.createRole().setName("Spieler").setColor(Color.GREEN).complete();

		if (guild.getCategoriesByName("BotControll", false).isEmpty()) {
		    Category botcat = guild.createCategory("BotControll").complete();
		    guild.createTextChannel("03-controller").setParent(botcat).complete();

		}
		break;
	    }
	case "move":
	    if (args.length == 2) {
		if (!member.getVoiceState().inVoiceChannel())
		    return;
		List<VoiceChannel> channels = guild.getVoiceChannelsByName(args[1], true);
		if (channels.size() == 0)
		    return;

		int i = 0;
		for (Member now : member.getVoiceState().getChannel().getMembers()) {
		    guild.moveVoiceMember(now, channels.get(0)).queue();
		    i++;
		}
		channel.sendMessage(
			":thumbsup: " + i + " Mitgliede(r) wurden nach `" + channels.get(0).getName() + "` verschoben")
			.queue();
	    } else if (args.length == 3) {
		List<VoiceChannel> from = guild.getVoiceChannelsByName(args[1], true);
		List<VoiceChannel> to = guild.getVoiceChannelsByName(args[2], true);
		if (to.size() == 0 || from.size() == 0)
		    return;

		int i = 0;
		for (Member now : from.get(0).getMembers()) {
		    guild.moveVoiceMember(now, to.get(0)).queue();
		    i++;
		}
		channel.sendMessage(":thumbsup: " + i + " Mitgliede(r) wurden von `" + from.get(0).getName()
			+ "` nach `" + to.get(0).getName() + "` verschoben").queue();
	    }
	    break;
	case "key":
	    if (args.length < 2)
		return;
	    List<String> keys = PlayerUnlockListener.keys;
	    switch (args[1].toLowerCase()) {
	    case "generate":
		channel.sendMessage(":thumbsup: Es wurde ein Freischaltungsschlüssel generiert: "
			+ PlayerUnlockListener.generateKey()).queue();
		break;
	    case "list":
		if (keys.size() == 0) {
		    channel.sendMessage("Es gibt momentan keine gültigen Schlüssel").queue();
		    return;
		}
		String message = "Gültige Schlüssel sind:";
		for (String now : keys) {
		    message += " `" + now + "`,";
		}
		channel.sendMessage(message).queue();
		break;
	    case "remove":
		if(args.length < 3) return;
		if(keys.contains(args[2])) {
		    keys.remove(args[2]);
		    channel.sendMessage(":thumbsup: Der Schlüssel " + args[2] + " wurde gelöscht.").queue();
		} else {
		    channel.sendMessage(":x: Der Schlüssel " + args[2] + " ist ungültig").queue();
		}
		break;
	    case "create":
		if(args.length < 3) return;
		if(keys.contains(args[2])) {
		    channel.sendMessage(":x: Der Schlüssel " + args[2] + " existiert bereits").queue();
		} else {
		    keys.add(args[2]);
		    channel.sendMessage(":thumbsup: Der Schlüssel " + args[2] + " wurde initialisiert").queue();
		}
		break;
	    }
	    break;
	}
    }
}