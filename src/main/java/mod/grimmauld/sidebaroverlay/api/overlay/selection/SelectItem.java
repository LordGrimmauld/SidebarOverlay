package mod.grimmauld.sidebaroverlay.api.overlay.selection;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import mod.grimmauld.sidebaroverlay.api.overlay.SelectOverlay;
import mod.grimmauld.sidebaroverlay.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SelectItem {
	protected static final Minecraft MC = Minecraft.getInstance();
	private final Component description;
	private final Set<Consumer<RenderGameOverlayEvent.Pre>> renderExtraHooks;

	public SelectItem(Component description) {
		this.description = description;
		renderExtraHooks = new HashSet<>();
	}

	public void onEnter(SelectOverlay screen) {
	}

	public void onOverlayOpen() {
	}

	public MutableComponent getDescription() {
		return description.copy().withStyle(ChatFormatting.WHITE);
	}

	public void onScroll(InputEvent.MouseScrollEvent event) {
	}

	public void onRightClick(InputEvent.MouseInputEvent event) {
	}

	public void continuousRendering(PoseStack ms, SuperRenderTypeBuffer buffer) {
	}

	public void renderActive(PoseStack ms, SuperRenderTypeBuffer buffer) {
	}

	@SafeVarargs
	public final SelectItem registerRenderHooks(Consumer<RenderGameOverlayEvent.Pre>... hook) {
		renderExtraHooks.addAll(Arrays.asList(hook));
		return this;
	}

	public void renderExtra(RenderGameOverlayEvent.Pre event) {
		renderExtraHooks.forEach(c -> c.accept(event));
	}
}
