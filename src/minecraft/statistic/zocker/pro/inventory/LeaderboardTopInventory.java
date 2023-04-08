package minecraft.statistic.zocker.pro.inventory;

import me.clip.placeholderapi.PlaceholderAPI;
import minecraft.core.zocker.pro.OfflineZocker;
import minecraft.core.zocker.pro.Zocker;
import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import minecraft.core.zocker.pro.config.Config;
import minecraft.core.zocker.pro.inventory.InventoryZocker;
import minecraft.core.zocker.pro.inventory.builder.InventoryEntryBuilder;
import minecraft.core.zocker.pro.inventory.util.ItemBuilder;
import minecraft.statistic.zocker.pro.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("ALL")
public class LeaderboardTopInventory extends InventoryZocker {

	private Zocker zocker;
	private String type;
	private Config menuLeaderboardTopConfig;
	private int count = 0;

	public LeaderboardTopInventory(Zocker zocker, String type) {
		this.zocker = zocker;
		this.type = type;
		this.menuLeaderboardTopConfig = Main.STATISTIC_MENU_LEADERBOARD_TOP;
	}

	@Override
	public String getTitle() {
		return PlaceholderAPI.setPlaceholders(zocker.getPlayer(), this.menuLeaderboardTopConfig.getString("menu.leaderboard.top.title"));
	}

	@Override
	public InventoryType getInventoryType() {
		return InventoryType.CHEST;
	}

	@Override
	public Integer getSize() {
		return this.menuLeaderboardTopConfig.getInt("menu.leaderboard.top.size");
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

		// Back
		this.addItem(new InventoryEntryBuilder()
			.setSlot(menuLeaderboardTopConfig.getInt("menu.leaderboard.top.back.position"))
			.onAllClicks(e -> new LeaderboardOverviewInventory(zocker).open(zocker))
			.setItem(this.getPreviousArrow().getItem()).build());

		ConfigurationSection itemSection = menuLeaderboardTopConfig.getSection("menu.leaderboard.top.item");
		if (itemSection == null) return;

		try {
			String typeName = StatisticManager.getName(this.type);
			String typeNamePlural = StatisticManager.getNamePlural(this.type);

			StatisticZocker statisticZocker = new StatisticZocker(zocker.getUUID());
			Statistic statistic = statisticZocker.get(this.type.toString()).get();

			if (statistic == null) return;

			zocker.getPlacement(
				Main.STATISTIC_DATABASE_TABLE,
				"statistic_value",
				"player_uuid",
				"statistic_value",
				"DESC",
				"statistic_type",
				this.type.toString(),
				10)
				.get().forEach((offlineZockerUUID, integer) -> {
				count++;

				OfflineZocker offlineZocker = new OfflineZocker(UUID.fromString(offlineZockerUUID));
				if (offlineZocker == null) return;

				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(offlineZockerUUID));
				if (offlinePlayer == null) return;


				itemSection.getKeys(false).forEach(itemKey -> {
					ConfigurationSection itemKeySection = itemSection.getConfigurationSection(itemKey);
					if (itemKeySection == null) return;
					if (itemKeySection.getInt("placement") != count) return;

					CompatibleMaterial compatibleMaterial = CompatibleMaterial.getMaterial(itemKeySection.getString("material.type"));
					if (compatibleMaterial == null) {
						System.out.println("Invalid material: " + itemKeySection.getString("material.type") + " found in leaderboard top!");
						return;
					}

					String display = PlaceholderAPI.setPlaceholders(zocker.getPlayer(), Objects.requireNonNull(itemKeySection.getString("display")));
					display = display.replace("%placement%", String.valueOf(count));
					display = display.replace("%placement_name%", offlineZocker.getName());
					display = display.replace("%placement_value%", String.valueOf(integer));
					display = display.replace("%leaderboard_type%", typeName);
					display = display.replace("%leaderboard_types%", typeNamePlural);

					List<String> lores = itemKeySection.getStringList("lore");
					int amount = itemKeySection.getInt("material.amount");
					if (amount == 0) {
						amount = 1;
					}

					if (!lores.isEmpty()) {
						List<String> loresPlaceholder = new ArrayList<>();
						for (String lore : lores) {
							if (lore == null) continue;
							lore = lore.replace("%placement%", String.valueOf(count));
							lore = lore.replace("%placement_name%", offlineZocker.getName());
							lore = lore.replace("%placement_value%", String.valueOf(integer));
							lore = lore.replace("%leaderboard_type%", typeName);
							lore = lore.replace("%leaderboard_types%", typeNamePlural);

							loresPlaceholder.add(PlaceholderAPI.setPlaceholders(zocker.getPlayer(), lore));
						}

						this.addItem(new InventoryEntryBuilder()
							.setItem(new ItemBuilder(CompatibleMaterial.getMaterial(itemKeySection.getString("material.type")).getMaterial())
								.setDisplayName(display)
								.setLore(loresPlaceholder)
								.setAmount(amount)
								.setOwningPlayer(offlinePlayer)
								.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ATTRIBUTES))
							.setSlot(itemKeySection.getInt("position"))
							.setAsync(true)
							.build());
					} else {
						this.addItem(new InventoryEntryBuilder()
							.setItem(new ItemBuilder(CompatibleMaterial.getMaterial(itemKeySection.getString("material.type")).getMaterial())
								.setDisplayName(display)
								.setAmount(1)
								.setOwningPlayer(offlinePlayer)
								.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ATTRIBUTES))
							.setSlot(itemKeySection.getInt("position"))
							.setAsync(true)
							.build());
					}
				});
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
