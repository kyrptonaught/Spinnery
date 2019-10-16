package glib.container.client;

import glib.container.common.BaseContainer;
import glib.container.common.widget.ItemSlot;
import glib.container.common.widget.Widget;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BaseScreen<T extends BaseContainer> extends AbstractContainerScreen<T> {
	T linkedContainer;
	List<Slot> dragSlots = new ArrayList<>();

	public BaseScreen(Text name, T linkedContainer, PlayerEntity player) {
		super(linkedContainer, player.inventory, name);
		setLinkedContainer(linkedContainer);
	}

	public T getLinkedContainer() {
		return linkedContainer;
	}

	public void setLinkedContainer(T linkedContainer) {
		this.linkedContainer = linkedContainer;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	protected boolean isPointWithinBounds(int slotX, int slotY, int defaultSizeX, int defaultSizeY, double mouseX, double mouseY) {
		Optional<? extends Widget> focusedWidget = getLinkedContainer().getLinkedPanel().getLinkedWidgets().stream().filter((widget) -> widget.isFocused(mouseX, mouseY)).findAny();
		if (focusedWidget.isPresent()) {
			if (focusedWidget.get() instanceof ItemSlot) {
				ItemSlot itemSlot = (ItemSlot) focusedWidget.get();
				return (slotX > itemSlot.internalSlot.xPosition - 8
					&&  slotX < itemSlot.internalSlot.xPosition + 8
					&&  slotY > itemSlot.internalSlot.yPosition - 8
					&&  slotY < itemSlot.internalSlot.yPosition + 8);
			} else {
				return (mouseX > slotX - 8
					&&  mouseX < slotX + 8
					&&  mouseY > slotY - 8
					&&  mouseY < slotY + 8);
			}
		} else {
			return false;
		}
	}

	@Override
	protected void drawMouseoverTooltip(int mouseX, int mouseY) {
		getLinkedContainer().getLinkedPanel().getLinkedWidgets().forEach((widget) -> widget.onDrawTooltip());
		super.drawMouseoverTooltip(mouseX, mouseY);
	}

	@Override
	public boolean keyPressed(int character, int keyCode, int keyModifier) {
		getLinkedContainer().getLinkedPanel().getLinkedWidgets().forEach((widget) -> widget.onKeyPressed(keyCode));
		return super.keyPressed(character, keyCode, keyModifier);
	}

	@Override
	public boolean keyReleased(int character, int keyCode, int keyModifier) {
		getLinkedContainer().getLinkedPanel().getLinkedWidgets().forEach((widget) -> widget.onKeyReleased(keyCode));
		return super.keyPressed(character, keyCode, keyModifier);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		getLinkedContainer().getLinkedPanel().getLinkedWidgets().forEach((widget) -> widget.onMouseClicked(mouseX, mouseY, mouseButton));
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
		getLinkedContainer().getLinkedPanel().getLinkedWidgets().forEach((widget) -> widget.onMouseReleased(mouseX, mouseY, mouseButton));
		return super.mouseReleased(mouseX, mouseY, mouseButton);
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		getLinkedContainer().getLinkedPanel().getLinkedWidgets().forEach((widget) -> widget.onMouseMoved(mouseX, mouseY));
		super.mouseMoved(mouseX, mouseY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double mouseZ) {
		getLinkedContainer().getLinkedPanel().getLinkedWidgets().forEach((widget) -> widget.onMouseScrolled(mouseX, mouseY, mouseZ));
		return super.mouseScrolled(mouseX, mouseY, mouseZ);
	}

	@Override
	protected void onMouseClick(Slot slot, int slotX, int slotY, SlotActionType slotActionType) {
		getLinkedContainer().getLinkedPanel().getLinkedWidgets().forEach((widget) -> widget.onSlotClicked(slot, slotX, slotY, slotActionType));
		super.onMouseClick(slot, slotX, slotY, slotActionType);
	}

	@Override
	public boolean mouseDragged(double slotX, double slotY, int mouseButton, double mouseX, double mouseY) {
		getLinkedContainer().getLinkedPanel().getLinkedWidgets().forEach((widget) -> widget.onMouseDragged(slotX, slotY, mouseButton, mouseX, mouseY));
		return super.mouseDragged(slotX, slotY, mouseButton, mouseX, mouseY);
	}

	@Override
	protected void drawBackground(float tick, int mouseX, int mouseY) {
	}

	@Override
	public void tick() {
		super.containerWidth = (int) linkedContainer.getLinkedPanel().getSizeX();
		super.containerHeight = (int) linkedContainer.getLinkedPanel().getSizeY();
		getLinkedContainer().tick();
		super.tick();
	}

	@Override
	public void render(int mouseX, int mouseY, float tick) {
		getLinkedContainer().getLinkedPanel().drawPanel();
		getLinkedContainer().getLinkedPanel().drawWidget();
		getLinkedContainer().getLinkedPanel().getLinkedWidgets().forEach((widget) -> {
			widget.isFocused(mouseX, mouseY);
		});

		super.render(mouseX, mouseY, tick);

		getLinkedContainer().getLinkedPanel().getLinkedWidgets().forEach((widget) -> {
			if (widget.getFocus() && widget instanceof ItemSlot && playerInventory.getCursorStack().isEmpty() && !((ItemSlot) widget).getSlot().getStack().isEmpty()) {
				this.renderTooltip(((ItemSlot) widget).getSlot().getStack(), mouseX, mouseY);
			}
		});
	}
}