//package qwertzite.aviation.airbasesecurity;
//
//import net.minecraftforge.common.MinecraftForge;
//import net.minecraftforge.fml.client.registry.RenderingRegistry;
//import qwertzite.aviation.airbasesecurity.entity.Entity120mmMortarM120;
//import qwertzite.aviation.airbasesecurity.entity.Entity120mmMortarShellM933HE;
//import qwertzite.aviation.airbasesecurity.eventhandler.EventHandlerBaseSecurityInput;
//import qwertzite.aviation.airbasesecurity.eventhandler.EventHandlerStarlightScope;
//import qwertzite.aviation.airbasesecurity.renderer.Render120mmMortarM120;
//import qwertzite.aviation.airbasesecurity.renderer.Render120mmMortarShellM933HE;
//
//public class AcAirBaseSecurityClient {
//	
//	public static void onPreInit() {
//		RenderingRegistry.registerEntityRenderingHandler(Entity120mmMortarM120.class, Render120mmMortarM120::new);
//		RenderingRegistry.registerEntityRenderingHandler(Entity120mmMortarShellM933HE.class, Render120mmMortarShellM933HE::new);
//		
//		MinecraftForge.EVENT_BUS.register(new EventHandlerBaseSecurityInput());
//		MinecraftForge.EVENT_BUS.register(new EventHandlerStarlightScope());
//	}
//}
