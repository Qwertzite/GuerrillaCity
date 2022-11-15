package qwertzite.guerrillacity.core.init;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.common.util.Lazy;
import qwertzite.guerrillacity.core.datagen.GcLangLocale;

public class KeyBindingRegister {
	public static final String CATEGORY_KEY = "key.guerrillacity2.category";
	public static final Map<GcLangLocale, String> LOCAL_CATEGORY_NAME = Map.of(
			GcLangLocale.EN_GB, "GC Key Bindings",
			GcLangLocale.EN_US, "GC Key Bindings",
			GcLangLocale.JP_JP, "GC キーバインド");
	public static final Set<KeyBindingRegister> ENTRY = new HashSet<>();
	
	private final String name;
	private final int key;
	private final Map<GcLangLocale, String> localName = new HashMap<>();
	private Lazy<KeyMapping> mapping;
	
	public KeyBindingRegister(String name, int key) {
		this.name = "key.guerrillacity2." + name;
		this.key = key;
		ENTRY.add(this);
	}
	
	public KeyBindingRegister setLocalisedLabelEn(String local) {
		this.localName.put(GcLangLocale.EN_GB, local);
		this.localName.put(GcLangLocale.EN_US, local);
		return this;
	}
	
	public KeyBindingRegister setLocalisedName(GcLangLocale locale, String name) {
		this.localName.put(locale, name);
		return this;
	}
	
	public Lazy<KeyMapping> build() {
		return this.mapping = Lazy.of(() -> new KeyMapping(this.name, this.key, CATEGORY_KEY));
	}
	
	public KeyMapping getMapping() { return this.mapping.get(); }
	public String getRegistrykey() { return this.name; }
	
	public String getLocalName(GcLangLocale locale) {
		return this.localName.get(locale);
	}

}
