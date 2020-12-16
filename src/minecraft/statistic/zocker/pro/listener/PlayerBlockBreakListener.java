package minecraft.statistic.zocker.pro.listener;

import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import minecraft.core.zocker.pro.config.Config;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import minecraft.statistic.zocker.pro.Main;
import minecraft.statistic.zocker.pro.StatisticType;
import minecraft.statistic.zocker.pro.StatisticZocker;

import java.util.List;

public class PlayerBlockBreakListener implements Listener {

	@EventHandler
	public void onPlayerBlockBreak(BlockBreakEvent e) {
		Block block = e.getBlock();
		Player player = e.getPlayer();

		Config config = Main.STATISTIC_CONFIG;
		StatisticZocker statisticZocker = new StatisticZocker(player.getUniqueId());

		if (config.getString("statistic.player.block.break.whitelist").equalsIgnoreCase("*")) {
			statisticZocker.add(StatisticType.BLOCK_BREAK, 1);
			addXp(player, statisticZocker, config);
			addMoney(player, statisticZocker, config);
		} else {
			List<String> blockWhitelist = config.getStringList("statistic.player.block.break.whitelist");

			for (String name : blockWhitelist) {
				if (block.getType() == CompatibleMaterial.valueOf(name).getMaterial()) {
					statisticZocker.add(StatisticType.BLOCK_BREAK, 1);
					addXp(player, statisticZocker, config);
					addMoney(player, statisticZocker, config);
					return;
				}
			}
		}
	}

	private void addXp(Player player, StatisticZocker statisticZocker, Config config) {
		if (!config.getBool("statistic.player.block.break.exp.enabled")) return;

		statisticZocker.addXp(StatisticType.BLOCK_BREAK,
			config.getDouble("statistic.player.block.break.exp.min"),
			config.getDouble("statistic.player.block.break.exp.max"),
			"statistic.player.block.break.exp");
	}

	private void addMoney(Player player, StatisticZocker statisticZocker, Config config) {
		if (!config.getBool("statistic.player.block.break.money.enabled")) return;

		statisticZocker.addMoney(StatisticType.BLOCK_BREAK,
			config.getDouble("statistic.player.block.break.money.min"),
			config.getDouble("statistic.player.block.break.money.max"),
			"statistic.player.block.break.money");
	}
}
