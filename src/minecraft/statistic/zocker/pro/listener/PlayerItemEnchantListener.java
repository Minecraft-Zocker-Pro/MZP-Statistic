package minecraft.statistic.zocker.pro.listener;

import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import minecraft.core.zocker.pro.config.Config;
import minecraft.statistic.zocker.pro.Main;
import minecraft.statistic.zocker.pro.StatisticType;
import minecraft.statistic.zocker.pro.StatisticZocker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

import java.util.List;

public class PlayerItemEnchantListener implements Listener {

	@EventHandler
	public void onPlayerItemEnchant(EnchantItemEvent e) {
		Player player = e.getEnchanter();

		Config config = Main.STATISTIC_CONFIG;
		StatisticZocker statisticZocker = new StatisticZocker(player.getUniqueId());

		if (config.getString("statistic.player.item.enchant.whitelist").equalsIgnoreCase("*")) {
			statisticZocker.add(StatisticType.ITEM_ENCHANT);
			addXp(statisticZocker, config);
			addMoney(statisticZocker, config);
		} else {
			List<String> blockWhitelist = config.getStringList("statistic.player.item.enchant.whitelist");

			for (String name : blockWhitelist) {
				if (e.getItem().getType() == CompatibleMaterial.valueOf(name).getMaterial()) {
					statisticZocker.add(StatisticType.ITEM_ENCHANT);
					addXp(statisticZocker, config);
					addMoney(statisticZocker, config);
					return;
				}
			}
		}
	}

	private void addXp(StatisticZocker statisticZocker, Config config) {
		if (!config.getBool("statistic.player.item.enchant.exp.enabled")) return;

		statisticZocker.addXp(StatisticType.ITEM_ENCHANT,
			config.getDouble("statistic.player.item.enchant.exp.min"),
			config.getDouble("statistic.player.item.enchant.exp.max"),
			"statistic.player.item.enchant.exp");
	}

	private void addMoney(StatisticZocker statisticZocker, Config config) {
		if (!config.getBool("statistic.player.item.enchant.money.enabled")) return;

		statisticZocker.addMoney(StatisticType.ITEM_CRAFT,
			config.getDouble("statistic.player.item.enchant.money.min"),
			config.getDouble("statistic.player.item.enchant.money.max"),
			"statistic.player.item.enchant.money");
	}
}
