package mod.grimmauld.sidebaroverlay.api.overlay.selection;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.sidebaroverlay.api.overlay.SelectOverlay;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SelectOpenOverlay extends SelectItem {

	private final SelectOverlay toOpen;

	public SelectOpenOverlay(ITextComponent description, SelectOverlay toOpen) {
		super(description);
		this.toOpen = toOpen;
	}

	@Override
	public void onEnter(SelectOverlay screen) {
		super.onEnter(screen);
		toOpen.open(screen);
	}
}
