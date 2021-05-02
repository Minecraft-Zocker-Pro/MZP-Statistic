package minecraft.statistic.zocker.pro.listener;

import minecraft.core.zocker.pro.config.Config;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import minecraft.statistic.zocker.pro.Main;
import minecraft.statistic.zocker.pro.StatisticType;
import minecraft.statistic.zocker.pro.StatisticZocker;

import java.util.List;

public class PlayerThrowListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerThrow(ProjectileLaunchEvent e) {
		if (e.isCancelled()) return;
		
		if (e.getEntity().getShooter() instanceof Player) {
			Player player = (Player) e.getEntity().getShooter();

			Config config = Main.STATISTIC_CONFIG;
			StatisticZocker statisticZocker = new StatisticZocker(player.getUniqueId());
			if (config.getString("statistic.player.throw.whitelist").equalsIgnoreCase("*")) {
				statisticZocker.add(StatisticType.THROW);
				addXp(player, statisticZocker, config);
				addMoney(player, statisticZocker, config);
			} else {
				List<String> blockWhitelist = config.getStringList("statistic.player.throw.whitelist");

				for (String name : blockWhitelist) {
					if (e.getEntityType() == EntityType.valueOf(name)) {
						statisticZocker.add(StatisticType.THROW);
						addXp(player, statisticZocker, config);
						addMoney(player, statisticZocker, config);
						return;
					}
				}
			}
		}
	}

	private void addXp(Player player, StatisticZocker statisticZocker, Config config) {
		if (!config.getBool("statistic.player.throw.exp.enabled")) return;

		statisticZocker.addXp(StatisticType.THROW,
			config.getDouble("statistic.player.throw.exp.min"),
			config.getDouble("statistic.player.throw.exp.max"),
			"statistic.player.throw.exp");
	}

	private void addMoney(Player player, StatisticZocker statisticZocker, Config config) {
		if (!config.getBool("statistic.player.throw.money.enabled")) return;

		statisticZocker.addMoney(StatisticType.THROW,
			config.getDouble("statistic.player.throw.money.min"),
			config.getDouble("statistic.player.throw.money.max"),
			"statistic.player.throw.money"
		);
	}
}
