package destiny.fabricated.menu;

import destiny.fabricated.block_entities.FabricatorBlockEntity;
import destiny.fabricated.init.BlockInit;
import destiny.fabricated.init.MenuInit;
import destiny.fabricated.init.NetworkInit;
import destiny.fabricated.items.FabricatorRecipeModuleItem;
import destiny.fabricated.network.packets.ServerboundFabricatorAnimPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;
import java.util.Set;

public class FabricatorBrowserCraftingMenu extends AbstractContainerMenu
{
    public FabricatorBlockEntity blockEntity;
    public Level level;
    public List<FabricatorRecipeModuleItem.RecipeData> recipeTypes;
    public boolean switching;
    public int item = 0;

    public FabricatorBrowserCraftingMenu(int containerId, Inventory inventory, FriendlyByteBuf buffer)
    {
        this(containerId, inventory, inventory.player.level().getBlockEntity(buffer.readBlockPos()));
    }

    public FabricatorBrowserCraftingMenu(int pContainerId, Inventory inventory, BlockEntity blockEntity)
    {
        super(MenuInit.FABRICATOR_BROWSER.get(), pContainerId);
        this.blockEntity = ((FabricatorBlockEntity) blockEntity);
        this.level = inventory.player.level();
        this.recipeTypes = ((FabricatorBlockEntity) blockEntity).getRecipeTypes();

        for (int i = 0; i < 9; ++i)
        {
            this.addSlot(new Slot(inventory, i, -10000, 0));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public void removed(Player pPlayer)
    {
        if(switching)
            return;

        blockEntity.close(level, blockEntity.getBlockPos(), false);
    }

    @Override
    public void slotsChanged(Container pContainer)
    {
        super.slotsChanged(pContainer);
    }

    @Override
    public boolean stillValid(Player player)
    {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.FABRICATOR.get());
    }
}
