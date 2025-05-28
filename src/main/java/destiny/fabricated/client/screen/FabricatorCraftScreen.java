package destiny.fabricated.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import destiny.fabricated.FabricatedMod;
import destiny.fabricated.items.FabricatorRecipeModuleItem.RecipeData;
import destiny.fabricated.menu.FabricatorCraftingMenu;
import destiny.fabricated.util.RenderBlitUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FabricatorCraftScreen extends AbstractContainerScreen<FabricatorCraftingMenu>
{
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FabricatedMod.MODID, "textures/gui/fabricator_icon_bg.png");
    public List<Recipe<Container>> recipes;
    public int selectedRecipe;
    public boolean hasSelected;
    public int selectedType;

    public FabricatorCraftScreen(FabricatorCraftingMenu pMenu, Inventory pPlayerInventory, Component pTitle)
    {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 256;
        this.imageHeight = 256;

        this.recipes = new ArrayList<>();
        this.hasSelected = false;
        this.selectedType = -1;
        this.selectedRecipe = 0;
    }

    @Override
    protected void init()
    {
        super.init();
        int baseX = (width - imageWidth) / 2;
        int baseY = (height - imageHeight) / 2 - this.menu.recipeTypes.size()*11+11;

        for (int i = 0; i < this.menu.recipeTypes.size(); i++)
        {
            int x = baseX+118;
            int y = baseY+(i*22)+120;

            this.addWidget(this.createButton(i, x, y, 18, 18, this.menu.recipeTypes.get(i)));
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY)
    {
        PoseStack pose = graphics.pose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int baseX = (width - imageWidth) / 2;
        int baseY = (height - imageHeight) / 2 - this.menu.recipeTypes.size()*11+11;

        for (int i = 0; i < this.menu.recipeTypes.size(); i++)
        {
            int x = baseX+118;
            int y = baseY+(i*22)+120;

            RecipeData data = this.menu.recipeTypes.get(i);
            pose.pushPose();

            if(pMouseX > x && pMouseX < x+18)
                if(pMouseY > y && pMouseY < y+18)
                    graphics.renderComponentTooltip(Minecraft.getInstance().font, List.of(data.nameComponent), pMouseX, pMouseY);

            pose.translate(x, y, 0);

            pose.pushPose();
            pose.scale(0.07f, 0.07f, 0.07f);
            RenderBlitUtil.blit(TEXTURE, pose, 0, 0, 0, 0, 256, 256);
            pose.popPose();

            ItemStack stack = data.getItem().getDefaultInstance();

            graphics.renderItem(stack, 1, 1);
            pose.popPose();
        }

        if(hasSelected)
        {
            int baseI = Math.max(0, 0);
            int iO = 0;
            for (int i = baseI; i < baseI+10; i++)
            {
                iO++;
                if(iO > 10)
                    iO = 9;

                float alpha = 1F;
                if(i == baseI || i == baseI+7)
                    alpha = 0.25f;
                if(i == baseI+1 || i == baseI+8)
                    alpha = 0.5f;
                if(i == baseI+2 || i == baseI+9)
                    alpha = 0.75f;

                try
                {
                    Recipe recipe = recipes.get(i);
                    if(recipe != null)
                    {
                        int x = baseX+140;
                        int y = baseY+(iO*22)+(selectedType*22)+32;

                        pose.pushPose();
                        ItemStack stack = recipe.getResultItem(Minecraft.getInstance().level.registryAccess());

                        pose.pushPose();
                        if(pMouseX > x && pMouseX < x+18)
                            if(pMouseY > y && pMouseY < y+18)
                                graphics.renderComponentTooltip(Minecraft.getInstance().font, List.of(stack.getHoverName()), pMouseX, pMouseY);

                        pose.translate(x, y, 0);

                        pose.pushPose();

                        pose.scale(0.07f, 0.07f, 0.07f);
                        RenderSystem.enableBlend();
                        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
                        RenderBlitUtil.blit(TEXTURE, pose, 0, 0, 0, 0, 256, 256);

                        RenderSystem.disableBlend();
                        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                        pose.popPose();

                        graphics.renderItem(stack, 1, 1);
                        pose.popPose();
                    }
                }
                catch (IndexOutOfBoundsException e)
                {
                    break;
                }
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick)
    {
        //renderBackground(graphics);
        super.render(graphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(graphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY)
    {

    }

    public AbstractButton createButton(int number, int x, int y, int width, int height, RecipeData data)
    {
        return new AbstractButton(x, y, width, height, Component.empty())
        {
            public final int id = number;

            @Override
            protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput)
            {
                defaultButtonNarrationText(pNarrationElementOutput);
            }

            @Override
            public void onPress()
            {
                recipeStuff(ForgeRegistries.RECIPE_TYPES.getValue(data.getKey().location()));
                selectedType = id;
                hasSelected = true;
            }
        };
    }

    public <T extends Recipe<?>> void recipeStuff(RecipeType<T> recipeType)
    {
        RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

        List<Recipe<Container>> recipes = recipeManager.getAllRecipesFor(((RecipeType<Recipe<Container>>) recipeType));
        this.recipes = recipes;


    }
}
