package mod.grimmauld.sidebaroverlay.util;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.sidebaroverlay.SidebarOverlay;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;


@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TextHelper {
	public static void sendStatus(@Nullable Player player, String key, Object... args) {
		if (player != null)
			player.displayClientMessage(new TranslatableComponent(translationKey(key), args), true);
	}

	public static String translationKey(String key) {
		return SidebarOverlay.MODID + "." + key;
	}

	public static Component translationComponent(String key) {
		return new TranslatableComponent(translationKey(key));
	}
}
