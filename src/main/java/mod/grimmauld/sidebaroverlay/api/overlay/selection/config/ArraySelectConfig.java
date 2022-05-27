package mod.grimmauld.sidebaroverlay.api.overlay.selection.config;


import mod.grimmauld.sidebaroverlay.api.overlay.SelectOverlay;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.StringRepresentable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ArraySelectConfig<T extends Comparable<? super T> & StringRepresentable> extends SelectConfig<T> {
	private final T[] values;
	private int selected;

	public ArraySelectConfig(T[] values, Component description) {
		super(description);
		this.values = values;
		selected = 0;
	}

	@Override
	public void onScrolled(int amount) {
		if (values.length != 0)
			selected = Math.floorMod(selected + amount, values.length);
		onValueChanged();
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		onScrolled(1);
	}

	@Nullable
	@Override
	public T getValue() {
		if (values.length == 0)
			return null;
		return values[selected];
	}

	@Override
	protected Component getState() {
		T value = getValue();
		if (value == null)
			return new TextComponent("none");
		return new TranslatableComponent(value.getSerializedName());
	}
}
