package minecraft.statistic.zocker.pro.listener;

import minecraft.core.zocker.pro.config.Config;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import minecraft.statistic.zocker.pro.Main;
import minecraft.statistic.zocker.pro.StatisticType;
import minecraft.statistic.zocker.pro.StatisticZocker;

import java.util.List;

public class EntityKillListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityKill(EntityDeathEvent e) {
		if (e.isCancelled()) return;
		
		Config config = Main.STATISTIC_CONFIG;

		if (config.getBool("statistic.player.kill.hostile.enabled")) {
			List<String> hostilesWhitelist = config.getStringList("statistic.player.kill.hostile.whitelist");
			for (String name : hostilesWhitelist) {
				if (EntityType.fromName(name) == null) continue;
				if (e.getEntity().getType() == EntityType.valueOf(name)) {
					if (e.getEntity().getKiller() == null) continue;

					StatisticZocker statisticZocker = new StatisticZocker(e.getEntity().getKiller().getUniqueId());
					statisticZocker.add(StatisticType.HOSTILE_KILL, 1);

					if (config.getBool("statistic.player.kill.hostile.exp.enabled")) {
						statisticZocker.addXp(
							StatisticType.HOSTILE_KILL,
							config.getDouble("statistic.player.kill.hostile.exp.min"),
							config.getDouble("statistic.player.kill.hostile.exp.max"),
							"statistic.player.kill.hostile.exp");
					}

					if (config.getBool("statistic.player.kill.hostile.money.enabled")) {
						statisticZocker.addXp(
							StatisticType.HOSTILE_KILL,
							config.getDouble("statistic.player.kill.hostile.money.min"),
							config.getDouble("statistic.player.kill.hostile.money.max"),
							"statistic.player.kill.hostile.money");
					}
				}

				return;
			}
		}

		if (config.getBool("statistic.player.kill.friendly.enabled")) {
			List<String> hostilesWhitelist = config.getStringList("statistic.player.kill.friendly.whitelist");
			for (String name : hostilesWhitelist) {
				if (EntityType.fromName(name) == null) continue;
				if (e.getEntity().getType() == EntityType.valueOf(name)) {
					if (e.getEntity().getKiller() == null) continue;

					StatisticZocker statisticZocker = new StatisticZocker(e.getEntity().getKiller().getUniqueId());
					statisticZocker.add(StatisticType.FRIENDLY_KILL, 1);

					if (config.getBool("statistic.player.kill.friendly.exp.enabled")) {
						statisticZocker.addXp(
							StatisticType.FRIENDLY_KILL,
							config.getDouble("statistic.player.kill.friendly.exp.min"),
							config.getDouble("statistic.player.kill.friendly.exp.max"),
							"statistic.player.kill.friendly.exp");
					}

					if (config.getBool("statistic.player.kill.friendly.money.enabled")) {
						statisticZocker.addXp(
							StatisticType.FRIENDLY_KILL,
							config.getDouble("statistic.player.kill.friendly.money.min"),
							config.getDouble("statistic.player.kill.friendly.money.max"),
							"statistic.player.kill.friendly.money");
					}
				}

				return;
			}
		}

	}

}
