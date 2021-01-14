package minecraft.statistic.zocker.pro;

public class Statistic {

	private String uuid;
	private String type;
	private String value;

	public Statistic(String uuid, String type, String value) {
		this.uuid = uuid;
		this.type = type;
		this.value = value;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		if (value == null) return "0";
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
