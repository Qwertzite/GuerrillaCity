package qwertzite.guerrillacity.core.datagen;

public enum GcLangLocale {
	EN_US("en_us"),
	EN_GB("en_gb"),
	JP_JP("jp_jp");
	
	private String locale;
	
	private GcLangLocale(String locale) {
		this.locale = locale;
	}
	
	public String getLocale() {
		return this.locale;
	}
}
