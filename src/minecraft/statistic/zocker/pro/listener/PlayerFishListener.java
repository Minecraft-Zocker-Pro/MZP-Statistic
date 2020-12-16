package minecraft.statistic.zocker.pro.listener;

import minecraft.core.zocker.pro.config.Config;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import minecraft.statistic.zocker.pro.Main;
import minecraft.statistic.zocker.pro.StatisticType;
import minecraft.statistic.zocker.pro.StatisticZocker;

public class PlayerFishListener implements Listener {

	@EventHandler
	public void onPlayerFish(PlayerFishEvent e) {
		if (e.getState() == PlayerFishEvent.State.CAUGHT_FISH || e.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
			StatisticZocker statisticZocker = new StatisticZocker(e.getPlayer().getUniqueId());
			Config config = Main.STATISTIC_CONFIG;

			statisticZocker.add(StatisticType.FISH, 1);

			if (config.getBool("statistic.player.fish.exp.enabled")) {
				statisticZocker.addXp(
					StatisticType.FISH,
					config.getDouble("statistic.player.fish.exp.min"),
					config.getDouble("statistic.player.fish.exp.max"),
					"statistic.player.fish.exp");
			}

			if (config.getBool("statistic.player.fish.money.enabled")) {
				statisticZocker.addMoney(
					StatisticType.FISH,
					config.getDouble("statistic.player.fish.money.min"),
					config.getDouble("statistic.player.fish.money.max"),
					"statistic.player.fish.money");
			}
		}
	}
}
