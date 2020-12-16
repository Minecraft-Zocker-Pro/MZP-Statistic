package minecraft.statistic.zocker.pro.listener;

import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import minecraft.core.zocker.pro.config.Config;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemBreakEvent;
import minecraft.statistic.zocker.pro.Main;
import minecraft.statistic.zocker.pro.StatisticType;
import minecraft.statistic.zocker.pro.StatisticZocker;

import java.util.List;

public class PlayerItemBreakListener implements Listener {

	@EventHandler
	public void onPlayerItemBreak(PlayerItemBreakEvent e) {
		Player player = e.getPlayer();
		Config config = Main.STATISTIC_CONFIG;
		StatisticZocker statisticZocker = new StatisticZocker(player.getUniqueId());

		if (config.getString("statistic.player.break.place.whitelist").equalsIgnoreCase("*")) {
			statisticZocker.add(StatisticType.ITEM_BREAK);
			addXp(statisticZocker, config);
			addMoney(statisticZocker, config);
		} else {
			List<String> itemWhitelist = config.getStringList("statistic.player.break.place.whitelist");

			for (String name : itemWhitelist) {
				if (e.getBrokenItem().getType() == CompatibleMaterial.valueOf(name).getMaterial()) {
					statisticZocker.add(StatisticType.ITEM_BREAK);
					addXp(statisticZocker, config);
					addMoney(statisticZocker, config);
					return;
				}
			}
		}
	}

	private void addXp(StatisticZocker statisticZocker, Config config) {
		if (!config.getBool("statistic.player.break.place.exp.enabled")) return;

		statisticZocker.addXp(StatisticType.ITEM_BREAK,
			config.getDouble("statistic.player.break.place.exp.min"),
			config.getDouble("statistic.player.break.place.exp.max"),
			"statistic.player.break.place.exp");
	}

	private void addMoney(StatisticZocker statisticZocker, Config config) {
		if (!config.getBool("statistic.player.break.place.money.enabled")) return;

		statisticZocker.addMoney(StatisticType.ITEM_BREAK,
			config.getDouble("statistic.player.break.place.money.min"),
			config.getDouble("statistic.player.break.place.money.max"),
			"statistic.player.break.place.money");
	}
}
