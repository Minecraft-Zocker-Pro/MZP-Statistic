package minecraft.statistic.zocker.pro.listener;

import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import minecraft.core.zocker.pro.compatibility.ServerVersion;
import minecraft.core.zocker.pro.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import minecraft.statistic.zocker.pro.Main;
import minecraft.statistic.zocker.pro.StatisticType;
import minecraft.statistic.zocker.pro.StatisticZocker;

import java.util.concurrent.ExecutionException;

public class PlayerDeathListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (!Main.STATISTIC_CONFIG.getBool("statistic.player.death.message.enabled")) {
			e.setDeathMessage(null);
		}

		// Player
		Player player = e.getEntity();

		if (ServerVersion.isServerVersionAtOrBelow(ServerVersion.V1_8)) {
			player.getLocation().getWorld().playEffect(player.getLocation().add(0.0D, 1.0D, 0.0D), Effect.STEP_SOUND, 152);
		} else {
			player.getWorld().playEffect(player.getLocation().add(0.0D, 1.0D, 0.0D), Effect.STEP_SOUND, CompatibleMaterial.REDSTONE_BLOCK.getMaterial());
		}

		StatisticZocker statisticZocker = new StatisticZocker(player.getUniqueId());

		// Streak
		statisticZocker.get(StatisticType.STREAK).thenAcceptAsync(statistic -> {
			if (statistic == null) return;
			int currentStreak = Integer.parseInt(statistic.getValue());

			statisticZocker.add(StatisticType.DEATH, 1);
			statisticZocker.reset(StatisticType.STREAK);

			try {
				String streaksTop = statisticZocker.get(StatisticType.STREAK_TOP).get().getValue();
				if (streaksTop != null) {
					if (currentStreak > Integer.parseInt(streaksTop)) {
						statisticZocker.set(StatisticType.STREAK_TOP, String.valueOf(currentStreak));
					}
				}
			} catch (InterruptedException | ExecutionException e2) {
				e2.printStackTrace();
			}
		});

		// Killer
		Player playerKiller = null;

		if (e.getEntity().

			getLastDamageCause() instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent edbe = (EntityDamageByEntityEvent) e.getEntity().getLastDamageCause();
			if (edbe.getDamager().hasMetadata("player")) {
				Entity entity = edbe.getDamager();
				Player killer = Bukkit.getPlayer(entity.getMetadata("player").get(0).asString());
				if (killer != null) {
					playerKiller = killer;
				}
			} else {
				playerKiller = player.getKiller();
			}
		} else {
			playerKiller = player.getKiller();
		}

		if (playerKiller != null) {
			StatisticZocker statisticKillerZocker = new StatisticZocker(playerKiller.getUniqueId());
			statisticKillerZocker.add(StatisticType.KILL, 1);
			statisticKillerZocker.add(StatisticType.STREAK, 1);

			addXp(statisticKillerZocker);
			addMoney(statisticKillerZocker);
		}

		removeMoney(statisticZocker);
		respawn(player);
	}

	private void addXp(StatisticZocker statisticZocker) {
		Config config = Main.STATISTIC_CONFIG;
		if (!config.getBool("statistic.player.kill.exp.enabled")) return;

		statisticZocker.addXp(StatisticType.KILL,
			config.getDouble("statistic.player.kill.exp.min"),
			config.getDouble("statistic.player.kill.exp.max"),
			"statistic.player.kill.exp");
	}

	private void addMoney(StatisticZocker statisticZocker) {
		Config config = Main.STATISTIC_CONFIG;
		if (!config.getBool("statistic.player.kill.money.enabled")) return;

		statisticZocker.addMoney(StatisticType.KILL,
			config.getDouble("statistic.player.kill.money.min"),
			config.getDouble("statistic.player.kill.money.max"),
			"statistic.player.kill.money");
	}

	private void removeMoney(StatisticZocker statisticZocker) {
		Config config = Main.STATISTIC_CONFIG;
		if (!config.getBool("statistic.player.death.money.enabled")) return;

		statisticZocker.removeMoney(StatisticType.DEATH,
			config.getDouble("statistic.player.death.money.min"),
			config.getDouble("statistic.player.death.money.max"),
			"statistic.player.death.money");
	}

	private void respawn(Player player) {
		new BukkitRunnable() {
			@Override
			public void run() {
				player.spigot().respawn();
			}
		}.runTask(Main.getPlugin());
	}
}
