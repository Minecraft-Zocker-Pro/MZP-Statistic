package minecraft.statistic.zocker.pro.inventory;

import me.clip.placeholderapi.PlaceholderAPI;
import minecraft.core.zocker.pro.Zocker;
import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import minecraft.core.zocker.pro.config.Config;
import minecraft.core.zocker.pro.inventory.InventoryZocker;
import minecraft.core.zocker.pro.inventory.builder.InventoryEntryBuilder;
import minecraft.core.zocker.pro.inventory.util.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import minecraft.statistic.zocker.pro.Main;
import minecraft.statistic.zocker.pro.StatisticType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("ALL")
public class LeaderboardOverviewInventory extends InventoryZocker {

	private Zocker zocker;
	private Config menuLeaderboardOverviewConfig;

	public LeaderboardOverviewInventory(Zocker zocker) {
		this.zocker = zocker;
		this.menuLeaderboardOverviewConfig = Main.STATISTIC_MENU_LEADERBOARD_OVERVIEW;
	}

	@Override
	public String getTitle() {
		return PlaceholderAPI.setPlaceholders(zocker.getPlayer(), menuLeaderboardOverviewConfig.getString("menu.leaderboard.overview.title"));
	}

	@Override
	public InventoryType getInventoryType() {
		return InventoryType.CHEST;
	}

	@Override
	public Integer getSize() {
		return menuLeaderboardOverviewConfig.getInt("menu.leaderboard.overview.size");
	}

	@Override
	public void onOpen(InventoryZocker inventoryZocker, InventoryOpenEvent inventoryOpenEvent) {
	}

	@Override
	public void onClose(InventoryZocker inventoryZocker, InventoryCloseEvent inventoryCloseEvent) {
	}

	@Override
	public void setupInventory() {
		this.fillBorders();

		ConfigurationSection itemSection = menuLeaderboardOverviewConfig.getSection("menu.leaderboard.overview.item");
		if (itemSection == null) return;

		itemSection.getKeys(false).forEach(itemKey -> {
			ConfigurationSection itemKeySection = itemSection.getConfigurationSection(itemKey);
			if (itemKeySection == null) return;

			String display = PlaceholderAPI.setPlaceholders(zocker.getPlayer(), Objects.requireNonNull(itemKeySection.getString("display")));
			List<String> lores = itemKeySection.getStringList("lore");
			int amount = itemKeySection.getInt("material.amount");
			if (amount == 0) {
				amount = 1;
			}

			StatisticType type = StatisticType.valueOf(itemKeySection.getString("type"));
			if (type == null) return;

			String typeName = type.getName();

			CompatibleMaterial compatibleMaterial = CompatibleMaterial.getMaterial(itemKeySection.getString("material.type"));
			if (compatibleMaterial == null) {
				System.out.println("Invalid material: " + itemKeySection.getString("material.type") + " found in leaderboard overview!");
				return;
			}

			if (!lores.isEmpty()) {
				List<String> loresPlaceholder = new ArrayList<>();
				for (String lore : lores) {
					if (lore == null) continue;
					lore = lore.replace("%leaderboard_type%", typeName);
					loresPlaceholder.add(PlaceholderAPI.setPlaceholders(zocker.getPlayer(), lore));
				}

				this.addItem(new InventoryEntryBuilder()
					.setItem(new ItemBuilder(compatibleMaterial.getMaterial())
						.setDisplayName(display.replace("%leaderboard_type%", typeName))
						.setLore(loresPlaceholder)
						.setAmount(amount)
						.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ATTRIBUTES))
					.setSlot(itemKeySection.getInt("position"))
					.onAllClicks(inventoryClickEvent -> {
						clickHandler(StatisticType.valueOf(itemKeySection.getString("type")), zocker);
					})
					.setAsync(true)
					.build());
			} else {
				this.addItem(new InventoryEntryBuilder()
					.setItem(new ItemBuilder(compatibleMaterial.getMaterial())
						.setDisplayName(display.replace("%leaderboard_type%", typeName))
						.setAmount(amount)
						.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ATTRIBUTES))
					.setSlot(itemKeySection.getInt("position"))
					.onAllClicks(inventoryClickEvent -> {
						clickHandler(StatisticType.valueOf(itemKeySection.getString("type")), zocker);
					})
					.setAsync(true)
					.build());
			}
		});
	}

	private void clickHandler(StatisticType type, Zocker zocker) {
		if (type == null) return;
		new LeaderboardTopInventory(zocker, type).open(zocker);
	}
}
