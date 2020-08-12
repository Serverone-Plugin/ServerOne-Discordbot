package de.robotix_00.serverone.discordbot;

import de.robotix_00.serverone.source.util.ServerOneConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class ChannelSummary {
    private static ServerOneConfig config = ServerOneConfig.getConfig(ServerOneDiscordBot.getPlugin(),"bot-settings.yml");
    private static JDA jda = ServerOneDiscordBot.getJDA();

    public static TextChannel getTextChannel(String configLocation) {
	return jda.getTextChannelById(config.getString(configLocation));
    }

    public static VoiceChannel getVoiceChannel(String configLocation) {
	return jda.getVoiceChannelById(config.getString(configLocation));
    }
    
    public static boolean isCorrectChannel(MessageChannel channel, String configLocation) {
	return channel.getId().equals(config.getString(configLocation));
    }
}
