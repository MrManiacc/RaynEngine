package com.jgfx.gui.systems;

import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.ecs.group.Group;
import com.jgfx.engine.game.AutoRegister;
import com.jgfx.engine.injection.anotations.All;
import com.jgfx.engine.injection.anotations.In;
import com.jgfx.engine.injection.anotations.One;
import com.jgfx.engine.time.EngineTime;
import com.jgfx.engine.window.IWindow;
import com.jgfx.gui.components.common.AlignmentCmp;
import com.jgfx.gui.components.container.ContainerCmp;
import com.jgfx.gui.components.container.SpacingCmp;
import com.jgfx.gui.components.display.render.RenderableCmp;
import com.jgfx.gui.components.display.size.AbsoluteSizeCmp;
import com.jgfx.gui.components.display.size.BoundsCmp;
import com.jgfx.gui.components.display.size.RelativeSizeCmp;
import com.jgfx.gui.components.image.ImageCmp;
import com.jgfx.gui.elements.IElement;
import com.jgfx.gui.elements.containers.Root;
import com.jgfx.gui.elements.containers.stack.AbstractStack;
import com.jgfx.gui.elements.containers.stack.HStack;
import com.jgfx.gui.elements.containers.stack.VStack;
import com.jgfx.gui.elements.controls.Image;
import com.jgfx.gui.enums.Alignment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;


@AutoRegister
public class TransformResolver extends EntitySystem {
    @In IWindow window;
    @All(ContainerCmp.class) Group containers;
    private static final Logger logger = LogManager.getLogger();

    /**
     * Here we resolve all of the transforms for every shape entity,
     * we find all {@link com.jgfx.gui.elements.containers.stack.AbstractStack} entities,
     * and calculate their matrices along with their children matrices
     */
    @Override
    protected void process(EngineTime time) {
//        containers.forEach(entity -> {
//            var element = entity.get(ContainerCmp.class).element();
//            computeStackPosition((AbstractStack) element);
//            if (element instanceof VStack)
//                computeVStackSize((AbstractStack) element);
//            else if (element instanceof HStack)
//                computeHStackSize((AbstractStack) element);
//            updateRenderComponent((AbstractStack) element);
//        }, entity -> entity.get(ContainerCmp.class).element() instanceof AbstractStack);
//        containers.forEach(entity -> processVStack.accept(entity.get(ContainerCmp.class).element()), entity -> entity.get(ContainerCmp.class).element() instanceof VStack);
    }


    /**
     * This will process all HStacks
     */
    private Consumer<VStack> processVStack = stack -> {
        Optional<AlignmentCmp> alignment = stack.get(AlignmentCmp.class);//this is always present
        Optional<SpacingCmp> spacing = stack.get(SpacingCmp.class);//This is also always present
        //We first compute the position of the stack based on it's alignment
        //Then we need to compute the children sizes
        switch (alignment.get().getAlignment()) {
            //If centered, the bottom element should be in the center of the parent container
            case CENTER:
                break;
        }
    };

    /**
     * This will compute the size of a stack based upon the largest child
     */
    private void computeStackPosition(AbstractStack stack) {
        var parent = stack.container(false);
        var alignment = ((Optional<AlignmentCmp>) stack.get(AlignmentCmp.class)).get();//this is always present
        var bounds = ((Optional<BoundsCmp>) stack.get(BoundsCmp.class)).get();//This is also always present
        if (parent instanceof Root) {
            var parentBounds = ((Root) parent).getBounds();
            computePosition(parentBounds, bounds, alignment);
        } else if (parent instanceof VStack) {
            var parentBounds = (Optional<BoundsCmp>) parent.get(BoundsCmp.class);
            parentBounds.ifPresent(boundsCmp -> computePosition(boundsCmp, bounds, alignment));
        }
    }

    /**
     * This will compute the bounds for a given element
     */
    private void computePosition(BoundsCmp parentBounds, BoundsCmp elementBounds, AlignmentCmp alignment) {
        switch (alignment.getAlignment()) {
            case CENTER: //center means we want it in the center of the screen
                elementBounds.setPosition(parentBounds.getCenterX(), parentBounds.getCenterY());
                break;
            case LEADING: //leading means we want it center on the left side
                elementBounds.setPosition(parentBounds.getX(), parentBounds.getCenterY());
                break;
            case TRAILING://Trailing means on the right side of container
                elementBounds.setPosition(parentBounds.getWidth(), parentBounds.getCenterY());
                break;
        }
    }

    /**
     * This will first compute all of the children's sizes, then it will use the max in each axis,
     * apply the correct padding, margins etc, and compute the correct size
     */
    private BoundsCmp computeHStackSize(AbstractStack stack) {
        var alignment = ((Optional<AlignmentCmp>) stack.get(AlignmentCmp.class)).get();//this is always present
        var bounds = ((Optional<BoundsCmp>) stack.get(BoundsCmp.class)).get();//This is also always present
        var spacing = ((Optional<SpacingCmp>) stack.get(SpacingCmp.class)).get();//This is also always present
        var children = stack.list();
        var maxHeight = 0.0f;
        var totalWidth = spacing.getSpacing();
        for (var child : children) {
            if (child instanceof Image) {
                var childBounds = processImage((Image) child);
                if (childBounds.getHeight() > maxHeight)
                    maxHeight = childBounds.getHeight();
                totalWidth += ((childBounds.getWidth() / 2.0f) + (spacing.getSpacing() / 2.0f));
            }
        }

        //TODO: accommodate for padding/margin
        bounds.setSize(totalWidth - spacing.getSpacing(), maxHeight);
        fixBounds(bounds, alignment);

        var xOffset = bounds.getX() - bounds.getWidth();
        for (var child : children) {
            if (child instanceof Image) {
                var image = (Image) child;
                var childBounds = processImage((Image) child);
                updateRenderComponent(image, xOffset - childBounds.getWidth() / 2.0f, bounds.getY());
                xOffset += (childBounds.getWidth() + spacing.getSpacing()) * 2;
            }
        }
        return bounds;
    }

    /**
     * Fixes anything that may go off of the screen
     */
    private void fixBounds(BoundsCmp bounds, AlignmentCmp alignment) {
        if (alignment.getAlignment() == Alignment.TRAILING || alignment.getAlignment() == Alignment.TOP_TRAILING || alignment.getAlignment() == Alignment.BOTTOM_TRAILING) {
            bounds.setX(bounds.getX() - bounds.getWidth());//fix the element to put it on screen
        } else if (alignment.getAlignment() == Alignment.LEADING || alignment.getAlignment() == Alignment.BOTTOM_LEADING || alignment.getAlignment() == Alignment.TOP_LEADING) {
            bounds.setX(bounds.getX() + bounds.getWidth());
        }
    }

    /**
     * This will first compute all of the children's sizes, then it will use the max in each axis,
     * apply the correct padding, margins etc, and compute the correct size
     */
    private void computeVStackSize(AbstractStack stack) {
        var alignment = ((Optional<AlignmentCmp>) stack.get(AlignmentCmp.class)).get();//this is always present
        var bounds = ((Optional<BoundsCmp>) stack.get(BoundsCmp.class)).get();//This is also always present
        var spacing = ((Optional<SpacingCmp>) stack.get(SpacingCmp.class)).get();//This is also always present
        var children = stack.list();
        var maxWidth = 0.0f;
        var totalHeight = spacing.getSpacing();
        for (var child : children) {
            if (child instanceof Image) {
                var childBounds = processImage((Image) child);
                if (childBounds.getWidth() > maxWidth)
                    maxWidth = childBounds.getWidth();
                totalHeight += ((childBounds.getHeight() / 2.0f) + (spacing.getSpacing() / 2.0f));
            } else if (child instanceof HStack) {
                var childBounds = computeHStackSize((AbstractStack) child);
                if (childBounds.getWidth() > maxWidth)
                    maxWidth = childBounds.getWidth();
                totalHeight += ((childBounds.getHeight() / 2.0f) + (spacing.getSpacing() / 2.0f));
            }
        }

        bounds.setSize(maxWidth, totalHeight - spacing.getSpacing());
        //TODO: accommodate for padding/margin
        fixBounds(bounds, alignment);

        var yOffset = bounds.getY() - bounds.getHeight();
        for (var child : children) {
            if (child instanceof Image) {
                var image = (Image) child;
                var childBounds = processImage((Image) child);
                updateRenderComponent(image, bounds.getX(), yOffset - childBounds.getHeight() / 2.0f);
                yOffset += (childBounds.getHeight() + spacing.getSpacing()) * 2;
            } else if (child instanceof HStack) {
                var hStack = (HStack) child;
                var childBounds = computeHStackSize(hStack);
                yOffset += (childBounds.getHeight() + spacing.getSpacing()) * 2;
                childBounds.setX(bounds.getX() - bounds.getWidth());
                childBounds.setY(yOffset);
                //Recompute children
                computeHStackSize(hStack);
//                updateRenderComponent(hStack, bounds.getX(), yOffset - childBounds.getHeight() / 2.0f);
                updateRenderComponent(hStack);
            }
        }
    }

    /**
     * This will update the render matrix to reflect the bounds
     */
    private void updateRenderComponent(AbstractStack stack) {
        var boundsCmp = ((Optional<BoundsCmp>) stack.get(BoundsCmp.class)).get();//This is also always present
        var renderCmp = ((Optional<RenderableCmp>) stack.get(RenderableCmp.class));//This is also always present
        renderCmp.ifPresent(renderableCmp -> renderableCmp
                .begin()
                .translate(boundsCmp.getX(), boundsCmp.getY())
                .scale(boundsCmp.getWidth(), boundsCmp.getHeight()));
    }

    /**
     * This will update the render component
     * TODO: align in the parent according to the parent's bounds
     */
    private void updateRenderComponent(IElement element, float x, float y) {
        var boundsCmp = ((Optional<BoundsCmp>) element.get(BoundsCmp.class)).get();//This is also always present
        var renderCmp = ((Optional<RenderableCmp>) element.get(RenderableCmp.class));//This is also always present
        renderCmp.ifPresent(renderableCmp -> {
            renderableCmp.begin()
                    .translate(x, y)
                    .scale(boundsCmp.getWidth(), boundsCmp.getHeight());
        });

    }

    /**
     * This will compute the appropriate size for an image
     */
    private BoundsCmp processImage(Image image) {
        var imgCmp = image.get(ImageCmp.class).get();
        var imgWidth = new AtomicReference<>(imgCmp.getWidth());
        var imgHeight = new AtomicReference<>(imgCmp.getHeight());
        //we create/update the matrix for the image
        image.get(AbsoluteSizeCmp.class).ifPresent(sizeCmp -> {
            imgWidth.set(sizeCmp.getWidth());
            imgHeight.set(sizeCmp.getHeight());
        });
        image.get(RelativeSizeCmp.class).ifPresent(sizeCmp -> {
            imgWidth.set(imgWidth.get() * sizeCmp.getWidth());
            imgHeight.set(imgHeight.get() * sizeCmp.getHeight());
        });
        image.get(BoundsCmp.class).ifPresentOrElse(bounds -> {
            bounds.setPosition(0, 0);
            bounds.setSize(imgWidth.get(), imgHeight.get());
        }, () -> {
            var bounds = new BoundsCmp(image);
            bounds.setPosition(0, 0);
            bounds.setSize(imgWidth.get(), imgHeight.get());
            image.add(bounds);
        });
        return image.get(BoundsCmp.class).get();
    }


    /**
     * This will process all HStacks
     */
    private Consumer<HStack> processHStack = stack -> {
        stack.list().forEach(element -> {

        });
    };


    /**
     * This makes us be the first entity system,
     * we need to process the transforms of everything before rendering
     *
     * @return returns -1
     */
    @Override
    public int compareTo(EntitySystem o) {
        return -1;
    }
}
