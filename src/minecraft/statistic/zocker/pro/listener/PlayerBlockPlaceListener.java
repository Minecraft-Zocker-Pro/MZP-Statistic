package minecraft.statistic.zocker.pro.listener;

import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import minecraft.core.zocker.pro.config.Config;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import minecraft.statistic.zocker.pro.Main;
import minecraft.statistic.zocker.pro.StatisticType;
import minecraft.statistic.zocker.pro.StatisticZocker;

import java.util.List;

public class PlayerBlockPlaceListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerBlockPlace(BlockPlaceEvent e) {
		if (e.isCancelled()) return;
		
		Block block = e.getBlock();
		Player player = e.getPlayer();

		Config config = Main.STATISTIC_CONFIG;
		StatisticZocker statisticZocker = new StatisticZocker(player.getUniqueId());

		if (config.getString("statistic.player.block.place.whitelist").equalsIgnoreCase("*")) {
			addXp(player, statisticZocker, config);
			addMoney(player, statisticZocker, config);
		} else {
			List<String> blockWhitelist = config.getStringList("statistic.player.block.place.whitelist");

			for (String name : blockWhitelist) {
				if (block.getType() == CompatibleMaterial.valueOf(name).getMaterial()) {
					statisticZocker.add(StatisticType.BLOCK_PLACE, 1);
					addXp(player, statisticZocker, config);
					addMoney(player, statisticZocker, config);
					return;
				}
			}
		}
	}

	private void addXp(Player player, StatisticZocker statisticZocker, Config config) {
		if (!config.getBool("statistic.player.block.place.exp.enabled")) return;

		statisticZocker.addXp(StatisticType.BLOCK_PLACE,
			config.getDouble("statistic.player.block.place.exp.min"),
			config.getDouble("statistic.player.block.place.exp.max"),
			"statistic.player.block.place.exp");
	}

	private void addMoney(Player player, StatisticZocker statisticZocker, Config config) {
		if (!config.getBool("statistic.player.block.place.money.enabled")) return;

		statisticZocker.addMoney(StatisticType.BLOCK_PLACE,
			config.getDouble("statistic.player.block.place.money.min"),
			config.getDouble("statistic.player.block.place.money.max"),
			"statistic.player.block.place.money");
	}
}
