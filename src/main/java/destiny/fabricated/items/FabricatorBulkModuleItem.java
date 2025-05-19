package destiny.fabricated.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FabricatorBulkModuleItem extends FabricatorModuleItem
{
    public static final String BULK_AMOUNT = "bulk_amount";

    public FabricatorBulkModuleItem(Properties pProperties)
    {
        super(pProperties);
    }

    public static ItemStack create(FabricatorBulkModuleItem item, int amount)
    {
        ItemStack stack = new ItemStack(item);
        item.setBulkAmount(stack, amount);

        return stack;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
                                TooltipFlag pIsAdvanced)
    {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.literal(String.valueOf(getBulkAmount(pStack))).withStyle(ChatFormatting.DARK_GREEN));
    }

    public int getBulkAmount(ItemStack stack)
    {
        if(stack.getTag() != null && stack.getTag().contains(BULK_AMOUNT))
            return stack.getTag().getInt(BULK_AMOUNT);

        return 0;
    }

    public void setBulkAmount(ItemStack stack, int amount)
    {
        stack.getOrCreateTag().putInt(BULK_AMOUNT, amount);
    }
}
