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

	public void addMoney(StatisticType type, double min, double max, String configPath) {
		this.addMoney(type.toString(), min, max, configPath);
	}

	public void addMoney(String type, double min, double max, String configPath) {
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
		this.removeMoney(type.toString(), min, max, configPath);
	}

	public void removeMoney(String type, double min, double max, String configPath) {
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
		this.addXp(type.toString(), min, max, configPath);
	}

	public void addXp(String type, double min, double max, String configPath) {
		if (min == 0 || max == 0 || configPath == null) return;

		int exp = Util.getRandomNumberBetween((int) min, (int) max);
		getPlayer().giveExp(exp);

		if (Main.STATISTIC_CONFIG.getBool(configPath + ".actionbar.enabled")) {
			CompatibleMessage.sendActionBar(getPlayer(), Main.STATISTIC_MESSAGE.getString("statistic.reward.exp.actionbar.add").replace("%exp%", String.valueOf(Util.formatDouble(exp))));
		}
	}

	public void removeXp(StatisticType type, double min, double max, String configPath) {
		this.removeXp(type.toString(), min, max, configPath);
	}

	public void removeXp(String type, double min, double max, String configPath) {
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
		this.add(type.toString(), 1);
	}

	public void add(StatisticType type, int amount) {
		this.add(type.toString(), amount);
	}

	public void add(String type) {
		this.add(type, 1);
	}

	public void add(String type, int amount) {
		if (amount == 0) return;

		this.get(type).thenAcceptAsync(statistic -> {
			try {
				if (statistic == null) {
					insert(type, String.valueOf(amount));
					Bukkit.getPluginManager().callEvent(new StatisticAddEvent(getPlayer(), type, true));

					insert(type + "_TOTAL", String.valueOf(amount));
					Bukkit.getPluginManager().callEvent(new StatisticAddEvent(getPlayer(), type + "_TOTAL", true));
					return;
				}

				int value = Integer.parseInt(statistic.getValue());

				this.set(type, String.valueOf((value + amount)));
				Bukkit.getPluginManager().callEvent(new StatisticAddEvent(getPlayer(), type, true));

				String valueTopString = this.get(type + "_TOTAL").get().getValue();

				if (valueTopString == null) return;

				int valueTop = Integer.parseInt(valueTopString);

				this.set(type + "_TOTAL", String.valueOf((valueTop + amount)));
				Bukkit.getPluginManager().callEvent(new StatisticAddEvent(getPlayer(), type + "_TOTAL", true));
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		});
	}

	public void remove(StatisticType type) {
		this.remove(type.toString(), 1);
	}

	public void remove(StatisticType type, int amount) {
		this.remove(type.toString(), amount);
	}

	public CompletableFuture<Boolean> remove(String type, int amount) {
		if (amount == 0) return null;

		this.get(type).thenApplyAsync(statistic -> {
			if (statistic == null) return null;
			int value = Integer.parseInt(statistic.getValue());
			if (value >= amount) {
				Bukkit.getPluginManager().callEvent(new StatisticRemoveEvent(getPlayer(), type, true));
				return this.set(type, String.valueOf((value - amount)));
			} else {
				Bukkit.getPluginManager().callEvent(new StatisticRemoveEvent(getPlayer(), type, true));
				return this.set(type, "0");
			}
		});

		return null;
	}

	public CompletableFuture<Boolean> reset(StatisticType type) {
		Bukkit.getPluginManager().callEvent(new StatisticResetEvent(getPlayer(), type.toString(), false));
		return this.set(type.toString(), "0");
	}

	public CompletableFuture<Boolean> reset(String type) {
		Bukkit.getPluginManager().callEvent(new StatisticResetEvent(getPlayer(), type, false));
		return this.set(type, "0");
	}

	public CompletableFuture<Statistic> get(StatisticType type) {
		return this.get(type.toString());
	}

	public CompletableFuture<Statistic> get(String type) {
		return this.get(Main.STATISTIC_DATABASE_TABLE, new String[]{"statistic_value"}, new String[]{"player_uuid", "statistic_type"}, new Object[]{this.getUUIDString(), type}).thenApply(stringStringMap -> {
			if (stringStringMap == null) return null;
			return new Statistic(this.getUUIDString(), type, stringStringMap.get("statistic_value"));
		});
	}

	public CompletableFuture<Integer> getPlacement(StatisticType type) {
		return this.getPlacement(type.toString());
	}

	public CompletableFuture<Integer> getPlacement(String type) {
		return this.get(type.toUpperCase()).thenApplyAsync(statistic -> {
			if (statistic == null) return 0;
			if (statistic.getValue().equalsIgnoreCase("-1")) return 0;

			try {
				return this.getPlacement(
					Main.STATISTIC_DATABASE_TABLE,
					"statistic_value",
					"player_uuid",
					this.getUUIDString(),
					"statistic_type",
					type.toUpperCase())
					.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

			return 0;
		});
	}

	public CompletableFuture<Boolean> set(StatisticType type, String value) {
		return this.set(type.toString(), value);
	}

	public CompletableFuture<Boolean> set(String type, String value) {
		return this.get(type).thenApplyAsync(statistic -> {
			if (statistic == null) {
				try {
					return this.insert(type, value).get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}

			try {
				return this.set(Main.STATISTIC_DATABASE_TABLE,
					new String[]{"statistic_value"},
					new Object[]{value},
					new String[]{"player_uuid", "statistic_type"},
					new Object[]{this.getUUIDString(), type}).get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

			return null;
		});
	}

	private CompletableFuture<Boolean> insert(String type, String value) {
		return this.insert(Main.STATISTIC_DATABASE_TABLE,
			new String[]{"player_uuid", "statistic_type", "statistic_value"},
			new Object[]{this.getUUIDString(), type, value},
			new String[]{"player_uuid", "statistic_type"},
			new Object[]{this.getUUIDString(), type});
	}

	public CompletableFuture<String> getKD() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Statistic statisticKill = get(StatisticType.KILL_TOTAL.toString()).get();
				if (statisticKill == null) return "0";

				String killTotalString = statisticKill.getValue();
				if (killTotalString == null) return "0";

				Statistic statisticDeath = get(StatisticType.DEATH_TOTAL.toString()).get();
				if (statisticDeath == null) return "0";

				String deathTotalString = statisticDeath.getValue();
				if (deathTotalString == null) return "0";

				if (killTotalString.equalsIgnoreCase("0") && deathTotalString.equalsIgnoreCase("0")) return "0";

				return String.valueOf(Util.formatDouble(Double.parseDouble(killTotalString) / Double.parseDouble(deathTotalString)));
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

			return null;
		});
	}
}
