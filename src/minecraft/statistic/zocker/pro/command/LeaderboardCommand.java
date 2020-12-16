package minecraft.statistic.zocker.pro.command;

import minecraft.core.zocker.pro.Zocker;
import minecraft.core.zocker.pro.command.Command;
import minecraft.statistic.zocker.pro.StatisticType;
import minecraft.statistic.zocker.pro.inventory.LeaderboardOverviewInventory;
import minecraft.statistic.zocker.pro.inventory.LeaderboardTopInventory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LeaderboardCommand extends Command {

	public LeaderboardCommand() {
		super("leaderboard", "mzp.statistic.command.leaderboard", new String[]{"leaderboards"});
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		return null;
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Zocker zocker = Zocker.getZocker(((Player) sender).getUniqueId());

			if (args.length <= 1) {
				new LeaderboardOverviewInventory(zocker).open(zocker);
			} else {
				if (!sender.hasPermission("mzp.statistic.command.leaderboard.top")) return;

				String type = args[0];
				if (type == null) return;

				new LeaderboardTopInventory(zocker, StatisticType.valueOf(type.toUpperCase())).open(zocker);
			}

		}
	}
}
