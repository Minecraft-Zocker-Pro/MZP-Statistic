package minecraft.statistic.zocker.pro.listener;

import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import minecraft.core.zocker.pro.config.Config;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import minecraft.statistic.zocker.pro.Main;
import minecraft.statistic.zocker.pro.StatisticType;
import minecraft.statistic.zocker.pro.StatisticZocker;

import java.util.List;

public class PlayerItemConsumeListener implements Listener {

	@EventHandler
	public void onPlayerConsume(PlayerItemConsumeEvent e) {
		Player player = e.getPlayer();
		Config config = Main.STATISTIC_CONFIG;
		StatisticZocker statisticZocker = new StatisticZocker(player.getUniqueId());

		if (config.getString("statistic.player.item.consume.whitelist").equalsIgnoreCase("*")) {
			statisticZocker.add(StatisticType.ITEM_CONSUME);
			addXp(statisticZocker, config);
			addMoney(statisticZocker, config);
		} else {
			List<String> blockWhitelist = config.getStringList("statistic.player.item.consume.whitelist");

			for (String name : blockWhitelist) {
				if (e.getItem().getType() == CompatibleMaterial.valueOf(name).getMaterial()) {
					statisticZocker.add(StatisticType.ITEM_CONSUME);
					addXp(statisticZocker, config);
					addMoney(statisticZocker, config);
					return;
				}
			}
		}
	}

	private void addXp(StatisticZocker statisticZocker, Config config) {
		if (!config.getBool("statistic.player.item.consume.exp.enabled")) return;

		statisticZocker.addXp(StatisticType.ITEM_CONSUME,
			config.getDouble("statistic.player.item.consume.exp.min"),
			config.getDouble("statistic.player.item.consume.exp.max"),
			"statistic.player.item.consume.exp");
	}

	private void addMoney(StatisticZocker statisticZocker, Config config) {
		if (!config.getBool("statistic.player.item.consume.money.enabled")) return;

		statisticZocker.addMoney(StatisticType.ITEM_CONSUME,
			config.getDouble("statistic.player.item.consume.money.min"),
			config.getDouble("statistic.player.item.consume.money.max"),
			"statistic.player.item.consume.money");
	}
}
