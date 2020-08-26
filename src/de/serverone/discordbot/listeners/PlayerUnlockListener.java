package de.serverone.discordbot.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.robotix_00.serverone.permissionsOne.PermissionsOne;
import de.robotix_00.serverone.source.util.ServerOneConfig;
import de.serverone.discordbot.ServerOneDiscordBot;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PlayerUnlockListener extends ListenerAdapter implements CommandExecutor {
    public static List<String> keys = new ArrayList<>();
    private static ServerOneConfig config = ServerOneConfig.getConfig(ServerOneDiscordBot.getPlugin(), "bot-settings.yml");

    public void onMessageReactionAdd(MessageReactionAddEvent event) {
	boolean succes = true;
	String key;

	if (!event.getMessageId().equals(config.getString("ids.messages.player-unlock")))
	    return;
	Role playerRole = event.getGuild().getRoleById(config.getString("ids.roles.player"));

	if (event.getMember().getRoles().contains(playerRole))
	    return;
	key = generateKey();

	try {
	    event.getUser().openPrivateChannel().complete().sendMessage(
		    "Herzlichen Glückwunsch, du bist jetzt ein Spieler auf ServerOne. Bitte verwende auf unserem Minecraftserver den Befehl `/unlock "
			    + key + "` um dich frei zu schalten."
			    + " Der Schlüssel verfällt nach einer Weile wieder. Falls du es nicht schaffst dich in der nächsten Zeit auf unserem Server einzuloggen bitten wir darum, dich bei einem Supporter zu melden")
		    .complete();
	} catch (ErrorResponseException e) {
	    System.err.println("Cound not send unlock-key to " + event.getUser().getAsTag());
	    keys.remove(key);
	    succes = false;
	}
	if (succes)
	    event.getGuild().addRoleToMember(event.getMember(), playerRole).queue();

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandlable, String[] args) {
	Player player = null;
	if (sender instanceof Player)
	    player = (Player) sender;
	else {
	    sender.sendMessage("§cUngültiger Sender, muss von einem Spieler ausgeführt werden");
	    return true;
	}
	if (args.length == 0) {
	    player.sendMessage("§cEs muss ein Schlüssel angegeben werden");
	    return true;
	}
	if (player.isOp() && args[0].equalsIgnoreCase("list")) {
	    String message = "§aMomentane Keys sind: ";
	    for (String now : keys) {
		message += "§c" + now + "§a/";
	    }
	    if (keys.isEmpty())
		player.sendMessage("§cEs gibt momentan keine Schlüssel");
	    else
		player.sendMessage(message);

	    return true;
	}
	if (isInMcPlayerGroup(player)) {
	    player.sendMessage("§cDu hast schon den Spielerrang!");
	    return true;
	}
	if (!keys.contains(args[0])) {
	    player.sendMessage("§cDer Schlüssel §a" + args[0] + " §cist ungültig");
	    return true;
	}

	unlockMcPlayer(player, args[0]);
	player.sendMessage(
		"§aHerzlichen Glückwunsch, du wurdest freigeschaltet. Wir wünschen dir viel Spaß auf §lServerOne");
	return true;
    }

    private void unlockMcPlayer(Player player, String key) {
	keys.remove(key);
	PermissionsOne.getPermissionController().setPlayersGroup(player, config.getString("ids.mc-groups.player"));
	LogListener.onPlayerUnlock(player, key);
    }

    private boolean isInMcPlayerGroup(Player player) {
	return !PermissionsOne.getPermissionController().getPlayersGroup(player).equals("default");
    }

    public static String generateKey() {
	String key;
	do {
	    key = "" + (int) (Math.random() * 1000000);
	} while (keys.contains(key));

	keys.add(key);
	return key;
    }
}
