package mod.grimmauld.sidebaroverlay;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.sidebaroverlay.api.Keyboard;
import mod.grimmauld.sidebaroverlay.api.overlay.SelectOverlay;
import mod.grimmauld.sidebaroverlay.api.overlay.selection.SelectItem;
import mod.grimmauld.sidebaroverlay.render.SuperRenderTypeBuffer;
import mod.grimmauld.sidebaroverlay.util.KeybindHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Logger;

import static mod.grimmauld.sidebaroverlay.util.TextHelper.translationComponent;
import static mod.grimmauld.sidebaroverlay.util.TextHelper.translationKey;


@Mod.EventBusSubscriber(value = Dist.CLIENT)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SuppressWarnings("unused")
public class Manager {
	public static final boolean shouldCloseOnEsc = false;
	public static final Set<SelectOverlay> overlays = new HashSet<>();
	public static final String IMC_ADD_OVERLAY_ENTRY = "addOverlayEntry";

	public static KeyMapping TOOL_CONFIG;
	public static KeyMapping TOOL_DEACTIVATE;
	public static KeyMapping TOOL_SELECT;
	public static KeyMapping TOOL_ACTIVATE;

	public static void init(FMLClientSetupEvent event) {
		TOOL_DEACTIVATE = new KeyMapping(translationKey("keybind.menu"), Keyboard.O.getKeycode(), SidebarOverlay.NAME);
		TOOL_SELECT = new KeyMapping(translationKey("keybind.select_tool"), Keyboard.LALT.getKeycode(), SidebarOverlay.NAME);
		TOOL_ACTIVATE = new KeyMapping(translationKey("keybind.activate_tool"), Keyboard.ENTER.getKeycode(), SidebarOverlay.NAME);
		TOOL_CONFIG = new KeyMapping(translationKey("keybind.config"), Keyboard.CTRL.getKeycode(), SidebarOverlay.NAME);

		ClientRegistry.registerKeyBinding(TOOL_DEACTIVATE);
		ClientRegistry.registerKeyBinding(TOOL_SELECT);
		ClientRegistry.registerKeyBinding(TOOL_ACTIVATE);
		ClientRegistry.registerKeyBinding(TOOL_CONFIG);
	}

	public static void onInterModProcess(InterModProcessEvent event) {
		SelectOverlay overlayMain = new SelectOverlay(translationComponent("overlay.main"))
			.configureDirectOpen(true);
		event.getIMCStream(IMC_ADD_OVERLAY_ENTRY::equals)
			.map(InterModComms.IMCMessage::getMessageSupplier)
			.map(Supplier::get)
			.peek(System.out::println)
			.filter(SelectItem.class::isInstance)
			.map(SelectItem.class::cast)
			.forEach(overlayMain::addOption);
		overlayMain.register();
	}

	@SubscribeEvent
	public static void onKeyPressed(InputEvent.KeyInputEvent event) {
		if (event.getAction() != Keyboard.PRESS)
			return;
		testKeybinds(event);
	}

	private static void testKeybinds(InputEvent event) {
		Optional<SelectOverlay> activeOverlay = getActiveOverlay();
		if (KeybindHelper.eventActivatesKeybind(event, TOOL_ACTIVATE)) {
			SelectOverlay activeSelectOverlay = activeOverlay.orElse(null);
			if (activeSelectOverlay != null) {
				activeSelectOverlay.select();
				return;
			} else {
				for (SelectOverlay overlay : overlays) {
					if (overlay.testAndOpenDirectly())
						return;
				}
			}
		}

		activeOverlay.ifPresent(overlay -> overlay.testAndClose(event));
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		overlays.forEach(SelectOverlay::onClientTick);
	}

	@SubscribeEvent
	public static void onDrawGameOverlay(RenderGameOverlayEvent.Pre event) {
		if (event.getType() != RenderGameOverlayEvent.ElementType.ALL)
			return;
		overlays.stream().filter(SelectOverlay::isVisible).forEach(selectScreen -> selectScreen.render(event));
		getActiveOverlay().flatMap(SelectOverlay::getActiveSelectItem).ifPresent(selectItem -> selectItem.renderExtra(event));
	}

	@SubscribeEvent
	public static void onMouseScrolled(InputEvent.MouseScrollEvent event) {
		if (Minecraft.getInstance().screen != null)
			return;
		overlays.stream().filter(SelectOverlay::isVisible).forEach(overlay -> overlay.onScroll(event));
	}

	@SubscribeEvent
	public static void onMouseInput(InputEvent.MouseInputEvent event) {
		if (Minecraft.getInstance().screen != null)
			return;

		int button = event.getButton();
		boolean pressed = !(event.getAction() == 0);

		if (!pressed)
			return;

		Minecraft mc = Minecraft.getInstance();
		if (button == mc.options.keyUse.getKey().getValue()) {
			if (mc.level == null || mc.player == null || mc.player.isShiftKeyDown())
				return;
			getActiveOverlay().flatMap(SelectOverlay::getActiveSelectItem).ifPresent(selectItem -> selectItem.onRightClick(event));
		}
		testKeybinds(event);
	}

	public static Optional<SelectOverlay> getActiveOverlay() {
		return overlays.stream().filter(SelectOverlay::isVisible).findFirst();
	}

	@SubscribeEvent
	public static void onRenderWorld(RenderWorldLastEvent event) {
		PoseStack ms = event.getMatrixStack();
		Camera info = Minecraft.getInstance().gameRenderer.getMainCamera();
		Vec3 view = info.getPosition();
		ms.pushPose();
		ms.translate(-view.x(), -view.y(), -view.z());
		SuperRenderTypeBuffer buffer = SuperRenderTypeBuffer.getInstance();
		Manager.getActiveOverlay().ifPresent(overlay -> overlay.options.forEach(selectItem -> selectItem.continuousRendering(ms, buffer)));
		Manager.getActiveOverlay().flatMap(SelectOverlay::getActiveSelectItem).ifPresent(selectItem -> selectItem.renderActive(ms, buffer));
		buffer.draw();

		ms.popPose();
	}
}
