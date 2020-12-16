package minecraft.statistic.zocker.pro.command;

import minecraft.core.zocker.pro.OfflineZocker;
import minecraft.core.zocker.pro.Zocker;
import minecraft.core.zocker.pro.command.Command;
import minecraft.core.zocker.pro.command.SubCommand;
import minecraft.core.zocker.pro.compatibility.CompatibleMessage;
import minecraft.statistic.zocker.pro.inventory.StatisticOverviewInventory;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import minecraft.statistic.zocker.pro.Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class StatisticCommand extends Command {

	private static final List<SubCommand> SUB_COMMAND_LIST = new ArrayList<>();

	public StatisticCommand() {
		super("statistic", "mzp.statistic.command.statistic", new String[]{"stat", "stats"});

		SUB_COMMAND_LIST.add(new StatisticAddCommand());
		SUB_COMMAND_LIST.add(new StatisticRemoveCommand());
		SUB_COMMAND_LIST.add(new StatisticSetCommand());
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		List<String> completions = new ArrayList<>();

		if (args.length == 0) {
			SUB_COMMAND_LIST.forEach(subCommand -> completions.add(subCommand.getName()));
		} else if (args.length == 1) {
			String partialPlayerName = args[0];

			int lastSpaceIndex = partialPlayerName.lastIndexOf(' ');
			if (lastSpaceIndex >= 0) {
				partialPlayerName = partialPlayerName.substring(lastSpaceIndex + 1);
			}

			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.getName().toLowerCase().startsWith(partialPlayerName)) {
					return Collections.singletonList(p.getName());
				}
			}

			SUB_COMMAND_LIST.stream().filter(subCommand -> subCommand.getName().toLowerCase().startsWith(args[0].toLowerCase()))
				.forEach(subCommand -> completions.add(subCommand.getName()));
		} else {
			SubCommand command = findSubCommand(args[0]);

			if (command != null) {
				return command.getCompletions(sender, args);
			}
		}

		return completions;
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Zocker zocker = Zocker.getZocker(((Player) sender).getUniqueId());

			if (args.length <= 0) {
				new StatisticOverviewInventory(zocker).open(zocker);
				return;
			}

			for (SubCommand subCommand : SUB_COMMAND_LIST) {
				if (subCommand.getName().equalsIgnoreCase(args[0])) {
					subCommand.execute(sender, args);
					return;
				}
			}

			Player player = (Player) sender;
			Player target = Bukkit.getPlayer(args[0]);

			if (target == null) {
				UUID uuid = OfflineZocker.fetchUUID(args[0]);
				if (uuid == null) {
					CompatibleMessage.sendMessage(player, Main.STATISTIC_MESSAGE.getString("statistic.prefix") + Main.STATISTIC_MESSAGE.getString("statistic.player.offline"));
					return;
				}

				OfflineZocker offlineZocker = new OfflineZocker(uuid);
				new StatisticOverviewInventory(offlineZocker).open(zocker);
				return;
			}

			if (target.isOnline()) {
				new StatisticOverviewInventory(new OfflineZocker(target.getUniqueId())).open(zocker);
				return;
			}

			CompatibleMessage.sendMessage(player, Main.STATISTIC_MESSAGE.getString("statistic.prefix") + Main.STATISTIC_MESSAGE.getString("statistic.player.offline"));
		}
	}

	private SubCommand findSubCommand(String name) {
		return SUB_COMMAND_LIST.stream().filter(subCommand -> subCommand.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
}
