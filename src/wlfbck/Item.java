package wlfbck;

public class Item {
	
	public int id;
	public String name;
	public String icon;
	public int mapTier;
	public int levelRequired;
	public String baseType;
	public int stackSize;
	public int links;
	public int itemClass;
	public boolean corrupted;
	public int gemLevel;
	public int gemQuality;
	public String itemType;
	public double chaosValue;
	public double exaltedValue;
	public int count;
	
	
	public Item(String name) {
		super();
		this.name = name;
	}


	@Override
	public String toString() {
		return "Item [id=" + id + ", " + (name != null ? "name=" + name + ", " : "")
				+ (icon != null ? "icon=" + icon + ", " : "") + "mapTier=" + mapTier + ", levelRequired="
				+ levelRequired + ", " + (baseType != null ? "baseType=" + baseType + ", " : "") + "stackSize="
				+ stackSize + ", links=" + links + ", itemClass=" + itemClass + ", corrupted=" + corrupted
				+ ", gemLevel=" + gemLevel + ", gemQuality=" + gemQuality + ", "
				+ (itemType != null ? "itemType=" + itemType + ", " : "") + "chaosValue=" + chaosValue
				+ ", exaltedValue=" + exaltedValue + ", count=" + count + "]";
	}
}

