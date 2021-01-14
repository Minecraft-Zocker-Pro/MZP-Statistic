package minecraft.statistic.zocker.pro.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import minecraft.statistic.zocker.pro.StatisticType;

public class StatisticResetEvent extends Event {

	private static final HandlerList HANDLER_LIST = new HandlerList();

	private final Player player;
	private final String statisticType;

	public StatisticResetEvent(Player player, String type) {
		super(true);
		this.player = player;
		this.statisticType = type;
	}

	public StatisticResetEvent(Player player, String type, boolean async) {
		super(async);
		this.player = player;
		this.statisticType = type;
	}

	public Player getPlayer() {
		return player;
	}

	public String getStatisticType() {
		return statisticType;
	}

	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}
}