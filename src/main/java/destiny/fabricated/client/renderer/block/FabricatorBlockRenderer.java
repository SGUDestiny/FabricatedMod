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

        if(fabricator.craftStack.isEmpty())
            return;

        poseStack.pushPose();

        float animTime = fabricator.fabricatingTicker+partialTick;

        float timeUp = 15f;
        float timeDown = 62.5f;
        float speedUp = 0.328125f / timeUp;
        float speedDown = 0.328125f / (timeDown-timeUp);

        poseStack.translate(0, 0.3125, 0.09375);
        if(animTime <= timeUp)
        {
            poseStack.translate(0, speedUp * animTime, 0);
        }

        if(animTime > timeUp && animTime <= timeDown)
        {
            poseStack.translate(0, (speedUp * timeUp)-(speedDown*(animTime-timeUp)), 0);
        }

        if(!this.itemRenderer.getModel(fabricator.craftStack, fabricator.getLevel(), null, 0).isGui3d())
        {
            poseStack.translate(0,0.02, 0);
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
        }
        else poseStack.translate(0, 0.125, 0);

        poseStack.scale(0.5f, 0.5f, 0.5f);

        if(animTime > timeUp && animTime <= timeDown)
            this.itemRenderer.renderStatic(fabricator.craftStack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, fabricator.getLevel(), 0);

        poseStack.popPose();


    }
}
