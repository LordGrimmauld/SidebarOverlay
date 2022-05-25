package mod.grimmauld.sidebaroverlay.api.overlay.selection.config;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.sidebaroverlay.api.overlay.SelectOverlay;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ArraySelectConfig<T extends Comparable<? super T> & IStringSerializable> extends SelectConfig<T> {
	private final T[] values;
	private int selected;

	public ArraySelectConfig(T[] values, ITextComponent description) {
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
	protected ITextComponent getState() {
		T value = getValue();
		if (value == null)
			return new StringTextComponent("none");
		return new TranslationTextComponent(value.getSerializedName());
	}
}
