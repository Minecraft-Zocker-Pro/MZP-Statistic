package minecraft.statistic.zocker.pro;

import minecraft.core.zocker.pro.CorePlugin;
import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import minecraft.core.zocker.pro.config.Config;
import minecraft.core.zocker.pro.storage.StorageManager;
import minecraft.statistic.zocker.pro.command.LeaderboardCommand;
import minecraft.statistic.zocker.pro.command.StatisticCommand;
import minecraft.statistic.zocker.pro.listener.*;
import minecraft.statistic.zocker.pro.placeholder.PlaceholderHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Main extends CorePlugin {

	public static Config STATISTIC_CONFIG, STATISTIC_MESSAGE, STATISTIC_MENU_OVERVIEW, STATISTIC_MENU_LEADERBOARD_OVERVIEW, STATISTIC_MENU_LEADERBOARD_TOP;
	public static String STATISTIC_DATABASE_TABLE;
	private static boolean hasVaultSupport;
	private static CorePlugin PLUGIN;
	private static Economy ECONOMY;

	@Override
	public void onEnable() {
		super.onEnable();
		super.setDisplayItem(CompatibleMaterial.BOOK.getMaterial());
		super.setPluginName("MZP-Statistic");

		PLUGIN = this;

		if (!Bukkit.getPluginManager().isPluginEnabled("MZP-Core")) {
			System.out.println("Disabled due to no MZP-Core dependency found!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		if (setupEconomy()) {
			System.out.println("Hooked into Vault!");
		}

		this.buildConfig();
		this.verifyDatabase();
		this.registerCommand();
		this.registerListener();

		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			setupPlaceholder();
		}
	}

	@Override
	public void registerCommand() {
		getCommand("statistic").setExecutor(new StatisticCommand());
		getCommand("leaderboard").setExecutor(new LeaderboardCommand());
	}

	@Override
	public void registerListener() {
		PluginManager pluginManager = Bukkit.getPluginManager();

		if (STATISTIC_CONFIG.getBool("statistic.player.block.place.exp.enabled") || STATISTIC_CONFIG.getBool("statistic.player.block.place.money.enabled")) {
			pluginManager.registerEvents(new PlayerBlockPlaceListener(), this);
		}

		if (STATISTIC_CONFIG.getBool("statistic.player.block.break.exp.enabled") || STATISTIC_CONFIG.getBool("statistic.player.block.break.money.enabled")) {
			pluginManager.registerEvents(new PlayerBlockBreakListener(), this);
		}

		if (STATISTIC_CONFIG.getBool("statistic.player.item.craft.exp.enabled") || STATISTIC_CONFIG.getBool("statistic.player.item.craft.money.enabled")) {
			pluginManager.registerEvents(new PlayerItemCraftListener(), this);
		}

		if (STATISTIC_CONFIG.getBool("statistic.player.item.break.exp.enabled") || STATISTIC_CONFIG.getBool("statistic.player.item.break.money.enabled")) {
			pluginManager.registerEvents(new PlayerItemBreakListener(), this);
		}

		if (STATISTIC_CONFIG.getBool("statistic.player.item.enchant.exp.enabled") || STATISTIC_CONFIG.getBool("statistic.player.item.enchant.money.enabled")) {
			pluginManager.registerEvents(new PlayerItemEnchantListener(), this);
		}

		if (STATISTIC_CONFIG.getBool("statistic.player.item.consume.exp.enabled") || STATISTIC_CONFIG.getBool("statistic.player.item.consume.money.enabled")) {
			pluginManager.registerEvents(new PlayerItemConsumeListener(), this);
		}

		if (STATISTIC_CONFIG.getBool("statistic.player.fish.exp.enabled") || STATISTIC_CONFIG.getBool("statistic.player.fish.money.enabled")) {
			pluginManager.registerEvents(new PlayerFishListener(), this);
		}

		if (STATISTIC_CONFIG.getBool("statistic.player.shear.exp.enabled") || STATISTIC_CONFIG.getBool("statistic.player.shear.money.enabled")) {
			pluginManager.registerEvents(new PlayerShearListener(), this);
		}

		if (STATISTIC_CONFIG.getBool("statistic.player.milk.exp.enabled") || STATISTIC_CONFIG.getBool("statistic.player.milk.money.enabled")) {
			pluginManager.registerEvents(new PlayerMilkListener(), this);
		}

		if (STATISTIC_CONFIG.getBool("statistic.player.tame.exp.enabled") || STATISTIC_CONFIG.getBool("statistic.player.tame.money.enabled")) {
			pluginManager.registerEvents(new PlayerTameListener(), this);
		}

		if (STATISTIC_CONFIG.getBool("statistic.player.throw.exp.enabled") || STATISTIC_CONFIG.getBool("statistic.player.throw.money.enabled")) {
			pluginManager.registerEvents(new PlayerThrowListener(), this);
		}

		if (STATISTIC_CONFIG.getBool("statistic.player.death.exp.enabled") || STATISTIC_CONFIG.getBool("statistic.player.death.money.enabled")) {
			pluginManager.registerEvents(new PlayerDeathListener(), this);
		}

		if (STATISTIC_CONFIG.getBool("statistic.player.kill.exp.enabled") || STATISTIC_CONFIG.getBool("statistic.player.kill.money.enabled")) {
			pluginManager.registerEvents(new EntityKillListener(), this);
		}

		pluginManager.registerEvents(new PlayerVoidFallListener(), this);
		pluginManager.registerEvents(new PlayerQuitListener(), this);
		pluginManager.registerEvents(new ZockerDataInitializeListener(), this);
	}

	@Override
	public void buildConfig() {
		// Config
		STATISTIC_CONFIG = new Config("statistic.yml", this.getPluginName());

		// Global or Per Server
		STATISTIC_CONFIG.set("statistic.global", true, "0.0.1");

		STATISTIC_CONFIG.setVersion("0.0.1", true);

		// Void
		STATISTIC_CONFIG.set("statistic.void.reset.inventory", true, "0.0.1");
		STATISTIC_CONFIG.set("statistic.void.reset.level", true, "0.0.1");
		STATISTIC_CONFIG.set("statistic.void.reset.xp", true, "0.0.1");
		STATISTIC_CONFIG.set("statistic.void.reset.potion", true, "0.0.1");
		STATISTIC_CONFIG.set("statistic.void.reset.world.blacklist", new String[]{"my_world", "my_world_nether"}, "0.0.1");

		// region Block place
		STATISTIC_CONFIG.set("statistic.player.block.place.whitelist", "*", "0.0.1");

		STATISTIC_CONFIG.set("statistic.player.block.place.exp.enabled", true, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.block.place.exp.min", 0, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.block.place.exp.max", 1, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.block.place.exp.actionbar.enabled", false, "0.0.1");

		STATISTIC_CONFIG.set("statistic.player.block.place.money.enabled", false, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.block.place.money.min", 0, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.block.place.money.max", 0.5, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.block.place.money.actionbar.enabled", false, "0.0.1");
		// endregion

		// region Block break
		STATISTIC_CONFIG.set("statistic.player.block.break.whitelist", "*", "0.0.1"); // TODO prevent anti spam

		STATISTIC_CONFIG.set("statistic.player.block.break.exp.enabled", true, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.block.break.exp.min", 0, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.block.break.exp.max", 1, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.block.break.exp.actionbar.enabled", false, "0.0.1");

		STATISTIC_CONFIG.set("statistic.player.block.break.money.enabled", false, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.block.break.money.min", 0, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.block.break.money.max", 0.55, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.block.break.money.actionbar.enabled", false, "0.0.1");
		// endregion

		// region Item craft
		STATISTIC_CONFIG.set("statistic.player.item.craft.whitelist", "*", "0.0.1");

		STATISTIC_CONFIG.set("statistic.player.item.craft.exp.enabled", true, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.item.craft.exp.min", 0, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.item.craft.exp.max", 1, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.item.craft.exp.actionbar.enabled", false, "0.0.1");

		STATISTIC_CONFIG.set("statistic.player.item.craft.money.enabled", true, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.item.craft.money.min", 0, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.item.craft.money.max", 1, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.item.craft.money.actionbar.enabled", false, "0.0.1");
		// endregion

		// region Item break
		STATISTIC_CONFIG.set("statistic.player.item.break.whitelist", "*", "0.0.1");

		STATISTIC_CONFIG.set("statistic.player.item.break.exp.enabled", true, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.item.break.exp.min", 0, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.item.break.exp.max", 0.5, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.item.break.exp.actionbar.enabled", false, "0.0.1");

		STATISTIC_CONFIG.set("statistic.player.item.break.money.enabled", true, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.item.break.money.min", 0.5, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.item.break.money.max", 1, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.item.break.money.actionbar.enabled", false, "0.0.1");
		// endregion

		// region Item enchant
		STATISTIC_CONFIG.set("statistic.player.item.enchant.whitelist", "*", "0.0.1");

		STATISTIC_CONFIG.set("statistic.player.item.enchant.exp.enabled", true, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.item.enchant.exp.min", 15, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.item.enchant.exp.max", 50, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.item.enchant.exp.actionbar.enabled", false, "0.0.1");

		STATISTIC_CONFIG.set("statistic.player.item.enchant.money.enabled", true, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.item.enchant.money.min", 5, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.item.enchant.money.max", 15, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.item.enchant.money.actionbar.enabled", false, "0.0.1");
		// endregion

		// region Item consume
		STATISTIC_CONFIG.set("statistic.player.item.consume.whitelist", "*", "0.0.1");

		STATISTIC_CONFIG.set("statistic.player.item.consume.exp.enabled", true, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.item.consume.exp.min", 0, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.item.consume.exp.max", 1, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.item.consume.exp.actionbar.enabled", false, "0.0.1");

		STATISTIC_CONFIG.set("statistic.player.item.consume.money.enabled", true, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.item.consume.money.min", 0.5, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.item.consume.money.max", 1, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.item.consume.money.actionbar.enabled", false, "0.0.1");
		// endregion

		// region Fish
		STATISTIC_CONFIG.set("statistic.player.fish.exp.enabled", true, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.fish.exp.min", 8, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.fish.exp.max", 20, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.fish.exp.actionbar.enabled", false, "0.0.1");

		STATISTIC_CONFIG.set("statistic.player.fish.money.enabled", true, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.fish.money.min", 5, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.fish.money.max", 15, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.fish.money.actionbar.enabled", false, "0.0.1");
		// endregion

		// region Shear
		STATISTIC_CONFIG.set("statistic.player.shear.exp.enabled", true, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.shear.exp.min", 0, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.shear.exp.max", 5, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.shear.exp.actionbar.enabled", false, "0.0.1");

		STATISTIC_CONFIG.set("statistic.player.shear.money.enabled", true, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.shear.money.min", 0.5, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.shear.money.max", 1, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.shear.money.actionbar.enabled", false, "0.0.1");
		// endregion

		// region Milk
		STATISTIC_CONFIG.set("statistic.player.milk.exp.enabled", true, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.milk.exp.min", 0, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.milk.exp.max", 5, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.milk.exp.actionbar.enabled", false, "0.0.1");

		STATISTIC_CONFIG.set("statistic.player.milk.money.enabled", true, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.milk.money.min", 0.5, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.milk.money.max", 1, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.milk.money.actionbar.enabled", false, "0.0.1");
		// endregion

		// region Tame
		STATISTIC_CONFIG.set("statistic.player.tame.exp.enabled", true, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.tame.exp.min", 5, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.tame.exp.max", 10, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.tame.exp.actionbar.enabled", false, "0.0.1");

		STATISTIC_CONFIG.set("statistic.player.tame.money.enabled", true, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.tame.money.min", 1, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.tame.money.max", 5, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.tame.money.actionbar.enabled", false, "0.0.1");
		// endregion

		// region Throw
		STATISTIC_CONFIG.set("statistic.player.throw.whitelist", "*", "0.0.1");

		STATISTIC_CONFIG.set("statistic.player.throw.exp.enabled", false, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.throw.exp.min", 0, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.throw.exp.max", 0, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.throw.exp.actionbar.enabled", false, "0.0.1");

		STATISTIC_CONFIG.set("statistic.player.throw.money.enabled", false, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.throw.money.min", 0, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.throw.money.max", 0, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.throw.money.actionbar.enabled", false, "0.0.1");
		// endregion

		// region Death
		STATISTIC_CONFIG.set("statistic.player.death.message.enabled", false, "0.0.1");

		STATISTIC_CONFIG.set("statistic.player.death.exp.enabled", false, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.death.exp.min", 0, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.death.exp.max", 0, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.death.exp.actionbar.enabled", false, "0.0.1");

		STATISTIC_CONFIG.set("statistic.player.death.money.enabled", false, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.death.money.min", 0, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.death.money.max", 0, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.death.money.actionbar.enabled", false, "0.0.1");
		// endregion

		// region Kill
		STATISTIC_CONFIG.set("statistic.player.kill.exp.enabled", true, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.kill.exp.min", 5, "0.0.1"); // TODO anti spam
		STATISTIC_CONFIG.set("statistic.player.kill.exp.max", 10, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.kill.exp.actionbar.enabled", false, "0.0.1");

		STATISTIC_CONFIG.set("statistic.player.kill.money.enabled", true, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.kill.money.min", 5, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.kill.money.max", 10, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.kill.money.actionbar.enabled", false, "0.0.1");
		// endregion

		// region Kill hostile
		STATISTIC_CONFIG.set("statistic.player.kill.hostile.whitelist", new String[]{
			"BLAZE", "CAVE_SPIDER", "DROWNED", "CREEPER", "ELDER_GUARDIAN", "ENDER_DRAGON", "ENDERMAN", "ENDERMITE", "EVOKER", "GHAST", "GIANT", "GUARDIAN", "HUSK", "ILLUSIONER", "MAGMA_CUBE", "PHANTOM", "PIG_ZOMBIE", "ZOMBIFIED_PIGLIN", "PILLAGER", "RAVAGER", "SHULKER", "SILVERFISH", "SKELETON",
			"SKELETON_HORSE", "SLIME", "SPIDER", "STRAY", "VEX", "VINDICATOR", "WITCH", "WITHER", "WITHER_SKELETON", "ZOMBIE", "ZOMBIE_HORSE", "ZOMBIE_VILLAGER", "HOGLIN", "PIGLIN", "ZOGLIN"
		}, "0.0.1");

		STATISTIC_CONFIG.set("statistic.player.kill.hostile.exp.enabled", true, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.kill.hostile.exp.min", 2.5, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.kill.hostile.exp.max", 5, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.kill.hostile.exp.actionbar.enabled", false, "0.0.1");

		STATISTIC_CONFIG.set("statistic.player.kill.hostile.money.enabled", true, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.kill.hostile.money.min", 1, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.kill.hostile.money.max", 3, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.kill.hostile.money.actionbar.enabled", false, "0.0.1");
		// endregion

		// region Kill friendly
		STATISTIC_CONFIG.set("statistic.player.kill.friendly.whitelist", new String[]{
			"BAT", "CAT", "CHICKEN", "COW", "COD", "DOLPHIN", "DONKEY", "FOX", "HORSE", "IRON_GOLEM", "LLAMA", "MULE", "MUSHROOM_COW", "OCELOT", "PANDA", "PARROT", "PIG", "POLAR_BEAR", "PUFFERFISH", "SALMON", "SHEEP", "SNOWMAN", "SQUID", "TRADER_LLAMA", "TROPICAL_FISH", "TURTLE", "VILLAGER",
			"WANDERING_TRADER", "WOLF", "RABBIT", "STRIDER"
		}, "0.0.1");

		STATISTIC_CONFIG.set("statistic.player.kill.friendly.exp.enabled", true, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.kill.friendly.exp.min", 1, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.kill.friendly.exp.max", 3, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.kill.friendly.exp.actionbar.enabled", false, "0.0.1");

		STATISTIC_CONFIG.set("statistic.player.kill.friendly.money.enabled", true, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.kill.friendly.money.min", 1, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.kill.friendly.money.max", 3, "0.0.1");
		STATISTIC_CONFIG.set("statistic.player.kill.friendly.money.actionbar.enabled", false, "0.0.1");
		// endregion

		STATISTIC_CONFIG.setVersion("0.0.1", true);


		// Menu Statistic Overview
		STATISTIC_MENU_OVERVIEW = new Config("menu.overview.yml", this.getPluginName());

		STATISTIC_MENU_OVERVIEW.set("menu.overview.title", "Statistic", "0.0.1");
		STATISTIC_MENU_OVERVIEW.set("menu.overview.size", 45, "0.0.1");

		STATISTIC_MENU_OVERVIEW.set("menu.overview.item.0.display", "&6&lKills", "0.0.1");
		STATISTIC_MENU_OVERVIEW.set("menu.overview.item.0.position", 20, "0.0.1");
		STATISTIC_MENU_OVERVIEW.set("menu.overview.item.0.lore", new String[]{"", "&3Current &6%mzpstatistic_kill%", "&3Total &6%mzpstatistic_kill_total%", "", "&3K/D &6%mzpstatistic_kd%", "", "&3Placement &6%mzpstatistic_placement_kill%"}, "0.0.1");
		STATISTIC_MENU_OVERVIEW.set("menu.overview.item.0.material.type", CompatibleMaterial.NETHERITE_SWORD.getMaterial().toString(), "0.0.1");
		STATISTIC_MENU_OVERVIEW.set("menu.overview.item.0.material.amount", 1, "0.0.1");

		STATISTIC_MENU_OVERVIEW.setVersion("0.0.1", true);

		// Menu Leaderboard Overview
		STATISTIC_MENU_LEADERBOARD_OVERVIEW = new Config("menu.leaderboard.overview.yml", this.getPluginName());

		STATISTIC_MENU_LEADERBOARD_OVERVIEW.set("menu.leaderboard.overview.title", "Leaderboard", "0.0.1");
		STATISTIC_MENU_LEADERBOARD_OVERVIEW.set("menu.leaderboard.overview.size", 45, "0.0.1");

		STATISTIC_MENU_LEADERBOARD_OVERVIEW.set("menu.leaderboard.overview.item.0.display", "&6&lTop Kills", "0.0.1");
		STATISTIC_MENU_LEADERBOARD_OVERVIEW.set("menu.leaderboard.overview.item.0.position", 10, "0.0.1");
		STATISTIC_MENU_LEADERBOARD_OVERVIEW.set("menu.leaderboard.overview.item.0.lore", new String[]{"", "&3Open the top &6%leaderboard_type% Leaderboard"}, "0.0.1");
		STATISTIC_MENU_LEADERBOARD_OVERVIEW.set("menu.leaderboard.overview.item.0.type", "KILL", "0.0.1");
		STATISTIC_MENU_LEADERBOARD_OVERVIEW.set("menu.leaderboard.overview.item.0.material.type", CompatibleMaterial.NETHERITE_SWORD.getMaterial().toString(), "0.0.1");
		STATISTIC_MENU_LEADERBOARD_OVERVIEW.set("menu.leaderboard.overview.item.0.material.amount", 1, "0.0.1");

		STATISTIC_MENU_LEADERBOARD_OVERVIEW.setVersion("0.0.1", true);

		// Menu Leaderboard Top
		STATISTIC_MENU_LEADERBOARD_TOP = new Config("menu.leaderboard.top.yml", this.getPluginName());
		STATISTIC_MENU_LEADERBOARD_TOP.set("menu.leaderboard.top.title", "Leaderboard Top", "0.0.1");
		STATISTIC_MENU_LEADERBOARD_TOP.set("menu.leaderboard.top.size", 54, "0.0.1");
		STATISTIC_MENU_LEADERBOARD_TOP.set("menu.leaderboard.top.back.position", 48, "0.0.1");

		STATISTIC_MENU_LEADERBOARD_TOP.set("menu.leaderboard.top.item.0.display", "&f#%placement% &6%placement_name% &3- %placement_value% %leaderboard_types%", "0.0.1");
		STATISTIC_MENU_LEADERBOARD_TOP.set("menu.leaderboard.top.item.0.position", 13, "0.0.1");
		STATISTIC_MENU_LEADERBOARD_TOP.set("menu.leaderboard.top.item.0.lore", new String[]{}, "0.0.1");
		STATISTIC_MENU_LEADERBOARD_TOP.set("menu.leaderboard.top.item.0.placement", 1, "0.0.1");
		STATISTIC_MENU_LEADERBOARD_TOP.set("menu.leaderboard.top.item.0.material.type", CompatibleMaterial.PLAYER_HEAD.getMaterial().toString(), "0.0.1");
		STATISTIC_MENU_LEADERBOARD_TOP.set("menu.leaderboard.top.item.0.material.amount", 1, "0.0.1");

		STATISTIC_MENU_LEADERBOARD_TOP.setVersion("0.0.1", true);

		STATISTIC_MESSAGE = new Config("message.yml", this.getPluginName());

		STATISTIC_MESSAGE.set("statistic.prefix", "&6&l[Statistic]&3 ", "0.0.1");
		STATISTIC_MESSAGE.set("statistic.player.offline", "Player &6%player% &3is not online.", "0.0.1");

		// Command
		STATISTIC_MESSAGE.set("statistic.command.statistic.add", "&3Added &6%amount% %type% &3to &6%target%.");
		STATISTIC_MESSAGE.set("statistic.command.statistic.remove", "&3Removed &6%amount% %type% &3from &6%target%.");
		STATISTIC_MESSAGE.set("statistic.command.statistic.set", "&3Player &6%target% &3set &6%amount% %type%&3.");

		// Reward
		STATISTIC_MESSAGE.set("statistic.reward.money.actionbar.add", "&3+$%money%");
		STATISTIC_MESSAGE.set("statistic.reward.money.actionbar.remove", "&3-$%money%");
		STATISTIC_MESSAGE.set("statistic.reward.exp.actionbar.add", "&3+%exp% EXP");
		STATISTIC_MESSAGE.set("statistic.reward.exp.actionbar.remove", "&3-%exp% EXP");

		STATISTIC_MESSAGE.setVersion("0.0.1", true);
	}

	@Override
	public void reload() {
		STATISTIC_CONFIG.reload();
		STATISTIC_MESSAGE.reload();
		STATISTIC_MENU_OVERVIEW.reload();

		// Leaderboard
		STATISTIC_MENU_LEADERBOARD_OVERVIEW.reload();
		STATISTIC_MENU_LEADERBOARD_TOP.reload();
	}

	private void verifyDatabase() {
		if (STATISTIC_CONFIG.getBool("statistic.global")) {
			STATISTIC_DATABASE_TABLE = "player_statistic";
		} else {
			STATISTIC_DATABASE_TABLE = "player_statistic_" + StorageManager.getServerName();
		}

		String statisticTable = "CREATE TABLE IF NOT EXISTS `" + STATISTIC_DATABASE_TABLE + "`( `player_uuid` VARCHAR(36) NOT NULL, `kill` INT DEFAULT 0, `kill_total` INT DEFAULT 0, `death` INT DEFAULT 0, `death_total` INT DEFAULT 0, `streak` INT DEFAULT 0, `streak_total` INT DEFAULT 0, `streak_top` INT DEFAULT 0, `hostile_kill` INT DEFAULT 0, `hostile_kill_total` INT DEFAULT 0, " +
			"`friendly_kill` INT DEFAULT 0, " +
			"`friendly_kill_total` INT DEFAULT 0, `milk` INT DEFAULT 0, `milk_total` INT DEFAULT 0, `tame` INT DEFAULT 0, `tame_total` INT DEFAULT 0, `shear` INT DEFAULT 0, `shear_total` INT DEFAULT 0, `throw` INT DEFAULT 0, `throw_total` INT DEFAULT 0, `fish` INT DEFAULT 0, `fish_total` INT DEFAULT 0, `block_break` INT DEFAULT 0, `block_break_total` INT DEFAULT 0, `block_place` INT DEFAULT 0, `block_place_total` INT DEFAULT 0, `item_break` " +
			"INT DEFAULT 0, `item_break_total` INT DEFAULT 0, `item_consume` INT DEFAULT 0, `item_consume_total` INT DEFAULT 0, `item_craft` INT DEFAULT 0, `item_craft_total` INT DEFAULT 0, `item_enchant` INT DEFAULT 0, `item_enchant_total` INT DEFAULT 0, `void_fall` INT DEFAULT 0, " +
			"`void_fall_total` INT DEFAULT 0, FOREIGN KEY (player_uuid) REFERENCES player (uuid) ON DELETE CASCADE);";

		if (StorageManager.isMySQL()) {
			assert StorageManager.getMySQLDatabase() != null : "Create table failed.";
			StorageManager.getMySQLDatabase().createTable(statisticTable);
			return;
		}

		assert StorageManager.getSQLiteDatabase() != null : "Create table failed.";
		StorageManager.getSQLiteDatabase().createTable(statisticTable);
	}

	private void setupPlaceholder() {
		new PlaceholderHandler().register();
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			hasVaultSupport = false;
			return false;
		}

		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			hasVaultSupport = false;
			return false;
		}

		ECONOMY = rsp.getProvider();
		hasVaultSupport = true;
		return true;
	}

	public static CorePlugin getPlugin() {
		return PLUGIN;
	}

	public static Economy getEconomy() {
		return ECONOMY;
	}

	public static boolean hasVaultSupport() {
		return hasVaultSupport;
	}
}
