package minecraft.statistic.zocker.pro;

import minecraft.core.zocker.pro.Zocker;
import minecraft.core.zocker.pro.compatibility.CompatibleMessage;
import minecraft.core.zocker.pro.util.Util;
import minecraft.statistic.zocker.pro.event.StatisticAddEvent;
import minecraft.statistic.zocker.pro.event.StatisticMoneyAddEvent;
import minecraft.statistic.zocker.pro.event.StatisticRemoveEvent;
import minecraft.statistic.zocker.pro.event.StatisticResetEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class StatisticZocker extends Zocker {

	public StatisticZocker(Player player) {
		super(player);
	}

	public StatisticZocker(UUID uuid) {
		super(uuid);
	}

	@Deprecated
	public StatisticZocker(String dummy) {
		super(dummy);
	}

	// TODO type for custom reward events
	public void addMoney(StatisticType type, double min, double max, String configPath) {
		if (min == 0 || max == 0 || configPath == null) return;

		new BukkitRunnable() {
			@Override
			public void run() {
				double money = Util.getRandomNumberBetween(min, max);

				StatisticMoneyAddEvent statisticMoneyAddEvent = new StatisticMoneyAddEvent(getPlayer(), type, configPath, money);
				Bukkit.getPluginManager().callEvent(statisticMoneyAddEvent);

				if (statisticMoneyAddEvent.isCancelled()) return;

				if (Main.hasVaultSupport()) {
					Main.getEconomy().depositPlayer(getPlayer(), money);

					if (Main.STATISTIC_CONFIG.getBool(configPath + ".actionbar.enabled")) {
						CompatibleMessage.sendActionBar(getPlayer(), Main.STATISTIC_MESSAGE.getString("statistic.reward.money.actionbar.add").replace("%money%", String.valueOf(Util.formatDouble(money))));
					}
				}

			}
		}.runTaskAsynchronously(Main.getPlugin());
	}

	public void removeMoney(StatisticType type, double min, double max, String configPath) {
		if (min == 0 || max == 0 || configPath == null) return;

		new BukkitRunnable() {
			@Override
			public void run() {
				double money = Util.getRandomNumberBetween(min, max);

				if (Main.hasVaultSupport()) {
					Main.getEconomy().withdrawPlayer(getPlayer(), money);

					if (Main.STATISTIC_CONFIG.getBool(configPath + ".actionbar.enabled")) {
						CompatibleMessage.sendActionBar(getPlayer(), Main.STATISTIC_MESSAGE.getString("statistic.reward.money.actionbar.remove").replace("%money%", String.valueOf(Util.formatDouble(money))));
					}
				}
			}
		}.runTaskAsynchronously(Main.getPlugin());
	}

	public void addXp(StatisticType type, double min, double max, String configPath) {
		if (min == 0 || max == 0 || configPath == null) return;

		int exp = Util.getRandomNumberBetween((int) min, (int) max);
		getPlayer().giveExp(exp);

		if (Main.STATISTIC_CONFIG.getBool(configPath + ".actionbar.enabled")) {
			CompatibleMessage.sendActionBar(getPlayer(), Main.STATISTIC_MESSAGE.getString("statistic.reward.exp.actionbar.add").replace("%exp%", String.valueOf(Util.formatDouble(exp))));
		}
	}

	public void removeXp(StatisticType type, double min, double max, String configPath) {
		if (min == 0 || max == 0 || configPath == null) return;

		int expRemove = Util.getRandomNumberBetween((int) min, (int) max);
		Player player = getPlayer();
		if (player.getTotalExperience() > expRemove) {
			player.setTotalExperience(player.getTotalExperience() - expRemove);

			if (Main.STATISTIC_CONFIG.getBool(configPath + ".actionbar.enabled")) {
				CompatibleMessage.sendActionBar(getPlayer(), Main.STATISTIC_MESSAGE.getString("statistic.reward.exp.actionbar.remove").replace("%exp%", String.valueOf(Util.formatDouble(expRemove))));
			}
		}
	}

	public void add(StatisticType type) {
		this.add(type, 1);
	}

	public void add(StatisticType type, int amount) {
		if (amount == 0) return;

		this.get(type).thenAcceptAsync(valueString -> {
			try {
				if (valueString == null) return;
				int value = Integer.valueOf(valueString);

				this.set(type, String.valueOf((value + amount)));

				StatisticType typeTotal = StatisticType.valueOf(type.name() + "_TOTAL");
				String valueTopString = this.get(typeTotal).get();

				if (valueTopString == null) return;

				int valueTop = Integer.valueOf(valueTopString);

				this.set(typeTotal, String.valueOf((valueTop + amount)));

				Bukkit.getPluginManager().callEvent(new StatisticAddEvent(getPlayer(), type, true));
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		});
	}

	public void remove(StatisticType type) {
		this.remove(type, 1);
	}

	public void remove(StatisticType type, int amount) {
		if (amount == 0) return;

		this.get(type).thenAcceptAsync(valueString -> {
			if (valueString == null) return;
			int value = Integer.valueOf(valueString);
			if (value >= amount) {
				Bukkit.getPluginManager().callEvent(new StatisticRemoveEvent(getPlayer(), type, true));
				this.set(type, String.valueOf((value - amount)));
			} else {
				Bukkit.getPluginManager().callEvent(new StatisticRemoveEvent(getPlayer(), type, true));
				this.set(type, "0");
			}
		});
	}

	public void reset(StatisticType type) {
		Bukkit.getPluginManager().callEvent(new StatisticResetEvent(getPlayer(), type, false));
		this.set(type, "0");
	}

	public CompletableFuture<String> get(StatisticType type) {
		return this.get(Main.STATISTIC_DATABASE_TABLE, type.name().toLowerCase(), "player_uuid", this.getUUIDString());
	}

	public CompletableFuture<Integer> getPlacement(StatisticType type) {
		return this.getPlacement(Main.STATISTIC_DATABASE_TABLE, type.name().toLowerCase(), "player_uuid", this.getUUIDString());
	}

	public CompletableFuture<Boolean> set(StatisticType type, String value) {
		return this.set(Main.STATISTIC_DATABASE_TABLE, type.name().toLowerCase(), value, "player_uuid", this.getUUIDString());
	}

	public CompletableFuture<String> getKD() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				String killTotalString = get(StatisticType.KILL_TOTAL).get();
				if (killTotalString == null) return null;

				String deathTotalString = get(StatisticType.DEATH_TOTAL).get();
				if (deathTotalString == null) return null;

				return String.valueOf(Util.formatDouble(Double.valueOf(killTotalString) / Double.valueOf(deathTotalString)));
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

			return null;
		});
	}
}
