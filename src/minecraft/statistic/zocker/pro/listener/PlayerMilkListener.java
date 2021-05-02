package minecraft.statistic.zocker.pro.listener;

import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import minecraft.core.zocker.pro.config.Config;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketFillEvent;
import minecraft.statistic.zocker.pro.Main;
import minecraft.statistic.zocker.pro.StatisticType;
import minecraft.statistic.zocker.pro.StatisticZocker;

public class PlayerMilkListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerMilk(PlayerBucketFillEvent e) {
		if (e.isCancelled()) return;
		if (e.getItemStack() == null) return;

		if (e.getItemStack().getType() == CompatibleMaterial.MILK_BUCKET.getMaterial()) {
			StatisticZocker statisticZocker = new StatisticZocker(e.getPlayer().getUniqueId());
			Config config = Main.STATISTIC_CONFIG;

			statisticZocker.add(StatisticType.MILK);

			if (config.getBool("statistic.player.milk.exp.enabled")) {
				statisticZocker.addXp(
					StatisticType.MILK,
					config.getDouble("statistic.player.milk.exp.min"),
					config.getDouble("statistic.player.milk.exp.max"),
					"statistic.player.milk.exp");
			}

			if (config.getBool("statistic.player.milk.money.enabled")) {
				statisticZocker.addMoney(
					StatisticType.MILK,
					config.getDouble("statistic.player.milk.money.min"),
					config.getDouble("statistic.player.milk.money.max"),
					"statistic.player.milk.money");
			}
		}
	}
}
