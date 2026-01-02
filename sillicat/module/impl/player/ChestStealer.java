package sillicat.module.impl.player;

import net.minecraft.client.gui.inventory.GuiChest;
import sillicat.module.Category;
import sillicat.module.Module;
import sillicat.module.ModuleInfo;
import sillicat.setting.impl.BindSetting;
import sillicat.setting.impl.BooleanSetting;
import sillicat.setting.impl.ModeSetting;
import sillicat.setting.impl.NumberSetting;

import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;


@ModuleInfo(
        name = "ChestStealer",
        description = "Remove items from chests efficiently.",
        category = Category.Player,
        defaultKey = -1,
        enabled = false
)
public class ChestStealer extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Steal", "Stash");
    public static final NumberSetting speed = new NumberSetting("Speed (ms)", 100, 5, 100, 5);
    private final BooleanSetting close = new BooleanSetting("Close", false);


    private boolean finished = false;
    private long lastAction = 0L;

    public ChestStealer() {
        addSettings(mode, speed, close);
        setKey(getKey());
    }

    @Override
    public void onUpdate() {
        if (mc.thePlayer == null || !(mc.currentScreen instanceof GuiChest)) {
            finished = false;
            super.onUpdate();
            return;
        }

        GuiChest chest = (GuiChest) mc.currentScreen;

        long now = System.currentTimeMillis();
        if (now - lastAction < (long) speed.getVal()) {
            super.onUpdate();
            return;
        }
        lastAction = now;

        if (!finished) {
            if (mode.getCurrMode().equalsIgnoreCase("Steal")) {
                GuiChest.INSTANCE.steal();
            } else if (mode.getCurrMode().equalsIgnoreCase("Stash")) {
                GuiChest.INSTANCE.stash();
            }

            if (isChestEmpty(chest)) {
                finished = true;
            }

            super.onUpdate();
            return;
        }

        if (close.isEnabled()) {
            mc.thePlayer.closeScreen();
            finished = false;
        }

        super.onUpdate();
    }

    private boolean isChestEmpty(GuiChest chest) {
        ContainerChest container = (ContainerChest) chest.inventorySlots;
        int chestSize = container.getLowerChestInventory().getSizeInventory();

        for (int i = 0; i < chestSize; i++) {
            Slot slot = container.getSlot(i);
            if (slot.getHasStack()) {
                return false;
            }
        }
        return true;
    }
}
