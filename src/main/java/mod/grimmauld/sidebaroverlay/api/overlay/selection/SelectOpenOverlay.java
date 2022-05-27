package mod.grimmauld.sidebaroverlay.api.overlay.selection;

import net.minecraft.MethodsReturnNonnullByDefault;
import mod.grimmauld.sidebaroverlay.api.overlay.SelectOverlay;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SelectOpenOverlay extends SelectItem {

	private final SelectOverlay toOpen;

	public SelectOpenOverlay(Component description, SelectOverlay toOpen) {
		super(description);
		this.toOpen = toOpen;
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		toOpen.open(screen);
	}
}
