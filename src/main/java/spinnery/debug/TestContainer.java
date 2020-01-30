package spinnery.debug;

import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.ItemStack;
import spinnery.common.BaseContainer;
import spinnery.common.BaseInventory;
import spinnery.widget.WInterface;
import spinnery.widget.WPosition;
import spinnery.widget.WSize;
import spinnery.widget.WSlot;
import spinnery.widget.WType;

public class TestContainer extends BaseContainer {
	public TestContainer(int synchronizationID, PlayerInventory newLinkedPlayerInventory) {
		super(synchronizationID, newLinkedPlayerInventory);

		getInventories().put(PLAYER_INVENTORY, newLinkedPlayerInventory);

		// WInterface
		WInterface mainInterface = new WInterface(this);

		getHolder().add(mainInterface);

		WSlot.addPlayerInventory(mainInterface, PLAYER_INVENTORY);

		BaseInventory inv = new BaseInventory(1);

		getInventories().put(1, inv);
		mainInterface.add(new WSlot(mainInterface, 0, 1));
		// WInterface
	}
}