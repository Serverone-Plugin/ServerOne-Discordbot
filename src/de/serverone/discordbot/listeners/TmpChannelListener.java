package de.serverone.discordbot.listeners;

import de.serverone.discordbot.ServerOneDiscordBot;
import de.serverone.source.util.ServerOneConfig;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

public class TmpChannelListener extends ListenerAdapter{
	public void onMessageReceived(MessageReceivedEvent event) {
		if(!event.getChannel().getId().equals(ServerOneConfig.getConfig(ServerOneDiscordBot.getPlugin(), "bot-settings.yml").getString("ids.channels.tmp-channel-creator"))) return;
		
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		if(!args[0].startsWith(ServerOneDiscordBot.prefix)) return;
		args[0] = args[0].replace(ServerOneDiscordBot.prefix, "");
		
		Guild guild = event.getGuild();
		Member member = event.getMember();
		TextChannel senderChannel = (TextChannel) event.getChannel();
		VoiceChannel createdChannel;
		int limit = 0;
		
		if(!args[0].equalsIgnoreCase("create")) return;
		
		//TODO ID von "get-started" aus config ablesem
		if(member.getVoiceState().getChannel() == null || !member.getVoiceState().getChannel().getName().equals("get-started")) {
			senderChannel.sendMessage(":x: Du musst in `get-started` sein, um einen Kanal zu erstellen").queue();
			return;
		}
		
		if(args.length < 2) {
			senderChannel.sendMessage(":x: Du musst einen Kanalnamen angeben").queue();;
			return;
		}
		if(args[1].equals("get-started") || args[1].length() > 100) {
			senderChannel.sendMessage(":x: Ungültiger Kanalname").queue();
			return;
		}
		
		if(args.length >= 3) {
			try {
				limit = Integer.parseInt(args[2]);
				
				if(limit > 99) limit = 0;
			}catch(Exception e) {
				senderChannel.sendMessage(":x: Ungültige Limitierungsanzahl").queue();
				return;
			}
		}
		
		
		ChannelAction<VoiceChannel> action = guild.createVoiceChannel(args[1]).setParent(senderChannel.getParent());
		
		if(limit >= 0) action.setUserlimit(limit);
		
		createdChannel = action.complete();
		guild.moveVoiceMember(member, createdChannel).queue();
		
		
		
		senderChannel.sendMessage(":thumbsup: Der Kanal `"+args[1]+"` wurde erfolgreich erstellt!").queue();
	}
	
	//Deletion
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
		deleteChannel(event.getChannelLeft());
	}
	public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
		deleteChannel(event.getChannelLeft());
	}
	private void deleteChannel(VoiceChannel channel) {
		if(!channel.getParent().getName().equals("tmp")) return;
		if(channel.getName().equals("get-started")) return;
		if(channel.getMembers().size() == 0) channel.delete().queue();
	}
}
