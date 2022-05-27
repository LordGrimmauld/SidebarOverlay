package mod.grimmauld.sidebaroverlay.api.overlay.selection.config;

import net.minecraft.util.StringRepresentable;
import net.minecraft.network.chat.Component;

public class EnumSelectConfig<T extends Enum<T> & StringRepresentable> extends ArraySelectConfig<T> {
	public EnumSelectConfig(Class<T> enumClass, Component description) {
		super(enumClass.getEnumConstants(), description);
	}
}
