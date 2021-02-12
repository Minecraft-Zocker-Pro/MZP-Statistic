package minecraft.statistic.zocker.pro.condition.player.custom;

import minecraft.core.zocker.pro.Zocker;
import minecraft.core.zocker.pro.condition.player.PlayerCondition;
import minecraft.statistic.zocker.pro.Statistic;
import minecraft.statistic.zocker.pro.StatisticZocker;

import java.util.concurrent.ExecutionException;

public class PlayerMZPStatisticCondition extends PlayerCondition {

	private final String statisticType;

	public PlayerMZPStatisticCondition(String statisticType) {
		this.statisticType = statisticType;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String getId() {
		return "81aac9a6-7d08-47c8-b061-d45833bf86ad";
	}

	@Override
	public String getName() {
		return "MZP-Statistic-" + this.statisticType;
	}

	@Override
	public String getDisplay() {
		String display = this.statisticType.toLowerCase();
		display = display.replace("_", " ");
		display = display.toLowerCase();
		display = display.substring(0, 1).toUpperCase() + display.substring(1);
		
		return display;
	}

	@Override
	public boolean onCheck(Zocker zocker, Object value) {
		StatisticZocker statisticZocker = new StatisticZocker(zocker.getUUID());

		int valueInt = Integer.parseInt(value.toString());
		if (valueInt == 0) return true;

		try {
			Statistic statistic = statisticZocker.get(this.getStatisticType()).get();

			if (statistic == null) return false;
			if (Integer.parseInt(statistic.getValue()) >= valueInt) return true;

		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public String getCurrentValue(Zocker zocker, Object value) {
		StatisticZocker statisticZocker = new StatisticZocker(zocker.getUUID());

		int valueInt = Integer.parseInt(value.toString());
		if (valueInt == 0) return "0";

		try {
			Statistic statistic = statisticZocker.get(this.getStatisticType()).get();

			if (statistic == null) return "0";
			return String.valueOf(statistic.getValue());

		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		return "0";
	}

	public String getStatisticType() {
		return statisticType;
	}
}
