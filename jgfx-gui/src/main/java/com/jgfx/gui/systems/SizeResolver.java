package com.jgfx.gui.systems;

import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AtomicDouble;
import com.jgfx.engine.ecs.entity.system.EntitySystem;
import com.jgfx.engine.game.AutoRegister;
import com.jgfx.engine.injection.anotations.EventSubscriber;
import com.jgfx.engine.injection.anotations.In;
import com.jgfx.gui.components.common.AlignmentCmp;
import com.jgfx.gui.components.container.SpacingCmp;
import com.jgfx.gui.components.display.render.RenderableCmp;
import com.jgfx.gui.components.display.rotate.RotationCmp;
import com.jgfx.gui.components.display.size.AbsoluteSizeCmp;
import com.jgfx.gui.components.display.size.BoundsCmp;
import com.jgfx.gui.components.display.size.RelativeSizeCmp;
import com.jgfx.gui.components.image.ImageCmp;
import com.jgfx.gui.elements.IElement;
import com.jgfx.gui.elements.containers.IContainer;
import com.jgfx.gui.elements.containers.Root;
import com.jgfx.gui.elements.containers.stack.AbstractStack;
import com.jgfx.gui.elements.containers.stack.HStack;
import com.jgfx.gui.elements.containers.stack.VStack;
import com.jgfx.gui.elements.controls.Image;
import com.jgfx.gui.enums.Alignment;
import com.jgfx.gui.events.ElementUpdateEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.jgfx.engine.event.Bus.GUI;

@AutoRegister
@EventSubscriber(GUI)
public class SizeResolver extends EntitySystem {

    private static final Logger logger = LogManager.getLogger(SizeResolver.class);
    @In
    Root root;

    @Subscribe
    public void onElementUpdate(ElementUpdateEvent event) {
        root.list().forEach(computeSize);
        root.list().forEach(this::computePosition);
        root.list().forEach(this::updateRenderComponent);
    }

    private void computePosition(IElement<?> element) {
        var bounds = (Optional<BoundsCmp>) element.get(BoundsCmp.class);
        if (bounds.isEmpty()) {
            logger.error("Failed to find bounds for element {} " + element.name());
        } else {
            //THis should be computed by this point
            var width = bounds.get().getWidth();
            var height = bounds.get().getHeight();
            var alignment = (Optional<AlignmentCmp>) element.get(AlignmentCmp.class);
            var parent = element.container();
            var parentBounds = parent.entity().get(BoundsCmp.class);
            if (parent instanceof Root)
                parentBounds = ((Root) parent).getBounds();
            if (parentBounds != null) {
                computeAlignedPosition(parent, element, parentBounds, bounds.get(), alignment.isPresent() ? alignment.get().getAlignment() : Alignment.CENTER);
                logger.debug("found bounds with {}, by {} for {}, with parent bounds {}", width, height, element.name(), parentBounds.dataString());
            }
        }
        computeAlignment(element);
        if (element instanceof AbstractStack) {
            var stack = (AbstractStack) element;
            stack.list().forEach(e -> this.computePosition((IElement<?>) e));
            stack.sort(); //we recompute the position after sorting
        }
    }

    /**
     * This will compute the x position for a compute
     */
    private void computeAlignedPosition(IContainer<?> parent, IElement<?> self, BoundsCmp parentBounds, BoundsCmp selfBounds, Alignment alignment) {
        //At this point the self should have ht proper width regardless of the type.
        //The parent should also have the proper width. So now we just use the parentBounds, and compute the position based upon the alignment
        var spacingCmp = parent.get(SpacingCmp.class);
        var spacing = spacingCmp.isEmpty() ? 0 : spacingCmp.get().getSpacing();
        if (parent instanceof HStack) {
            var index = parent.findIndex(self);
            float xPos = 0;
            if (index == 0)
                xPos = parentBounds.getX();
            else {
                var previous = parent.list().get(index - 1);
                var previousBounds = (BoundsCmp) previous.get(BoundsCmp.class).get();
                xPos = previousBounds.getX() + (previousBounds.getWidth() * 2) + spacing;
            }
            selfBounds.setX(xPos);
            selfBounds.setY(parentBounds.getY());
//            switch (alignment) {
//                case TOP_LEADING:
//                    selfBounds.setY(parentBounds.getY());
//                    selfBounds.setX(xPos);
//                    break;
//            }

        } else if (parent instanceof VStack) {
            var index = parent.findIndex(self);
            float ypos;
            if (index == 0) {
                ypos = parentBounds.getY();
            } else {
                var previous = parent.list().get(index - 1);
                var previousBounds = (BoundsCmp) previous.get(BoundsCmp.class).get();
                ypos = previousBounds.getY() + (previousBounds.getHeight() * 2);
            }
            switch (alignment) {
                case TOP_LEADING:
                    selfBounds.setY(ypos);
                    selfBounds.setX(parentBounds.getX());
                    break;
                case LEADING:
                    selfBounds.setY(ypos + (spacing / 2.0f));
                    selfBounds.setX(parentBounds.getX());
                    break;
                case BOTTOM_LEADING:
                    selfBounds.setX(parentBounds.getX());
                    selfBounds.setY(ypos + spacing);
                    break;
                case CENTER:
                    selfBounds.setY(ypos + (spacing / 2.0f));
                    selfBounds.setX((parentBounds.getCenterX() - (selfBounds.getWidth())));
                    break;
                case TOP:
                    selfBounds.setY(ypos);
                    selfBounds.setX(parentBounds.getCenterX() - (selfBounds.getWidth()));
                    break;
                case BOTTOM:
                    selfBounds.setX(parentBounds.getCenterX() - (selfBounds.getWidth()));
                    selfBounds.setY(ypos + spacing);
                    break;
                case TRAILING:
                    selfBounds.setY(ypos + (spacing / 2.0f));
                    selfBounds.setX(parentBounds.getCenterX() + parentBounds.getWidth() - selfBounds.getWidth() * 2 - spacing);
                    break;
                case TOP_TRAILING:
                    selfBounds.setY(ypos);
                    selfBounds.setX(parentBounds.getCenterX());
                    break;
                case BOTTOM_TRAILING:
                    selfBounds.setX(parentBounds.getCenterX());
                    selfBounds.setY(ypos + spacing);
                    break;
            }
        }
    }


    /**
     * This will take an element, and find it's alignment and compute the correct position based on the parent
     */
    private void computeAlignment(IElement<?> element) {
        var parent = element.container();
        var selfBounds = element.entity().get(BoundsCmp.class);
        BoundsCmp parentBounds;
        BoundsCmp previousBounds = null;
        float space = 0;
        if (parent instanceof Root) {
            parentBounds = ((Root) parent).getBounds();
            if (element.entity().has(SpacingCmp.class))
                space = element.entity().get(SpacingCmp.class).getSpacing();
        } else {
            parentBounds = parent.entity().get(BoundsCmp.class);
            if (parent.entity().has(SpacingCmp.class))
                space = parent.entity().get(SpacingCmp.class).getSpacing();
            var index = parent.findIndex(element);
            if (index != 0) {
                var previous = (IElement<?>) parent.list().get(index - 1);
                if (previous != null && previous.entity() != null) {
                    if (previous.entity().has(BoundsCmp.class))
                        previousBounds = previous.entity().get(BoundsCmp.class);
                }
            }
        }
        var x = previousBounds == null ? parentBounds.getX() : previousBounds.getX();
        var cx = previousBounds == null ? parentBounds.getCenterX() : previousBounds.getCenterX();
        var w = previousBounds == null ? parentBounds.getWidth() : previousBounds.getWidth() * 2;
        var y = previousBounds == null ? parentBounds.getX() : previousBounds.getY();
        var cy = previousBounds == null ? parentBounds.getCenterY() : previousBounds.getCenterY();
        var h = previousBounds == null ? parentBounds.getHeight() : previousBounds.getHeight() * 2;
        var count = (element instanceof IContainer) ? ((IContainer) element).count() : 0;
        //TODO: This will only work for a VStack ATM
        logger.info(selfBounds == null);
        if (selfBounds != null && element instanceof VStack) {
            float spacing = space;
            element.entity().ifPresent(AlignmentCmp.class, align -> {
                switch (align.getAlignment()) {
                    case TOP:
                        selfBounds.setX(cx - selfBounds.getWidth());
                        selfBounds.setY(parentBounds.getY());
                        break;
                    case CENTER:
                        selfBounds.setX(cx - selfBounds.getWidth());
                        selfBounds.setY((parentBounds.getY() + (parentBounds.getHeight())) - (selfBounds.getHeight()));
                        break;
                    case BOTTOM:
                        selfBounds.setX(cx - selfBounds.getWidth());
                        selfBounds.setY(cy + selfBounds.getHeight() / 2.0f);
                        break;
                    case LEADING:
                        selfBounds.setX(parentBounds.getX());
                        selfBounds.setY((parentBounds.getY() + (parentBounds.getHeight())) - (selfBounds.getHeight()));
                        break;
                    case TOP_LEADING:
                        selfBounds.setX(parentBounds.getX());
                        selfBounds.setY(parentBounds.getY());
                        break;
                    case BOTTOM_LEADING:
                        selfBounds.setX(parentBounds.getX());
                        selfBounds.setY(cy + selfBounds.getHeight() / 2.0f);
                        break;
                    case TRAILING:
                        selfBounds.setX((parentBounds.getX() + (parentBounds.getWidth() * 2)) - (selfBounds.getWidth() * 2));
                        selfBounds.setY((parentBounds.getY() + (parentBounds.getHeight())) - (selfBounds.getHeight()));
                        break;
                    case TOP_TRAILING:
                        selfBounds.setX((parentBounds.getX() + (parentBounds.getWidth() * 2)) - (selfBounds.getWidth() * 2));
                        selfBounds.setY(parentBounds.getY());
                        break;
                    case BOTTOM_TRAILING:
                        selfBounds.setX((parentBounds.getX() + (parentBounds.getWidth() * 2)) - (selfBounds.getWidth() * 2));
                        selfBounds.setY(cy + selfBounds.getHeight() / 2.0f);
                        break;
                }
            });
            updateRenderComponent(element);
            logger.info("Computing alignment for {} with new bounds {}", element.name(), selfBounds.dataString());
        } else if (selfBounds != null && element instanceof HStack) {
            float spacing = space;
            float selfSpacing = element.entity().has(SpacingCmp.class) ? element.entity().get(SpacingCmp.class).getSpacing() : 0;
            element.entity().ifPresent(AlignmentCmp.class, align -> {
                switch (align.getAlignment()) {
                    case TOP_LEADING:
                        selfBounds.setX(x);
                        selfBounds.setY(y + h);
                        break;
                    case LEADING:
                        selfBounds.setX(x);
                        selfBounds.setY(y + h + spacing / 2.0f);
                        break;
                    case BOTTOM_LEADING:
                        selfBounds.setX(x);
                        selfBounds.setY(y + h + spacing);
                        break;
                    case CENTER:
                        selfBounds.setY(y + h + spacing / 2.0f);
                        selfBounds.setX(parentBounds.getCenterX() - (selfBounds.getWidth()) + selfSpacing);
                        break;
                    case TOP:
                        selfBounds.setY(y + h);
                        selfBounds.setX(parentBounds.getCenterX() - (selfBounds.getWidth()) + selfSpacing);
                        break;
                    case BOTTOM:
                        selfBounds.setY(y + h + spacing);
                        selfBounds.setX(parentBounds.getCenterX() - (selfBounds.getWidth()) + selfSpacing);
                        break;
                    case TRAILING:
                        selfBounds.setY(y + h + spacing / 2.0f);
                        selfBounds.setX(parentBounds.getCenterX() + parentBounds.getWidth());
                        break;
                    case TOP_TRAILING:
                        selfBounds.setY(y + h);
                        selfBounds.setX(parentBounds.getCenterX() + parentBounds.getWidth());
                        break;
                    case BOTTOM_TRAILING:
                        selfBounds.setX(parentBounds.getCenterX() + parentBounds.getWidth());
                        selfBounds.setY(y + h + spacing);
                        break;
                }
            });
        }

    }

    /**
     * This will take any element and recursively compute it's size
     */
    private Consumer<IElement> computeSize = element -> {
        var bounds = computeBounds(element);
        if (element instanceof HStack && bounds != null) {
            var stack = (HStack) element;
            var width = new AtomicReference<>(findTotalWidth(new AtomicDouble(0), stack.list()));
            var added = stack.entity().has(SpacingCmp.class) ? stack.entity().get(SpacingCmp.class).getSpacing() : 0;
            bounds.setWidth(width.get() + added / 2.0f);
        } else if (element instanceof VStack && bounds != null) {
            var stack = (VStack) element;
            var width = new AtomicReference<>(findTotalWidth(new AtomicDouble(0), stack.list()));
            var height = new AtomicReference<>(findTotalHeight(new AtomicDouble(0), stack.list()));
            bounds.setHeight(height.get());
            var added = stack.entity().has(SpacingCmp.class) ? stack.entity().get(SpacingCmp.class).getSpacing() : 0;
            bounds.setWidth(width.get() + added / 2.0f);
        }
        if (element instanceof IContainer)
            ((IContainer) element).list().forEach(this::acceptSize);
        if (bounds != null) {
            logger.info("Resolved size of element {}, with the data of-> {{}}", element.name(), bounds.dataString());
        }
    };


    /**
     * This will update the render matrix to reflect the bounds
     */
    private void updateRenderComponent(IElement element) {
        var boundsCmp = ((Optional<BoundsCmp>) element.get(BoundsCmp.class)).get();//This is also always present
        var renderCmp = ((Optional<RenderableCmp>) element.get(RenderableCmp.class));//This is also always present
        var rotationCmp = ((Optional<RotationCmp>) element.get(RotationCmp.class));//This is also always present
        var spacingCmp = ((Optional<SpacingCmp>) element.get(SpacingCmp.class));//This is also always present
        renderCmp.ifPresent(renderableCmp -> {
                    renderableCmp
                            .begin()
                            .translate(boundsCmp.getX() + boundsCmp.getWidth(), boundsCmp.getY() + boundsCmp.getHeight())
                            .scale(boundsCmp.getWidth(), boundsCmp.getHeight())
                            .rotate((float) Math.toRadians(90));//Images are flipped by 90 degrees for some reason...
                    rotationCmp.ifPresent(cmp -> renderableCmp.rotate((float) Math.toRadians(cmp.getAngle())));
                }

        );
        if (element instanceof IContainer<?>)
            ((IContainer<?>) element).list().forEach(this::updateRenderComponent);
    }

    /**
     * Recursively will compute the bounds for any element
     */
    private BoundsCmp computeBounds(IElement element) {
        if (element.is(Image.class))
            return computeImageBounds((Image) element);
        else if (element instanceof AbstractStack) {
            var stack = (AbstractStack) element;
            if (!stack.has(BoundsCmp.class))
                stack.add(new BoundsCmp(stack));
            //This will find the max width of a list of children
            var maxWidth = findMaxWidth(new AtomicDouble(0), stack.list());
            var maxHeight = findMaxHeight(new AtomicDouble(0), stack.list());
            var bounds = (BoundsCmp) stack.get(BoundsCmp.class).get();
            //We want the maximum possible width and height first
            bounds.setWidth(maxWidth);
            bounds.setHeight(maxHeight);
            return bounds;
        }
        return null;
    }

    /**
     * @return recursively find the largest width out of all of the children, and outputs the window to the input
     */
    private float findMaxHeight(AtomicDouble currentMax, List<IElement> children) {
        for (var element : children) {
            //If the element isn't a container we don't need recursion
            if (!element.is(IContainer.class)) {
                if (element.is(Image.class)) {
                    var bounds = computeImageBounds((Image) element);
                    if (bounds != null)
                        if (bounds.getHeight() > currentMax.get())
                            currentMax.set(bounds.getHeight());
                }
            } else {
                var container = ((IContainer) element);
                return findMaxHeight(currentMax, container.list());
            }
        }
        return (float) currentMax.get();
    }

    /**
     * @return recursively find the largest width out of all of the children, and outputs the window to the input
     */
    private float findMaxWidth(AtomicDouble currentMax, List<IElement> children) {
        for (var element : children) {
            //If the element isn't a container we don't need recursion
            if (!element.is(IContainer.class)) {
                if (element.is(Image.class)) {
                    var bounds = computeImageBounds((Image) element);
                    if (bounds.getWidth() > currentMax.get())
                        currentMax.set(bounds.getWidth());
                }
            } else {
                var container = ((IContainer) element);
                return findMaxWidth(currentMax, container.list());
            }
        }
        return (float) currentMax.get();
    }


    /**
     * @return will recursively return the width of all the children
     */
    private float findTotalHeight(AtomicDouble currentHeight, List<IElement> children) {
        for (var child : children) {
            if (!(child instanceof IContainer)) {
                if (child.has(BoundsCmp.class)) {
                    var spacing = child.container().entity().has(SpacingCmp.class) && child.container() instanceof VStack ? child.container().entity().get(SpacingCmp.class).getSpacing() : 0;
                    var bounds = (BoundsCmp) child.get(BoundsCmp.class).get();
                    currentHeight.set(currentHeight.get() + bounds.getHeight() + spacing / 2.0f);
                }
            } else {
                var container = (IContainer) child;
                if (container instanceof VStack) {
                    var added = findTotalHeight(new AtomicDouble(0), container.list());
                    currentHeight.set(currentHeight.get() + added);
                } else if (container instanceof HStack) {
                    var added = findMaxHeight(new AtomicDouble(0), container.list());
                    currentHeight.set(currentHeight.get() + added);
                }
            }
        }
        return (float) currentHeight.get();
    }

    /**
     * @return will recursively return the width of all the children
     */
    private float findTotalWidth(AtomicDouble currentWidth, List<IElement> children) {
        for (var child : children) {
            if (!(child instanceof IContainer)) {
                if (child.has(BoundsCmp.class) && child.container() instanceof HStack) {
                    var spacing = child.container().entity().has(SpacingCmp.class) && child.container() instanceof HStack ? child.container().entity().get(SpacingCmp.class).getSpacing() : 0;
                    var bounds = (BoundsCmp) child.get(BoundsCmp.class).get();
                    currentWidth.set(currentWidth.get() + bounds.getWidth() + spacing / 2.0f);
                }
            } else {
                var container = (IContainer) child;
                var added = findTotalWidth(new AtomicDouble(0), container.list());
                System.out.println(added + " : " + container.name());
                currentWidth.set(currentWidth.get() + added);
            }
        }
        return (float) currentWidth.get();
    }


    /**
     * We won't worry about the parents size here, we'll just compute the relative variables.
     * The parent container, will do the magic of computing the actual size, so we just need to return
     * the "localized" size. without referencing anything else here
     *
     * @return returns the computed bounds for an image, based upon it's components
     */
    private BoundsCmp computeImageBounds(Image image) {
        Consumer<BoundsCmp> updateBounds = bounds -> {
            var width = image.get(ImageCmp.class).get().getWidth();//TODO: come up with a better non hard coded solution for this
            var height = image.get(ImageCmp.class).get().getHeight();

            if (image.has(AbsoluteSizeCmp.class)) {
                width = image.get(AbsoluteSizeCmp.class).get().getWidth();
                height = image.get(AbsoluteSizeCmp.class).get().getHeight();
            }
            if (image.has(RelativeSizeCmp.class)) {
                width *= image.get(RelativeSizeCmp.class).get().getWidth();
                height *= image.get(RelativeSizeCmp.class).get().getHeight();
            }
            bounds.setWidth(width);
            bounds.setHeight(height);
            //We may want other parameters to tweak the  size properties
            //TODO: here we compute the actual bounds of the image. It will automatically be returned
        };

        image.entity().ifPresentOrElse(BoundsCmp.class, updateBounds, () -> {
            var bounds = new BoundsCmp(image);
            image.addDirect(bounds);
            updateBounds.accept(bounds);
        });
        return image.entity().get(BoundsCmp.class);
    }


    private void acceptSize(Object e) {
        computeSize.accept((IElement) e);
    }
}
