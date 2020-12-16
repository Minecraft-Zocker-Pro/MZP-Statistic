package minecraft.statistic.zocker.pro.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import minecraft.statistic.zocker.pro.StatisticType;
import minecraft.statistic.zocker.pro.StatisticZocker;

import java.util.concurrent.ExecutionException;

public class PlayerQuitListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent e) {
		StatisticZocker statisticZocker = new StatisticZocker(e.getPlayer().getUniqueId());

		statisticZocker.get(StatisticType.STREAK).thenAccept(currentStreakString -> {
			try {
				if (currentStreakString == null) return;
				int currentStreak = Integer.valueOf(currentStreakString);

				String currentStreaksTopString = statisticZocker.get(StatisticType.STREAK_TOP).get();
				if (currentStreaksTopString == null) return;

				if (currentStreak > Integer.valueOf(currentStreaksTopString)) {
					statisticZocker.set(StatisticType.STREAK_TOP, String.valueOf(currentStreak));
				}
			} catch (InterruptedException | ExecutionException e1) {
				e1.printStackTrace();
			}

			statisticZocker.reset(StatisticType.STREAK);
		});
	}
}
