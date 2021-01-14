package minecraft.statistic.zocker.pro;

import java.util.ArrayList;
import java.util.List;

public class StatisticManager {

	private static final List<String> STATISTIC_TYPES = new ArrayList<>();

	public static void register(String statistic) {
		if (STATISTIC_TYPES.contains(statistic)) return;
		STATISTIC_TYPES.add(statistic);
	}

	public static void unregister(String statistic) {
		if (!STATISTIC_TYPES.contains(statistic)) return;
		STATISTIC_TYPES.remove(statistic);
	}

	public static String getName(String name) {
		name = name.replace("_", " ");
		name = name.toLowerCase();
		name = name.substring(0, 1).toUpperCase() + name.substring(1);

		return name;
	}

	public static String getNamePlural(String name) {
		return getName(name) + "s";
	}

	public static List<String> getStatisticTypes() {
		return STATISTIC_TYPES;
	}
}
