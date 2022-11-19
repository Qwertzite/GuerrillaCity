package qwertzite.guerrillacity.core.common;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import qwertzite.guerrillacity.core.datagen.GcLangLocale;
import qwertzite.guerrillacity.core.init.KeyBindingRegister;

//public class GcKeyBindings {
//	public static final Lazy<KeyMapping> CURSOR_UP = new KeyBindingRegister("arrow_up", GLFW.GLFW_KEY_UP)
//			.setLocalisedLabelEn("GC Cursor Up").setLocalisedName(GcLangLocale.JP_JP, "GC 上矢印").build();
//	public static final Lazy<KeyMapping> CURSOR_DOWN = new KeyBindingRegister("arrow_down", GLFW.GLFW_KEY_DOWN)
//			.setLocalisedLabelEn("GC Cursor Down").setLocalisedName(GcLangLocale.JP_JP, "GC 下矢印").build();
//	public static final Lazy<KeyMapping> CURSOR_LEFT = new KeyBindingRegister("arrow_left", GLFW.GLFW_KEY_LEFT)
//			.setLocalisedLabelEn("GC Cursor Left").setLocalisedName(GcLangLocale.JP_JP, "GC 左矢印").build();
//	public static final Lazy<KeyMapping> CURSOR_RIGHT = new KeyBindingRegister("arrow_right", GLFW.GLFW_KEY_RIGHT)
//			.setLocalisedLabelEn("GC Cursor Right").setLocalisedName(GcLangLocale.JP_JP, "GC 右矢印").build();
//	
//	public static final Lazy<KeyMapping> BTN_VIEW = new KeyBindingRegister("button_view", GLFW.GLFW_KEY_V)
//			.setLocalisedLabelEn("GC View Button").setLocalisedName(GcLangLocale.JP_JP, "GC ビューボタン").build();
//	
//	
//	public static void addClickEventListener(Key key) {
//		
//		
//	}
//	
//	public void init() {
//		
//	}
//	
//	

	
public enum GcKeyBindings {
	CURSOR_UP(new KeyBindingRegister("arrow_up", GLFW.GLFW_KEY_UP)
			.setLocalisedLabelEn("GC Cursor Up").setLocalisedName(GcLangLocale.JP_JP, "GC 上矢印").build()),
	CURSOR_DOWN(new KeyBindingRegister("arrow_down", GLFW.GLFW_KEY_DOWN)
			.setLocalisedLabelEn("GC Cursor Down").setLocalisedName(GcLangLocale.JP_JP, "GC 下矢印").build()),
	CURSOR_LEFT(new KeyBindingRegister("arrow_left", GLFW.GLFW_KEY_LEFT)
			.setLocalisedLabelEn("GC Cursor Left").setLocalisedName(GcLangLocale.JP_JP, "GC 左矢印").build()),
	CURSOR_RIGHT(new KeyBindingRegister("arrow_right", GLFW.GLFW_KEY_RIGHT)
			.setLocalisedLabelEn("GC Cursor Right").setLocalisedName(GcLangLocale.JP_JP, "GC 右矢印").build()),
	
	BTN_VIEW(new KeyBindingRegister("button_view", GLFW.GLFW_KEY_V)
			.setLocalisedLabelEn("GC View Button").setLocalisedName(GcLangLocale.JP_JP, "GC ビューボタン").build());
	
	private final Lazy<KeyMapping> mapping;
	
	private GcKeyBindings(Lazy<KeyMapping> mapping) {
		this.mapping = mapping;
	}
	
	public KeyMapping get() {
		return this.mapping.get();
	}
	
	public static void init() {
		MinecraftForge.EVENT_BUS.addListener(GcKeyBindings::onClientTick);
	}
	
	private static Set<Predicate<GcKeyBindings>> view_listeners = new HashSet<>();
	
	/**
	 * Called twice per tick, at tick start and end.
	 * @param event
	 */
	public static void onClientTick(ClientTickEvent event) {
		while (BTN_VIEW.get().consumeClick()) {
			for (var listener : view_listeners) {
				if (listener.test(BTN_VIEW)) break;
			}
		}
	}
	
	/**
	 * Add new listener for the key.
	 * Listeners usually returns true when success.
	 * @param key
	 * @param listener return true to prevent the rest of listeners to receive event.
	 */
	public static void addListener(GcKeyBindings key, Predicate<GcKeyBindings> listener) {
		view_listeners.add(listener);
	}
}
