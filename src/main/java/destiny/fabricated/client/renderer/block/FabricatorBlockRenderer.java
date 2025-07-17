package destiny.fabricated.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import destiny.fabricated.block_entities.FabricatorBlockEntity;
import destiny.fabricated.blocks.FabricatorBlock;
import destiny.fabricated.client.model.block.FabricatorModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class FabricatorBlockRenderer extends GeoBlockRenderer<FabricatorBlockEntity>
{
    private ItemRenderer itemRenderer;
    private int fabricatingTicker = 0;

    public FabricatorBlockRenderer()
    {
        super(new FabricatorModel());
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void actuallyRender(PoseStack poseStack, FabricatorBlockEntity fabricator, BakedGeoModel model,
                               RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                               boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red,
                               float green, float blue, float alpha)
    {
        super.actuallyRender(poseStack, fabricator, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        if(fabricator.getBlockState().getValue(FabricatorBlock.STATE).equals(FabricatorBlock.FabricatorState.FABRICATING))
            fabricatingTicker++;
        if(fabricatingTicker > 62.5)
            fabricatingTicker = 0;

        if(fabricator.craftStack.isEmpty())
            return;

        poseStack.pushPose();

        float animTime = fabricator.fabricatingTicker+partialTick;

        float timeUp = 12.5f;
        float timeDown = 50f;
        float speedUp = 0.328125f / timeUp;
        float speedDown = 0.328125f / timeDown;

        poseStack.translate(0, 0.4, 0.09375);
        if(fabricator.fabricatingTicker <= timeUp)
        {
            poseStack.translate(0, speedUp * animTime, 0);
        }

        if(fabricator.fabricatingTicker > timeUp && fabricator.fabricatingTicker <= timeDown)
        {
            poseStack.translate(0, (speedUp * 12.5f)-(speedDown*animTime), 0);
        }

        if(!this.itemRenderer.getModel(fabricator.craftStack, fabricator.getLevel(), null, 0).isGui3d())
        {
            poseStack.translate(0,0.02, 0);
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
        }
        else poseStack.translate(0, 0.125, 0);

        poseStack.scale(0.5f, 0.5f, 0.5f);

        if(fabricator.fabricatingTicker > timeUp && fabricator.fabricatingTicker <= timeDown)
            this.itemRenderer.renderStatic(fabricator.craftStack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, fabricator.getLevel(), 0);

        poseStack.popPose();


    }
}
