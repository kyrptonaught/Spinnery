package spinnery.container.common.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Level;
import org.lwjgl.glfw.GLFW;
import spinnery.SpinneryMod;
import spinnery.container.client.BaseRenderer;

import java.util.List;

public class WSlot extends WWidget {
	private int slotNumber;
	private ItemStack previewStack = ItemStack.EMPTY;
	private Inventory linkedInventory;

	public WSlot(WAnchor anchor, int positionX, int positionY, int positionZ, double sizeX, double sizeY, int slotNumber, Inventory linkedInventory, WPanel linkedWPanel) {
		setLinkedPanel(linkedWPanel);

		setAnchor(anchor);

		setPositionX(positionX + (getAnchor() == WAnchor.MC_ORIGIN ? getLinkedPanel().getPositionX() : 0));
		setPositionY(positionY + (getAnchor() == WAnchor.MC_ORIGIN ? getLinkedPanel().getPositionY() : 0));
		setPositionZ(positionZ);

		setSizeX(sizeX);
		setSizeY(sizeY);

		setSlotNumber(slotNumber);
		setLinkedInventory(linkedInventory);
	}

	public static void addSingle(WAnchor anchor, int positionX, int positionY, int positionZ, double sizeX, double sizeY, int slotNumber, Inventory linkedInventory, WPanel linkedWPanel) {
		linkedWPanel.add(new WSlot(anchor, positionX, positionY, positionZ, sizeX, sizeY, slotNumber, linkedInventory, linkedWPanel));
	}

	public static void addArray(WAnchor anchor, int arrayX, int arrayY, int positionX, int positionY, int positionZ, double sizeX, double sizeY, int slotNumber, Inventory linkedInventory, WPanel linkedWPanel) {
		for (int y = 0; y < arrayY; ++y) {
			for (int x = 0; x < arrayX; ++x) {
				WSlot.addSingle(anchor, positionX + (int) (sizeX * x), positionY + (int) (sizeY * y), positionZ, sizeX, sizeY, slotNumber++, linkedInventory, linkedWPanel);
			}
		}
	}

	public static void addPlayerInventory(int positionZ, double sizeX, double sizeY, PlayerInventory linkedInventory, WPanel linkedWPanel) {
		int temporarySlotNumber = 0;
		addArray(
				WAnchor.MC_ORIGIN,
				9,
				1,
				4,
				(int) linkedWPanel.getSizeY() - 18 - 4,
				positionZ,
				sizeX,
				sizeY,
				temporarySlotNumber,
				linkedInventory,
				linkedWPanel);
		temporarySlotNumber = 9;
		addArray(
				WAnchor.MC_ORIGIN,
				9,
				3,
				4,
				(int) linkedWPanel.getSizeY() - 72 - 6,
				positionZ,
				sizeX,
				sizeY,
				temporarySlotNumber,
				linkedInventory,
				linkedWPanel);
	}

	public ItemStack getStack() {
		try {
			return getLinkedInventory().getInvStack(getSlotNumber());
		} catch (ArrayIndexOutOfBoundsException exception) {
			SpinneryMod.logger.log(Level.ERROR, "Cannot access slot " + getSlotNumber() + ", as it does exist in the inventory!");
			return ItemStack.EMPTY;
		}
	}

	public void setStack(ItemStack stack) {
		try {
			getLinkedInventory().setInvStack(getSlotNumber(), stack);
		} catch (ArrayIndexOutOfBoundsException exception) {
			SpinneryMod.logger.log(Level.ERROR, "Cannot access slot " + getSlotNumber() + ", as it does exist in the inventory!");
		}
	}

	public ItemStack getPreviewStack() {
		return previewStack;
	}

	public void setPreviewStack(ItemStack previewStack) {
		this.previewStack = previewStack;
	}

	public int getSlotNumber() {
		return slotNumber;
	}

	public void setSlotNumber(int slotNumber) {
		this.slotNumber = slotNumber;
	}

	public Inventory getLinkedInventory() {
		return linkedInventory;
	}

	public void setLinkedInventory(Inventory linkedInventory) {
		this.linkedInventory = linkedInventory;
	}

	@Override
	public void onMouseClicked(double mouseX, double mouseY, int mouseButton) {
		super.onMouseClicked(mouseX, mouseY, mouseButton);
		if (getFocus()) {
			ItemStack stackA = getLinkedPanel().getLinkedContainer().getLinkedPlayerInventory().getCursorStack().copy();
			ItemStack stackB = getStack().copy();

			if (InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), GLFW.GLFW_KEY_LEFT_CONTROL)) {
				if (mouseButton == 0) {
					if (stackB.getCount() < stackB.getMaxCount()) {
						for (WWidget widget : getLinkedPanel().getLinkedWidgets()) {
							if (widget != this) {
								if (widget instanceof WSlot) {
									ItemStack stackC = ((WSlot) widget).getStack();

									if (stackB.getCount() < stackB.getMaxCount() && stackB.getItem() == stackC.getItem()) {
										int quantityA = stackB.getMaxCount() - stackB.getCount();

										int quantityB = stackC.getCount() - quantityA;

										if (quantityB <= 0) {
											stackB.increment(stackC.getCount());
											stackC.decrement(stackC.getCount());
										} else {
											stackB.increment(quantityA);
											stackC.decrement(quantityA);
										}
									}
								} else if (widget instanceof WList) {
									for (List listWidget : ((WList) widget).getListWidgets()) {
										for (Object internalWidget : listWidget) {
											if (internalWidget instanceof WSlot) {
												ItemStack stackC = ((WSlot) internalWidget).getStack();

												if (stackB.getCount() < stackB.getMaxCount() && stackB.isItemEqualIgnoreDamage(stackC)) {
													int quantityA = stackB.getMaxCount() - stackB.getCount();

													stackC.decrement(quantityA);

													if (stackC.getCount() <= 0) {
														stackB.increment(stackC.getCount());
														stackC = ItemStack.EMPTY;
													} else {
														stackB.increment(quantityA);
														stackC.decrement(quantityA);
													}
												} else {
													setStack(stackB);
													return;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			} else {
				if (mouseButton == 2) {
					if (getLinkedPanel().getLinkedContainer().getLinkedPlayerInventory().player.isCreative()) {
						getLinkedPanel().getLinkedContainer().getLinkedPlayerInventory().setCursorStack(new ItemStack(stackB.getItem(), stackB.getMaxCount()));
						return;
					}
				} else if (stackA.isItemEqualIgnoreDamage(stackB)) {
					if (mouseButton == 0) {
						int quantityA = stackA.getCount(); // Cursor
						int quantityB = stackB.getCount(); // WSlot

						if (quantityA <= stackB.getMaxCount() - quantityB) {
							stackB.increment(quantityA);
							stackA.decrement(quantityA);
						} else {
							int quantityC = stackB.getMaxCount() - quantityB;

							stackB.increment(quantityC);
							stackA.decrement(quantityC);
						}
					} else if (mouseButton == 1) {
						stackA.decrement(1);
						stackB.increment(1);
					}
				} else if (!stackB.isEmpty() && stackA.isEmpty() && mouseButton == 1) {
					int quantityA = (int) Math.ceil(stackB.getCount() / 2f);

					stackA = new ItemStack(stackB.getItem(), quantityA);
					stackB.decrement(quantityA);
				} else if (stackB.isEmpty() && !stackA.isEmpty() && mouseButton == 1) {
					stackB = new ItemStack(stackA.getItem(), 1);
					stackA.decrement(1);
				} else if (mouseButton == 0) {
					if (stackA.isEmpty()) {
						stackA = stackB.copy();
						stackB = ItemStack.EMPTY;
					} else {
						stackB = stackA.copy();
						stackA = ItemStack.EMPTY;
					}
				}
			}
			getLinkedPanel().getLinkedContainer().getLinkedPlayerInventory().setCursorStack(stackA);
			setStack(stackB);
		}
	}

	@Override
	public void onMouseDragged(double mouseX, double mouseY, int mouseButton, double dragOffsetX, double dragOffsetY) {
		if (isWithinBounds(mouseX, mouseY) && InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
			if (!getLinkedPanel().getLinkedContainer().getDragSlots().contains(this)) {
				getLinkedPanel().getLinkedContainer().getDragSlots().add(this);
			}
		}
		super.onMouseDragged(mouseX, mouseY, mouseButton, dragOffsetX, dragOffsetY);
	}

	@Override
	public void drawWidget() {
		BaseRenderer.drawBeveledPanel(getPositionX(), getPositionY(), getPositionZ(), getSizeX(), getSizeY(), 0xFF373737, getFocus() ? 0xFF00C116 : 0xFF8b8b8b, 0xFFFFFFFF);

		ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
		GuiLighting.enableForItems();
		itemRenderer.renderGuiItem(getPreviewStack().isEmpty() ? getStack() : getPreviewStack(), 1 + (int) (getPositionX() + (getSizeX() - 18) / 2), 1 + (int) (getPositionY() + (getSizeY() - 18) / 2));
		itemRenderer.renderGuiItemOverlay(MinecraftClient.getInstance().textRenderer, getPreviewStack().isEmpty() ? getStack() : getPreviewStack(), 1 + (int) (getPositionX() + (getSizeX() - 18) / 2), 1 + (int) (getPositionY() + (getSizeY() - 18) / 2));
		GuiLighting.disable();
	}
}
