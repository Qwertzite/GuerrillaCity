//package qwertzite.aviation.airbasesecurity;
//
//import net.minecraft.item.Item;
//import qwertzite.aviation.airbasesecurity.entity.Entity120mmMortarM120;
//import qwertzite.aviation.airbasesecurity.entity.Entity120mmMortarShellM933HE;
//import qwertzite.aviation.airbasesecurity.item.Item120mmMortar;
//import qwertzite.aviation.airbasesecurity.item.Item120mmMortarShellM933HE;
//import qwertzite.aviation.airbasesecurity.item.ItemStarlightScope;
//import qwertzite.aviation.airbasesecurity.network.PacketMortar120M120Ctrl;
//import qwertzite.aviation.core.init.AcCreativeTabs;
//import qwertzite.aviation.core.init.LoaderEntity;
//import qwertzite.aviation.core.init.LoaderItems;
//import qwertzite.aviation.core.network.AcNetwork;
//
//public class AcAirBaseSecurity {
//	public static final String SECURITY_ITEM_MODEL_PATH = "security/";
//	
//	public static Item MORTAR_120mm_M120;
//	public static Item MORTAR_SHELL_120mm_M933_HE;
//	
//	public static Item STARLIGHT_SCOPE;
//	
//	public static void onPreInit() {
//		MORTAR_120mm_M120 = LoaderItems.newEntry(new Item120mmMortar(), "mortar120_m120")
//				.setCreativeTab(AcCreativeTabs.SECURITY)
//				.setModelPath(SECURITY_ITEM_MODEL_PATH)
//				.register();
//		MORTAR_SHELL_120mm_M933_HE = LoaderItems.newEntry(new Item120mmMortarShellHE(), "mortar120shell_m933he")
//				.setCreativeTab(AcCreativeTabs.SECURITY)
//				.setModelPath(SECURITY_ITEM_MODEL_PATH)
//				.register();
//		
//		LoaderEntity.register("security.mortar120_m120", Entity120mmMortarM120.class,
//				"ac.security.mortar120_m120", 128, 5, true);
//		LoaderEntity.register("security.mortar120shell_m933he", Entity120mmMortarShellM933HE.class,
//				"ac.security.mortar120shell_m933he", 128, 2, true);
//		
//		STARLIGHT_SCOPE = LoaderItems.newEntry(new ItemStarlightScope(), "starlight_scope")
//				.setCreativeTab(AcCreativeTabs.SECURITY)
//				.setModelPath(SECURITY_ITEM_MODEL_PATH)
//				.register();
//		
//	}
//	
//	public static void onInit() {
//		AcNetwork.registerPacket(PacketMortar120M120Ctrl.class);
//	}
//}
