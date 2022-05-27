package mod.grimmauld.sidebaroverlay.api.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.MethodsReturnNonnullByDefault;
import mod.grimmauld.sidebaroverlay.Manager;
import mod.grimmauld.sidebaroverlay.api.Keyboard;
import mod.grimmauld.sidebaroverlay.api.overlay.selection.SelectItem;
import mod.grimmauld.sidebaroverlay.api.overlay.selection.config.SelectConfig;
import mod.grimmauld.sidebaroverlay.render.ExtraTextures;
import mod.grimmauld.sidebaroverlay.util.KeybindHelper;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static mod.grimmauld.sidebaroverlay.Manager.*;
import static net.minecraft.client.gui.GuiComponent.*;

@ParametersAreNonnullByDefault
public class SelectOverlay {

	private static final Minecraft MC = Minecraft.getInstance();
	private final Component title;
	public boolean canBeOpenedDirectly;
	public List<SelectItem> options;
	int menuWidth;
	int menuHeight;
	private boolean visible;
	private int targetY;
	private float movingY;
	private SelectOverlay previous;
	private int selectedOptionIndex;

	public SelectOverlay(Component titleIn) {
		this.title = titleIn;
		visible = false;
		options = new ArrayList<>();
		movingY = 0;
		targetY = 0;
		selectedOptionIndex = 0;
		canBeOpenedDirectly = false;
		previous = null;
		adjustTarget();
	}

	public void testAndClose(InputEvent event) {
		if (Keyboard.ESC.isKeyDown() && Manager.shouldCloseOnEsc) {
			close();
			return;
		}
		if (KeybindHelper.eventActivatesKeybind(event, TOOL_DEACTIVATE)) {
			close();
			if (previous != null)
				previous.open(previous.previous);
		}
	}

	public SelectOverlay withOptions(List<SelectItem> options) {
		this.options = options;
		return this;
	}

	public SelectOverlay addOptions(Collection<? extends SelectItem> options) {
		this.options.addAll(options);
		return this;
	}

	public <T extends SelectItem> SelectOverlay addOption(T option) {
		this.options.add(option);
		return this;
	}

	public SelectOverlay configureDirectOpen(boolean canBeOpenedDirectly) {
		this.canBeOpenedDirectly = canBeOpenedDirectly;
		return this;
	}

	public boolean testAndOpenDirectly() {
		if (canBeOpenedDirectly && !visible) {
			open(null);
			return true;
		}
		return false;
	}

	public void open(@Nullable SelectOverlay previous) {
		if (MC.screen != null)
			return;
		this.previous = previous;
		if (previous != null)
			previous.close();
		this.updateContents();
		this.setVisible(true);
		this.options.forEach(SelectItem::onOverlayOpen);
	}

	public void render(RenderGameOverlayEvent.Pre event) {
		if (visible)
			draw(event.getMatrixStack(), event.getPartialTicks());
	}

	private void draw(PoseStack ms, float partialTicks) {
		Window window = MC.getWindow();

		int x = window.getGuiScaledWidth() - menuWidth - 10;
		int y = window.getGuiScaledHeight() - menuHeight;

		boolean sideways = false;
		if ((window.getGuiScaledWidth() - 182) / 2 < menuWidth + 20) {
			sideways = true;
			y -= 24;
		}

		ms.pushPose();
		float shift = yShift(partialTicks);
		float sidewaysShift = shift * ((float) menuWidth / (float) menuHeight) + (40 + menuHeight / 4f)
			+ 8;

		ms.translate(sideways ? sidewaysShift : 0, sideways ? 0 : shift, 0);

		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1, 1, 1, 3 / 4f);

		RenderSystem.setShaderTexture(0, ExtraTextures.GRAY.getLocation());
		blit(ms, x, y, 0, 0, menuWidth, menuHeight, 16, 16);
		RenderSystem.setShaderColor(1, 1, 1, 1);

		int yPos = y + 4;
		int xPos = x + 4;

		Font font = MC.font;

		// TODO add Keybinds

		font.drawShadow(ms, title, xPos, yPos, 0xEEEEEE);

		yPos += 4;

		// TODO: Add entry Keybinds

		yPos += 4;
		yPos += font.lineHeight;

		for (int i = 0; i < options.size(); i++) {
			MutableComponent desc = options.get(i).getDescription();
			if (i == selectedOptionIndex)
				desc.withStyle(ChatFormatting.UNDERLINE, ChatFormatting.ITALIC);
			font.draw(ms, desc, xPos, yPos, menuWidth - 8);
			yPos += font.lineHeight + 2;
		}

		ms.popPose();
	}

	private float yShift(float partialTicks) {
		return (movingY + (targetY - movingY) * 0.2f * partialTicks);
	}

	public void onClientTick() {
		if (movingY != targetY) {
			movingY += (targetY - movingY) * 0.2;
		}
	}

	protected void adjustTarget() {
		targetY = visible ? -14 : 0;
	}

	public void close() {
		setVisible(false);
	}

	public void updateContents() {
		int fontheight = MC.font.lineHeight;

		this.menuWidth = 158;
		this.menuHeight = 4;
		this.menuHeight += 12; // title

		// todo special keybinds
		menuHeight += 4 + (2 + fontheight) * options.size();
		adjustTarget();
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		adjustTarget();
	}

	public void select() {
		getActiveSelectItem().ifPresent(selectItem -> selectItem.onEnter(this));
	}

	public Optional<SelectItem> getActiveSelectItem() {
		if (!this.visible || options.isEmpty())
			return Optional.empty();
		return Optional.of(options.get(selectedOptionIndex % options.size()));
	}

	public Optional<SelectConfig<?>> getActiveSelectConfig() {
		if (!this.visible || options.isEmpty())
			return Optional.empty();
		SelectItem item = options.get(selectedOptionIndex % options.size());
		if (!(item instanceof SelectConfig))
			return Optional.empty();
		return Optional.of(((SelectConfig<?>) item));
	}

	public void advanceSelectionIndex(int i) {
		if (!options.isEmpty()) {
			selectedOptionIndex -= i;
			while (selectedOptionIndex < 0)
				selectedOptionIndex += options.size();
			selectedOptionIndex %= options.size();
		}
	}

	public SelectOverlay register() {
		Manager.overlays.add(this);
		return this;
	}

	public void onScroll(InputEvent.MouseScrollEvent event) {
		int amount = (int) Math.signum(event.getScrollDelta());
		if (TOOL_SELECT.isDown()) {
			this.advanceSelectionIndex(amount);
			event.setCanceled(true);
		} else if (TOOL_CONFIG.isDown()) {
			getActiveSelectConfig().ifPresent(selectConfig -> {
				selectConfig.onScrolled(amount);
				event.setCanceled(true);
			});
		}
		if (!event.isCanceled())
			getActiveSelectItem().ifPresent(item -> item.onScroll(event));
	}
}
