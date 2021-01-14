package minecraft.statistic.zocker.pro.placeholder;

import me.clip.placeholderapi.expansion.Configurable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import minecraft.statistic.zocker.pro.Statistic;
import minecraft.statistic.zocker.pro.StatisticManager;
import org.bukkit.OfflinePlayer;
import minecraft.statistic.zocker.pro.StatisticZocker;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("ALL")
public class PlaceholderHandler extends PlaceholderExpansion implements Configurable {

	@Override
	public boolean canRegister() {
		return true;
	}

	@Override
	public Map<String, Object> getDefaults() {
		return null;
	}

	@Override
	public String getIdentifier() {
		return "mzpstatistic";
	}

	@Override
	public String getAuthor() {
		return "ludgart";
	}

	@Override
	public boolean persist() {
		return true;
	}

	// sync
	@Override
	public String getVersion() {
		return getClass().getPackage().getImplementationVersion();
	}

	@Override
	public String onRequest(OfflinePlayer player, String identifier) {
		if (player == null) return "";

		try {
			StatisticZocker statisticZocker = new StatisticZocker(player.getUniqueId());
			if (identifier.startsWith("placement_")) {
				String type = identifier.replace("placement_", "");
				if (type == null || type.length() == 0) return "";

				int placement = statisticZocker.getPlacement(type).get();
				if (placement == 0) return "0";

				return String.valueOf(placement);
			}

			if (identifier.equalsIgnoreCase("kd")) {
				return statisticZocker.getKD().get();
			}

			for (String type : StatisticManager.getStatisticTypes()) {
				if (identifier.equalsIgnoreCase(type.toString())) {
					Statistic statistic = statisticZocker.get(type).get();
					if (statistic == null) return "0";
					return statistic.getValue();
				}
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		return "";
	}
}
