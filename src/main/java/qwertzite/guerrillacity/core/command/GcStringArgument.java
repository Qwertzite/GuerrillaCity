package qwertzite.guerrillacity.core.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class GcStringArgument implements ArgumentType<String> {
	
	public static GcStringArgument string() {
		return new GcStringArgument();
	}
	
	public static String getString(final CommandContext<?> context, final String name) {
		return context.getArgument(name, String.class);
	}
	
	private final List<String> examples = new ArrayList<>();
	
	public GcStringArgument() {
	}
	
	@Override
	public String parse(StringReader reader) throws CommandSyntaxException {
		final int start = reader.getCursor();
		while (reader.canRead() && !Character.isWhitespace(reader.peek())) {
			reader.skip();
		}
		return reader.getString().substring(start, reader.getCursor());
	}

	@Override
	public String toString() {
		return "gc_string";
	}

	@Override
	public Collection<String> getExamples() {
		return examples;
	}
}
