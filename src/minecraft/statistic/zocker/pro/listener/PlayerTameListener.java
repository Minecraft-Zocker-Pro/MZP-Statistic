package minecraft.statistic.zocker.pro.listener;

import minecraft.core.zocker.pro.config.Config;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;
import minecraft.statistic.zocker.pro.Main;
import minecraft.statistic.zocker.pro.StatisticType;
import minecraft.statistic.zocker.pro.StatisticZocker;

public class PlayerTameListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerTame(EntityTameEvent e) {
		if (e.isCancelled()) return;
		
		StatisticZocker statisticZocker = new StatisticZocker(e.getOwner().getUniqueId());
		Config config = Main.STATISTIC_CONFIG;

		statisticZocker.add(StatisticType.TAME);

		if (config.getBool("statistic.player.tame.exp.enabled")) {
			statisticZocker.addXp(
				StatisticType.TAME,
				config.getDouble("statistic.player.tame.exp.min"),
				config.getDouble("statistic.player.tame.exp.max"),
				"statistic.player.tame.exp");
		}

		if (config.getBool("statistic.player.tame.money.enabled")) {
			statisticZocker.addMoney(
				StatisticType.TAME,
				config.getDouble("statistic.player.tame.money.min"),
				config.getDouble("statistic.player.tame.money.max"),
				"statistic.player.tame.money");
		}
	}
}