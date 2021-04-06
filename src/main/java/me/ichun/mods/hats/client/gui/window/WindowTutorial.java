package me.ichun.mods.hats.client.gui.window;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import me.ichun.mods.hats.client.gui.WorkspaceHats;
import me.ichun.mods.hats.client.gui.window.element.ElementHatRender;
import me.ichun.mods.hats.client.gui.window.element.ElementHatsScrollView;
import me.ichun.mods.hats.common.Hats;
import me.ichun.mods.hats.common.hats.HatInfo;
import me.ichun.mods.hats.common.hats.HatResourceHandler;
import me.ichun.mods.hats.common.hats.sort.SortHandler;
import me.ichun.mods.hats.common.world.HatsSavedData;
import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.window.Window;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.*;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class WindowTutorial extends Window<WorkspaceHats>
{
    private static final ResourceLocation TEX_ARROW = new ResourceLocation("textures/gui/container/villager2.png");

    public enum Direction
    {
        LEFT,
        RIGHT
    }

    public int age;

    public Direction direction;
    public int pointX;
    public int pointY;
    public int finalWidth;
    public int finalHeight;

    public WindowTutorial(WorkspaceHats parent, Direction dir, int pointX, int pointY, int width, int height, @Nullable Consumer<Workspace> callback, String text)
    {
        super(parent);

        this.direction = dir;
        this.pointX = pointX;
        this.pointY = pointY;
        this.finalWidth = width;
        this.finalHeight = height;

        this.posX = dir == Direction.LEFT ? pointX + 30 : pointX - 30 - finalWidth;
        this.posY = pointY - finalHeight / 2;

        disableDockingEntirely();
        disableDrag();
        disableTitle();

        setView(new ViewTutorial(this, callback, text));
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTick)
    {
        if(age <= Hats.configClient.guiAnimationTime)
        {
            float prog = (float)Math.sin(Math.toRadians(MathHelper.clamp(((age + partialTick) / Hats.configClient.guiAnimationTime), 0F, 1F) * 90F));

            width = (int)(finalWidth * prog);
            height = (int)(finalHeight * prog);
            this.resize(parent.getMinecraft(), parent.getWidth(), parent.getHeight());
        }

        RenderSystem.pushMatrix();
        RenderSystem.translatef(0F, 0F, 300F);

        super.render(stack, mouseX, mouseY, partialTick);

        //This is called after end scissor
        if(age > Hats.configClient.guiAnimationTime / 2)
        {
            bindTexture(TEX_ARROW);
            if(direction == Direction.LEFT)
            {
                RenderHelper.draw(stack, pointX + 2, pointY - 4, 10, 9, 0, 25/512F, 15/512F, 171/256F, 180/256F);
            }
            else
            {
                RenderHelper.draw(stack, pointX - 2 - 10, pointY - 4, 10, 9, 0, 15/512F, 25/512F, 171/256F, 180/256F);
            }
        }

        RenderSystem.popMatrix();
    }

    @Override
    public void tick()
    {
        super.tick();

        age++;

        if(age == Hats.configClient.guiAnimationTime + 1) //set the constraint and resize
        {
            width = finalWidth;
            height = finalHeight;
            this.resize(parent.getMinecraft(), parent.getWidth(), parent.getHeight());
        }
    }

    public static class ViewTutorial extends View<WindowTutorial>
    {
        public ViewTutorial(@Nonnull WindowTutorial parent, @Nullable Consumer<Workspace> callback, String text)
        {
            super(parent, "hats.gui.tutorial.title");

            int padding = 3;

            ElementTextWrapper text1 = new ElementTextWrapper(this);
            text1.setText(text);
            text1.setConstraint(new Constraint(text1).top(this, Constraint.Property.Type.TOP, padding).bottom(this, Constraint.Property.Type.BOTTOM, 30));
            elements.add(text1);

            ElementButton<?> button = new ElementButton<>(this, I18n.format("gui.ok"), elementClickable ->
            {
                parent.parent.removeWindow(parent);

                if(callback != null)
                {
                    callback.accept(parent.parent);
                }
            });
            button.setSize(60, 20);
            button.constraints().bottom(this, Constraint.Property.Type.BOTTOM, padding);
            elements.add(button);
        }
    }
}