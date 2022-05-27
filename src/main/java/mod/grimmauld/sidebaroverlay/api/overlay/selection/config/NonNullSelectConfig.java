package mod.grimmauld.sidebaroverlay.api.overlay.selection.config;

import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;

public abstract class NonNullSelectConfig<T extends Comparable<? super T>> extends SelectConfig<T> {
	public NonNullSelectConfig(Component description) {
		super(description);
	}

	@Override
	@Nonnull
	public abstract T getValue();
}
