package mod.grimmauld.sidebaroverlay.api.overlay.selection.config;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.sidebaroverlay.api.overlay.SelectOverlay;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BooleanSelectConfig extends NonNullSelectConfig<Boolean> {
	private boolean value;

	public BooleanSelectConfig(Component description, boolean defaultValue) {
		super(description);
		this.value = defaultValue;
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		value = !value;
		this.onValueChanged();
	}

	@Override
	public void onScrolled(int amount) {
		value ^= Math.abs(amount) % 2 != 0;
		this.onValueChanged();
	}

	@Override
	@Nonnull
	protected Component getState() {
		return new TextComponent(value ? "On" : "Off");
	}

	@Override
	public Boolean getValue() {
		return value;
	}
}
