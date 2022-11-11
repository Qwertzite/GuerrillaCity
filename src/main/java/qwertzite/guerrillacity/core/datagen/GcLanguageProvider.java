package qwertzite.guerrillacity.core.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import qwertzite.guerrillacity.GuerrillaCityCore;
import qwertzite.guerrillacity.core.init.BlockRegister;
import qwertzite.guerrillacity.core.init.ItemRegister;

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
		
	}

	@Override
	public String getName() {
		return "Gc Languages: " + locale;
	}
}
