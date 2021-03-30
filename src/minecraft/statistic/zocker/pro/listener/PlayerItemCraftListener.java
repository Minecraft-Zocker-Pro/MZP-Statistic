package minecraft.statistic.zocker.pro.listener;

import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import minecraft.core.zocker.pro.config.Config;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import minecraft.statistic.zocker.pro.Main;
import minecraft.statistic.zocker.pro.StatisticType;
import minecraft.statistic.zocker.pro.StatisticZocker;

import java.util.List;

public class PlayerItemCraftListener implements Listener {

	@EventHandler
	public void onPlayerItemCraft(CraftItemEvent e) {
		Player player = (Player) e.getView().getPlayer();
		Config config = Main.STATISTIC_CONFIG;
		StatisticZocker statisticZocker = new StatisticZocker(player.getUniqueId());

		if (config.getString("statistic.player.item.craft.whitelist").equalsIgnoreCase("*")) {
			statisticZocker.add(StatisticType.ITEM_CRAFT);
			addXp(statisticZocker, config);
			addMoney(statisticZocker, config);
		} else {
			List<String> blockWhitelist = config.getStringList("statistic.player.item.craft.whitelist");

			for (String name : blockWhitelist) {
				if (e.getRecipe().getResult().getType() == CompatibleMaterial.valueOf(name).getMaterial()) {
					statisticZocker.add(StatisticType.ITEM_CRAFT);
					addXp(statisticZocker, config);
					addMoney(statisticZocker, config);
					return;
				}
			}
		}
	}

	private void addXp(StatisticZocker statisticZocker, Config config) {
		if (!config.getBool("statistic.player.item.craft.exp.enabled")) return;

		statisticZocker.addXp(StatisticType.ITEM_CRAFT,
			config.getDouble("statistic.player.item.craft.exp.min"),
			config.getDouble("statistic.player.item.craft.exp.max"),
			"statistic.player.item.craft.exp");
	}

	private void addMoney(StatisticZocker statisticZocker, Config config) {
		if (!config.getBool("statistic.player.item.craft.money.enabled")) return;

		statisticZocker.addMoney(StatisticType.ITEM_CRAFT,
			config.getDouble("statistic.player.item.craft.money.min"),
			config.getDouble("statistic.player.item.craft.money.max"),
			"statistic.player.item.craft.money");
	}
}
