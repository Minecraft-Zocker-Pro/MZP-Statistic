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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StatisticOverviewInventory extends InventoryZocker {

	private final Zocker zocker;
	private final Config menuOverviewConfig;

	public StatisticOverviewInventory(Zocker zocker) {
		this.zocker = zocker;
		menuOverviewConfig = Main.STATISTIC_MENU_OVERVIEW;
	}

	@Override
	public String getTitle() {
		return PlaceholderAPI.setPlaceholders(zocker.getPlayer(), menuOverviewConfig.getString("menu.overview.title"));
	}

	@Override
	public InventoryType getInventoryType() {
		return InventoryType.CHEST;
	}

	@Override
	public Integer getSize() {
		return menuOverviewConfig.getInt("menu.overview.size");
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

		ConfigurationSection itemSection = menuOverviewConfig.getSection("menu.overview.item");
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

			if (!lores.isEmpty()) {
				List<String> loresPlaceholder = new ArrayList<>();
				for (String lore : lores) {
					if (lore == null) continue;
					loresPlaceholder.add(PlaceholderAPI.setPlaceholders(zocker.getPlayer(), lore));
				}

				this.addItem(new InventoryEntryBuilder()
					.setItem(new ItemBuilder(CompatibleMaterial.getMaterial(itemKeySection.getString("material.type")).getMaterial())
						.setDisplayName(display)
						.setLore(loresPlaceholder)
						.setAmount(amount)
						.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ATTRIBUTES))
					.setSlot(itemKeySection.getInt("position"))
					.setAsync(false)
					.build());
			} else {
				this.addItem(new InventoryEntryBuilder()
					.setItem(new ItemBuilder(CompatibleMaterial.getMaterial(itemKeySection.getString("material.type")).getMaterial())
						.setDisplayName(display)
						.setAmount(amount)
						.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ATTRIBUTES))
					.setSlot(itemKeySection.getInt("position"))
					.setAsync(false)
					.build());
			}
		});
	}
}
