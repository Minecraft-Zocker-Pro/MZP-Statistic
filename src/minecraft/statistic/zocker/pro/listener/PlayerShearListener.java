package minecraft.statistic.zocker.pro.listener;

import minecraft.core.zocker.pro.config.Config;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;
import minecraft.statistic.zocker.pro.Main;
import minecraft.statistic.zocker.pro.StatisticType;
import minecraft.statistic.zocker.pro.StatisticZocker;

public class PlayerShearListener implements Listener {

	private static final Config CONFIG = Config.getConfig("config.yml", "stats");

	@EventHandler
	public void onPlayerShear(PlayerShearEntityEvent e) {
		StatisticZocker statisticZocker = new StatisticZocker(e.getPlayer().getUniqueId());
		Config config = Main.STATISTIC_CONFIG;

		statisticZocker.add(StatisticType.SHEAR);

		if (config.getBool("statistic.player.shear.exp.enabled")) {
			statisticZocker.addXp(
				StatisticType.SHEAR,
				config.getDouble("statistic.player.shear.exp.min"),
				config.getDouble("statistic.player.shear.exp.max"),
				"statistic.player.shear.exp");
		}

		if (config.getBool("statistic.player.shear.money.enabled")) {
			statisticZocker.addMoney(
				StatisticType.SHEAR,
				config.getDouble("statistic.player.shear.money.min"),
				config.getDouble("statistic.player.shear.money.max"),
				"statistic.player.shear.money");
		}
	}
}
