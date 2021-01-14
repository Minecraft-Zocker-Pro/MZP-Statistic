package minecraft.statistic.zocker.pro.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class StatisticMoneyAddEvent extends Event implements Cancellable {
	private static final HandlerList HANDLER_LIST = new HandlerList();

	private final Player player;
	private final String statisticType;
	private final String configPath;
	private final double amount;
	private boolean cancelled;

	public StatisticMoneyAddEvent(Player player, String type, String configPath, double amount) {
		super(true);
		this.player = player;
		this.statisticType = type;
		this.configPath = configPath;
		this.amount = amount;
	}

	public Player getPlayer() {
		return player;
	}

	public String getStatisticType() {
		return statisticType;
	}

	public double getAmount() {
		return amount;
	}

	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

	public String getConfigPath() {
		return configPath;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
