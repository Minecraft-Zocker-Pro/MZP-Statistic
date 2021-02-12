package minecraft.statistic.zocker.pro.listener;

import minecraft.core.zocker.pro.config.Config;
import minecraft.statistic.zocker.pro.Main;
import minecraft.statistic.zocker.pro.StatisticType;
import minecraft.statistic.zocker.pro.StatisticZocker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

public class PlayerBreedListener implements Listener {

	@EventHandler
	public void onPlayerBreed(EntityBreedEvent e) {
		if (e.getBreeder() instanceof Player) {
			StatisticZocker statisticZocker = new StatisticZocker(e.getBreeder().getUniqueId());
			Config config = Main.STATISTIC_CONFIG;

			statisticZocker.add(StatisticType.BREED);

			if (config.getBool("statistic.player.breed.exp.enabled")) {
				statisticZocker.addXp(
					StatisticType.BREED,
					config.getDouble("statistic.player.breed.exp.min"),
					config.getDouble("statistic.player.breed.exp.max"),
					"statistic.player.breed.exp");
			}

			if (config.getBool("statistic.player.breed.money.enabled")) {
				statisticZocker.addMoney(
					StatisticType.BREED,
					config.getDouble("statistic.player.breed.money.min"),
					config.getDouble("statistic.player.breed.money.max"),
					"statistic.player.breed.money");
			}
		}
	}
}
