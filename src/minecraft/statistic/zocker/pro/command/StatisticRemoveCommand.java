package minecraft.statistic.zocker.pro.command;

import minecraft.core.zocker.pro.OfflineZocker;
import minecraft.core.zocker.pro.command.SubCommand;
import minecraft.core.zocker.pro.compatibility.CompatibleMessage;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import minecraft.statistic.zocker.pro.Main;
import minecraft.statistic.zocker.pro.StatisticType;
import minecraft.statistic.zocker.pro.StatisticZocker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StatisticRemoveCommand extends SubCommand {

	private final List<String> tabCompleteList = new ArrayList<>();

	public StatisticRemoveCommand() {
		super("remove");

		for (StatisticType type : StatisticType.values()) {
			tabCompleteList.add(type.toString());
		}
	}

	@Override
	public String getUsage() {
		return Main.STATISTIC_MESSAGE.getString("statistic.prefix") + "§3Type §6/statistic remove <player> <type> <amount>";
	}

	@Override
	public String getPermission() {
		return "mzp.statistic.command.statistic.remove";
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		if (args.length <= 2) {
			CompatibleMessage.sendMessage(sender, getUsage());
			return;
		}

		if (args.length == 3) {
			String targetName = args[0];
			String typeString = args[1];
			String amountString = args[2];

			if (targetName == null || typeString == null || amountString == null) {
				CompatibleMessage.sendMessage(sender, getUsage());
				return;
			}

			if (!NumberUtils.isNumber(amountString)) {
				CompatibleMessage.sendMessage(sender, getUsage());
				return;
			}

			Player target = Bukkit.getPlayer(targetName);

			if (target == null) {
				UUID uuid = OfflineZocker.fetchUUID(args[0]);
				if (uuid == null) {
					CompatibleMessage.sendMessage(sender, Main.STATISTIC_MESSAGE.getString("statistic.prefix") + Main.STATISTIC_MESSAGE.getString("statistic.player.offline"));
					return;
				}

				StatisticZocker statisticZocker = new StatisticZocker(uuid);
				statisticZocker.remove(typeString.toUpperCase(), Integer.parseInt(amountString));
				sendAddedMessage(sender, Main.STATISTIC_MESSAGE.getString("statistic.command.statistic.remove")
					.replace("%type%", typeString)
					.replace("%target%", targetName)
					.replace("%amount%", amountString));
				return;
			}

			StatisticZocker statisticZocker = new StatisticZocker(target.getUniqueId());
			statisticZocker.remove(typeString.toUpperCase(), Integer.valueOf(amountString));
			sendAddedMessage(sender, Main.STATISTIC_MESSAGE.getString("statistic.command.statistic.remove")
				.replace("%type%", typeString)
				.replace("%target%", targetName)
				.replace("%amount%", amountString));
		}
	}

	private void sendAddedMessage(CommandSender sender, String message) {
		CompatibleMessage.sendMessage(sender, Main.STATISTIC_MESSAGE.getString("statistic.prefix") + message);
	}

	@Override
	public List<String> getCompletions(CommandSender sender, String[] args) {
		return tabCompleteList;
	}
}
