package qwertzite.guerrillacity.core.common;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.common.util.Lazy;
import qwertzite.guerrillacity.core.datagen.GcLangLocale;
import qwertzite.guerrillacity.core.init.KeyBindingRegister;

public class GcKeyBindings {
	public static final Lazy<KeyMapping> CURSOR_UP = new KeyBindingRegister("arrow_up", GLFW.GLFW_KEY_UP)
			.setLocalisedLabelEn("GC Cursor Up").setLocalisedName(GcLangLocale.JP_JP, "GC 上矢印").build();
	public static final Lazy<KeyMapping> CURSOR_DOWN = new KeyBindingRegister("arrow_down", GLFW.GLFW_KEY_DOWN)
			.setLocalisedLabelEn("GC Cursor Down").setLocalisedName(GcLangLocale.JP_JP, "GC 下矢印").build();
	public static final Lazy<KeyMapping> CURSOR_LEFT = new KeyBindingRegister("arrow_left", GLFW.GLFW_KEY_LEFT)
			.setLocalisedLabelEn("GC Cursor Left").setLocalisedName(GcLangLocale.JP_JP, "GC 左矢印").build();
	public static final Lazy<KeyMapping> CURSOR_RIGHT = new KeyBindingRegister("arrow_right", GLFW.GLFW_KEY_RIGHT)
			.setLocalisedLabelEn("GC Cursor Right").setLocalisedName(GcLangLocale.JP_JP, "GC 右矢印").build();
	
	public static enum GcKeys {
		CURSOR_UP,
		CURSOR_DOWN,
		CURSOR_LEFT,
		CURSOR_RIGHT,
	}
}
