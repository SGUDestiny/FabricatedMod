package destiny.fabricated.items;

import destiny.fabricated.block_entities.FabricatorBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FabricatorModuleItem extends Item
{
    public FabricatorModuleItem(Properties pProperties)
    {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        Level level = context.getLevel();
        BlockEntity blockEntity = level.getBlockEntity(context.getClickedPos());
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();

        if(level.isClientSide())
            return InteractionResult.CONSUME;

        if(player != null && blockEntity instanceof FabricatorBlockEntity fabricator)
        {
            int slot;
            for (slot = 0; slot < fabricator.upgrades.getSlots(); slot++)
            {
                if(fabricator.upgrades.getStackInSlot(slot).isEmpty())
                    break;
            }
            if(slot > fabricator.upgrades.getSlots())
            {
                player.displayClientMessage(Component.translatable("message.fabricated.fabricator_full").withStyle(ChatFormatting.RED), true);
                return InteractionResult.CONSUME;
            }
            player.setItemInHand(context.getHand(), fabricator.upgrades.insertItem(slot, stack, false));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.CONSUME;
    }
}
