package mod.grimmauld.sidebaroverlay.util;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.client.event.InputEvent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class KeybindHelper {
	public static boolean eventActivatesKeybind(InputEvent event, @Nullable KeyMapping key) {
		if (key == null)
			return false;
		if (event instanceof InputEvent.KeyInputEvent) {
			InputEvent.KeyInputEvent keyEvent = ((InputEvent.KeyInputEvent) event);
			return key.isActiveAndMatches(InputConstants.getKey(keyEvent.getKey(), keyEvent.getScanCode()));
		}
		if (event instanceof InputEvent.MouseInputEvent) {
			InputEvent.MouseInputEvent mouseEvent = ((InputEvent.MouseInputEvent) event);
			return key.matchesMouse(mouseEvent.getButton());
		}
		return false;
	}
}
