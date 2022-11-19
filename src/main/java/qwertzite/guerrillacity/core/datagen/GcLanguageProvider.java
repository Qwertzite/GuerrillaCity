package qwertzite.guerrillacity.core.datagen;

import net.minecraft.Util;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import qwertzite.guerrillacity.GuerrillaCityCore;
import qwertzite.guerrillacity.core.common.GcCommon;
import qwertzite.guerrillacity.core.init.BiomeRegister;
import qwertzite.guerrillacity.core.init.BlockRegister;
import qwertzite.guerrillacity.core.init.ItemRegister;
import qwertzite.guerrillacity.core.init.KeyBindingRegister;

public class GcLanguageProvider extends LanguageProvider {
	
	private final GcLangLocale locale;
	
	public GcLanguageProvider(DataGenerator gen, GcLangLocale locale) {
		super(gen, GuerrillaCityCore.MODID, locale.getLocale());
		this.locale = locale;
	}

	@Override
	protected void addTranslations() {
		
		for (var register : BlockRegister.getEntries()) {
			if (register.getLocalName(this.locale) != null) this.addBlock(register.getRegistryObject(), register.getLocalName(this.locale));
		}
		for (var register : ItemRegister.getEntries()) {
			if (register.getLocalName(this.locale) != null) this.addItem(register.getRegistryObject(), register.getLocalName(this.locale));
		}
		
		{
			if (KeyBindingRegister.LOCAL_CATEGORY_NAME.containsKey(this.locale)) this.add(KeyBindingRegister.CATEGORY_KEY, KeyBindingRegister.LOCAL_CATEGORY_NAME.get(this.locale));
		}
		for (var register : KeyBindingRegister.ENTRY) {
			if (register.getLocalName(this.locale) != null) this.add(register.getRegistrykey(), register.getLocalName(this.locale));
		}
		
		for (var register : BiomeRegister.getEntries()) {
			if (register.getLocalName(this.locale) != null) this.add(Util.makeDescriptionId("biome", register.getRegistrykey().location()), register.getLocalName(this.locale));
		}
		
		{
			this.add("itemGroup.%s".formatted(GuerrillaCityCore.MODID), GcCommon.GC_CREATIVE_TAB_NAME);
		}
	}

	@Override
	public String getName() {
		return "Gc Languages: " + locale;
	}
}
