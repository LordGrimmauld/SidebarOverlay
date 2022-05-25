package mod.grimmauld.sidebaroverlay.api.overlay.selection.config;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.ITextComponent;

public class EnumSelectConfig<T extends Enum<T> & IStringSerializable> extends ArraySelectConfig<T> {
	public EnumSelectConfig(Class<T> enumClass, ITextComponent description) {
		super(enumClass.getEnumConstants(), description);
	}
}
