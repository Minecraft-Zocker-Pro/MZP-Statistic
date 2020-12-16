package minecraft.statistic.zocker.pro;

public enum StatisticType {

	KILL,
	KILL_TOTAL,

	DEATH,
	DEATH_TOTAL,

	STREAK,
	STREAK_TOTAL,
	STREAK_TOP,

	HOSTILE_KILL,
	HOSTILE_KILL_TOTAL,

	FRIENDLY_KILL,
	FRIENDLY_KILL_TOTAL,

	MILK,
	MILK_TOTAL,

	TAME,
	TAME_TOTAL,

	SHEAR,
	SHEAR_TOTAL,

	THROW,
	THROW_TOTAL,

	FISH,
	FISH_TOTAL,

	BLOCK_BREAK,
	BLOCK_BREAK_TOTAL,

	BLOCK_PLACE,
	BLOCK_PLACE_TOTAL,

	ITEM_BREAK,
	ITEM_BREAK_TOTAL,

	ITEM_CONSUME,
	ITEM_CONSUME_TOTAL,

	ITEM_CRAFT,
	ITEM_CRAFT_TOTAL,

	ITEM_ENCHANT,
	ITEM_ENCHANT_TOTAL,

	VOID_FALL,
	VOID_FALL_TOTAL;


	public String getName() {
		String name = this.name();
		name = name.replace("_", " ");
		name = name.toLowerCase();
		name = name.substring(0, 1).toUpperCase() + name.substring(1);

		return name;
	}

	public String getNamePlural() {
		return this.getName() + "s";
	}
}
