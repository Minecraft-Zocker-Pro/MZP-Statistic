package minecraft.statistic.zocker.pro.listener;

import minecraft.core.zocker.pro.compatibility.CompatibleHand;
import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import minecraft.core.zocker.pro.compatibility.CompatibleSound;
import minecraft.core.zocker.pro.compatibility.ServerVersion;
import minecraft.core.zocker.pro.config.Config;
import minecraft.core.zocker.pro.event.PlayerVoidFallEvent;
import minecraft.essential.zocker.pro.command.spawn.SpawnCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import minecraft.statistic.zocker.pro.Main;
import minecraft.statistic.zocker.pro.StatisticType;
import minecraft.statistic.zocker.pro.StatisticZocker;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class PlayerVoidFallListener implements Listener {

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerVoidFall(PlayerVoidFallEvent e) {
		if (e.isCancelled()) return;

		Player player = e.getPlayer();
		Config config = Main.STATISTIC_CONFIG;

		List<String> blacklistWorlds = config.getStringList("statistic.void.reset.world.blacklist");
		for (String worldName : blacklistWorlds) {
			if (player.getWorld().getName().equalsIgnoreCase(worldName)) {
				return;
			}
		}

		// Reset inventory
		if (config.getBool("statistic.void.reset.inventory")) {
			player.getInventory().clear();
			player.getInventory().setHelmet(null);
			player.getInventory().setChestplate(null);
			player.getInventory().setLeggings(null);
			player.getInventory().setBoots(null);

			// Hand
			player.setItemInHand(new ItemStack(Material.AIR));
			if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9)) {
				CompatibleHand.OFF_HAND.setItem(player, CompatibleMaterial.AIR.getItem());
			}

			player.setItemOnCursor(new ItemStack(Material.AIR));

			new BukkitRunnable() {
				@Override
				public void run() {
					// Crafting menu
					player.getOpenInventory().getTopInventory().setItem(0, new ItemStack(Material.AIR));
					player.getOpenInventory().getTopInventory().setItem(1, new ItemStack(Material.AIR));
					player.getOpenInventory().getTopInventory().setItem(2, new ItemStack(Material.AIR));
					player.getOpenInventory().getTopInventory().setItem(3, new ItemStack(Material.AIR));
					player.getOpenInventory().getTopInventory().setItem(4, new ItemStack(Material.AIR));

					// Reset potion
					if (config.getBool("statistic.void.reset.inventory")) {
						for (PotionEffect effect : player.getActivePotionEffects()) {
							player.removePotionEffect(effect.getType());
						}
					}
				}
			}.runTask(Main.getPlugin());
		}

		// Reset level
		if (config.getBool("statistic.void.reset.inventory")) {
			player.setLevel(0);
		}

		// Reset xp
		if (config.getBool("statistic.void.reset.inventory")) {
			player.setExp(0.0F);
		}

		player.setHealth(20.0D);
		player.setFoodLevel(20);
		player.setSaturation(20);
		player.setFireTicks(0);
		player.setFallDistance(0);

		StatisticZocker statisticZocker = new StatisticZocker(player.getUniqueId());
		statisticZocker.get(StatisticType.STREAK).thenAccept(statistic -> {
			if (statistic == null) return;
			int currentStreak = Integer.parseInt(statistic.getValue());

			statisticZocker.get(StatisticType.STREAK_TOP).thenAccept(currentStreakTop -> {
				if (currentStreakTop == null || currentStreakTop.getValue() == null) return;

				if (currentStreak > Integer.parseInt(currentStreakTop.getValue())) {
					statisticZocker.set(StatisticType.STREAK_TOP, String.valueOf(currentStreak));
				}
			});

			statisticZocker.reset(StatisticType.STREAK);
		});

		statisticZocker.add(StatisticType.VOID_FALL, 1);
		statisticZocker.add(StatisticType.DEATH, 1);
		removeMoney(statisticZocker);
		removeXp(statisticZocker);

		if (Bukkit.getPluginManager().isPluginEnabled("MZP-Essential")) {
			if (SpawnCommand.getSpawnLocation() != null) {
				runSynchronous(player, SpawnCommand.getSpawnLocation());
			} else {
				runSynchronous(player, player.getWorld().getSpawnLocation());
			}
		} else {
			runSynchronous(player, player.getWorld().getSpawnLocation());
		}

		Player playerKiller = player.getKiller();
		if (playerKiller == null) return;
		if (player == playerKiller) return;

		StatisticZocker statisticKillerZocker = new StatisticZocker(playerKiller.getUniqueId());
		statisticKillerZocker.add(StatisticType.KILL, 1);
		statisticKillerZocker.add(StatisticType.STREAK, 1);

		addXp(statisticKillerZocker);
		addMoney(statisticKillerZocker);
	}


	private void addXp(StatisticZocker statisticZocker) {
		Config config = Main.STATISTIC_CONFIG;
		if (!config.getBool("statistic.player.kill.exp.enabled")) return;

		statisticZocker.addXp(StatisticType.KILL,
			config.getDouble("statistic.player.kill.exp.min"),
			config.getDouble("statistic.player.kill.exp.max"),
			"statistic.player.kill.exp");
	}

	private void removeXp(StatisticZocker statisticZocker) {
		Config config = Main.STATISTIC_CONFIG;
		if (!config.getBool("statistic.player.death.exp.enabled")) return;

		statisticZocker.removeXp(StatisticType.DEATH,
			config.getDouble("statistic.player.death.exp.min"),
			config.getDouble("statistic.player.death.exp.max"),
			"statistic.player.death.exp");
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

	private void runSynchronous(Player player, Location location) {
		new BukkitRunnable() {
			@Override
			public void run() {
				player.teleport(location);
			}
		}.runTask(Main.getPlugin());

		CompatibleSound.playTeleportSound(player);
	}
}
