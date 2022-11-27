package qwertzite.guerrillacity.core.command;

import com.google.gson.JsonObject;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;

public class GcStringArgumentTypeInfo implements ArgumentTypeInfo<GcStringArgument, GcStringArgumentTypeInfo.Template> {

	
	@Override
	public void serializeToNetwork(Template pTemplate, FriendlyByteBuf pBuffer) {}

	@Override
	public Template deserializeFromNetwork(FriendlyByteBuf pBuffer) {
		return new Template();
	}

	@Override
	public void serializeToJson(Template pTemplate, JsonObject pJson) {}

	@Override
	public Template unpack(GcStringArgument pArgument) {
		return new Template();
	}

	public final class Template implements ArgumentTypeInfo.Template<GcStringArgument> {
		
		Template() {}
		
		@Override
		public GcStringArgument instantiate(CommandBuildContext pContext) {
			return new GcStringArgument();
		}
		
		@Override
		public ArgumentTypeInfo<GcStringArgument, ?> type() {
			return GcStringArgumentTypeInfo.this;
		}
	}
}
