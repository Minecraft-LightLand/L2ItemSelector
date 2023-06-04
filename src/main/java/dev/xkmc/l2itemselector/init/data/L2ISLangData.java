package dev.xkmc.l2itemselector.init.data;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum L2ISLangData {
	;

	private final String key, def;
	private final int arg;


	L2ISLangData(String key, String def, int arg) {
		this.key = key;
		this.def = def;
		this.arg = arg;
	}

	public MutableComponent get(Object... args) {
		if (args.length != arg)
			throw new IllegalArgumentException("for " + name() + ": expect " + arg + " parameters, got " + args.length);
		return Component.translatable(key, args);
	}

	public static void genLang(RegistrateLangProvider pvd) {
		for (L2ISLangData lang : L2ISLangData.values()) {
			pvd.add(lang.key, lang.def);
		}
		for (L2Keys lang : L2Keys.values()) {
			pvd.add(lang.id, lang.def);
		}
		pvd.add("key.categories.l2mods", "L2Mods Keys");
	}

}
