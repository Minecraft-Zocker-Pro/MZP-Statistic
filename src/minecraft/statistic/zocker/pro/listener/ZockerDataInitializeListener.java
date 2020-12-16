package minecraft.statistic.zocker.pro.listener;

import minecraft.core.zocker.pro.Zocker;
import minecraft.core.zocker.pro.event.ZockerDataInitializeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import minecraft.statistic.zocker.pro.Main;
import minecraft.statistic.zocker.pro.StatisticZocker;

public class ZockerDataInitializeListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onZockerDataInitialize(ZockerDataInitializeEvent e) {
		Zocker zocker = e.getZocker();
		if (zocker == null) return;

		StatisticZocker statisticZocker = new StatisticZocker(zocker.getUUID());

		statisticZocker.hasValueAsync(Main.STATISTIC_DATABASE_TABLE, "player_uuid", "player_uuid", statisticZocker.getUUIDString()).thenApply(aBoolean -> {
			if (aBoolean) return true;

			statisticZocker.insert(Main.STATISTIC_DATABASE_TABLE, "player_uuid", statisticZocker.getPlayer().getUniqueId().toString());

			return aBoolean;
		});
	}
}
