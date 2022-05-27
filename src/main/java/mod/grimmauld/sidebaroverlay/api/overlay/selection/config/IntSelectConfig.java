package mod.grimmauld.sidebaroverlay.api.overlay.selection.config;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class IntSelectConfig extends NonNullSelectConfig<Integer> {
	private final int min;
	private final int max;
	private int value;

	public IntSelectConfig(Component description, int min, int defaultValue, int max) {
		super(description);
		this.min = min;
		this.value = defaultValue;
		this.max = max;
		this.onValueChanged();
	}

	@Override
	public void onScrolled(int amount) {
		value = Mth.clamp(value + amount, min, max);
		this.onValueChanged();
	}

	@Override
	public Integer getValue() {
		return value;
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}
}
