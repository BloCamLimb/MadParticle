package cn.ussshenzhou.madparticle.designer.universal.widegt;

import cn.ussshenzhou.madparticle.designer.universal.util.Vec2i;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;

import java.util.HashMap;

/**
 * @author USS_Shenzhou
 */
public class TScrollPanel extends TPanel {
    private final HashMap<TWidget, Vec2i> originalPos = new HashMap<>();
    private double prevScrollAmount = -1;
    private double scrollAmount = 0;
    private int bottomY = 0;
    private static int speedFactor = 6;
    private int scrollbarGap = 0;

    public TScrollPanel() {
        super();
    }

    @Override
    public void add(TWidget child) {
        super.add(child);
    }

    @Override
    public void layout() {
        initPos();
        prevScrollAmount = -1;
        super.layout();
    }

    private void initPos() {
        for (TWidget tWidget : children) {
            int y = tWidget.getY() + tWidget.getSize().y;
            if (bottomY < y) {
                bottomY = y;
            }
            originalPos.put(tWidget, new Vec2i(tWidget.getX(), tWidget.getY()));
        }
    }

    private void reLayout() {
        for (TWidget tWidget : children) {
            tWidget.setAbsBounds(originalPos.get(tWidget).x, (int) (originalPos.get(tWidget).y - scrollAmount), tWidget.getSize());
            if (tWidget.getY() < this.getY() || tWidget.getY() + tWidget.getSize().y > this.getY() + this.height) {
                tWidget.setVisible(false);
            } else {
                tWidget.setVisible(true);
            }
        }
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pPoseStack, pMouseX, pMouseY, pPartialTick);
        renderScrollBar();
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    public void renderBackground(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick){
        if(getMaxScroll()>0){
            fill(pPoseStack, x, y, x + width - getScrollbarGap() - 6, y + height, 0x80000000);
        } else {
            fill(pPoseStack, x, y, x + width, y + height, 0x80000000);
        }
    }

    /**
     * modified from
     *
     * @see net.minecraft.client.gui.components.AbstractSelectionList#render(PoseStack, int, int, float)
     */
    private void renderScrollBar() {
        int k1 = this.getMaxScroll();
        if (k1 > 0) {
            int i = getScrollBarX();
            int j = i + 6;
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.getBuilder();
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            int l1 = (int) ((float) (height * height) / (float) bottomY);
            l1 = Mth.clamp(l1, 32, getY() + height - getY() - 8);
            int i2 = (int) this.getScrollAmount() * (getY() + height - getY() - l1) / k1 + getY();
            if (i2 < getY()) {
                i2 = getY();
            }

            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            bufferbuilder.vertex(i, getY() + height, 0.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(j, getY() + height, 0.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(j, getY(), 0.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(i, getY(), 0.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.vertex(i, (i2 + l1), 0.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.vertex(j, (i2 + l1), 0.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.vertex(j, i2, 0.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.vertex(i, i2, 0.0D).color(128, 128, 128, 255).endVertex();
            bufferbuilder.vertex(i, (i2 + l1 - 1), 0.0D).color(192, 192, 192, 255).endVertex();
            bufferbuilder.vertex((j - 1), (i2 + l1 - 1), 0.0D).color(192, 192, 192, 255).endVertex();
            bufferbuilder.vertex((j - 1), i2, 0.0D).color(192, 192, 192, 255).endVertex();
            bufferbuilder.vertex(i, i2, 0.0D).color(192, 192, 192, 255).endVertex();
            tesselator.end();
        }
    }

    private int getScrollBarX() {
        return this.getX() + width - 6;
    }

    public int getUsableWidth() {
        return width - 6 - scrollbarGap;
    }

    @SuppressWarnings("AlibabaAvoidDoubleOrFloatEqualCompare")
    @Override
    public void tick() {
        if (scrollAmount != prevScrollAmount) {
            reLayout();
            prevScrollAmount = scrollAmount;
        }
        super.tick();
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (isInRange(pMouseX, pMouseY, scrollbarGap, scrollbarGap)) {
            if (pMouseX > getScrollBarX() - scrollbarGap - 6) {
                double d0 = Math.max(1, this.getMaxScroll());
                int j = Mth.clamp((int) ((float) (height * height) / (float) bottomY), 32, height);
                double d1 = Math.max(1.0D, d0 / (double) (height - j));
                this.setScrollAmount(-pDragY * d1 / speedFactor);
            } else {
                this.setScrollAmount(pDragY / speedFactor);
                return true;
            }
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (isInRange(pMouseX, pMouseY)) {
            this.setScrollAmount(pDelta);
            return true;
        }
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    public int getMaxScroll() {
        return Math.max(0, bottomY - getY() - getHeight());
    }

    public double getScrollAmount() {
        return scrollAmount;
    }

    public void setScrollAmount(double deltaScroll) {
        deltaScroll = -deltaScroll * speedFactor;
        this.scrollAmount = Mth.clamp(scrollAmount + deltaScroll, 0.0D, this.getMaxScroll());
    }

    public int getScrollbarGap() {
        return scrollbarGap;
    }

    public void setScrollbarGap(int scrollbarGap) {
        this.scrollbarGap = scrollbarGap;
    }
}