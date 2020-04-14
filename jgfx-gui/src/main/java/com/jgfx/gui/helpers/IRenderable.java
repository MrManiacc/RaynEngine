package com.jgfx.gui.helpers;

import com.jgfx.assets.context.CoreContext;
import com.jgfx.assets.naming.Name;
import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.engine.utils.ShapeUtils;
import com.jgfx.gui.components.display.position.AbsolutePositionCmp;
import com.jgfx.gui.components.display.position.RelativePositionCmp;
import com.jgfx.gui.components.display.render.RenderableCmp;
import com.jgfx.gui.components.display.rotate.RotationCmp;
import com.jgfx.gui.components.display.size.AbsoluteSizeCmp;
import com.jgfx.gui.components.display.size.AbstractSizeCmp;
import com.jgfx.gui.components.display.size.RelativeSizeCmp;
import com.jgfx.gui.elements.AbstractElement;
import com.jgfx.gui.elements.IElement;
import com.jgfx.gui.exception.GuiException;

/**
 * Represents an element that can be rendered
 */
public interface IRenderable<T extends AbstractElement> extends IElement<T> {
    /**
     * This will generate the vao/ get the vao for the model
     */
    default T model(ResourceUrn urn) throws GuiException {
        if (!CoreContext.has(ShapeUtils.class)) {
            throw new GuiException("Failed to find shape utils in core context!");
        }
        var shapes = CoreContext.get(ShapeUtils.class);
        if (!shapes.has(urn)) {
            logger().warn("Failed to find shape {} in shape utils", urn);
            return cast();
        }
        get(RenderableCmp.class).ifPresentOrElse(renderableCmp -> renderableCmp.setVao(shapes.get(urn)), () -> {
            var renderableCmp = new RenderableCmp(cast());
            renderableCmp.setVao(shapes.get(urn));
            add(renderableCmp);
        });
        return cast();
    }

    /**
     * Creates a model with the given shape name
     */
    default T model(Name shapeName) throws GuiException {
        return model(new ResourceUrn(new Name("engine"), new Name("shapes"), shapeName));
    }

    /**
     * This will translate the model
     */
    default T absOffset(float x, float y) {
        get(AbsolutePositionCmp.class).ifPresentOrElse(positionCmp -> {
            positionCmp.setX(x);
            positionCmp.setY(y);
        }, () -> {
            var positionCmp = new AbsolutePositionCmp(cast());
            positionCmp.setX(x);
            positionCmp.setY(y);
            add(positionCmp);//add before model so the component isn't reinitialized
            try {
                model(new Name("quad"));
            } catch (GuiException e) {
                /*Ignored*/
                logger().warn("Attempted to set model to quad for new renderable");
            }
        });
        return cast();
    }

    /**
     * This will translate the model
     */
    default T relOffset(float x, float y) {
        get(RelativePositionCmp.class).ifPresentOrElse(positionCmp -> {
            positionCmp.setX(x);
            positionCmp.setY(y);
        }, () -> {
            var positionCmp = new RelativePositionCmp(cast());
            positionCmp.setX(x);
            positionCmp.setY(y);
            add(positionCmp);//add before model so the component isn't reinitialized
            try {
                model(new Name("quad"));
            } catch (GuiException e) {
                /*Ignored*/
                logger().warn("Attempted to set model to quad for new renderable");
            }
        });
        return cast();
    }


    /**
     * This will scale the element
     */
    default T absScale(float width, float height) {
        get(AbsoluteSizeCmp.class).ifPresentOrElse(sizeCmp -> {
            sizeCmp.setWidth(width);
            sizeCmp.setHeight(height);
        }, () -> {
            var sizeCmp = new AbsoluteSizeCmp(cast());
            sizeCmp.setWidth(width);
            sizeCmp.setHeight(height);

            add(sizeCmp);//add before model so the component isn't reinitialized
            try {
                model(new Name("quad"));
            } catch (GuiException e) {
                /*Ignored*/
                logger().warn("Attempted to set model to quad for new renderable");
            }
        });
        return cast();
    }

    /**
     * This will scale the element
     */
    default T relScale(float width, float height) {
        get(RelativeSizeCmp.class).ifPresentOrElse(relativeSizeCmp -> {
            relativeSizeCmp.setWidth(width);
            relativeSizeCmp.setHeight(height);
        }, () -> {
            var relativeSizeCmp = new RelativeSizeCmp(cast());
            relativeSizeCmp.setWidth(width);
            relativeSizeCmp.setHeight(height);

            add(relativeSizeCmp);//add before model so the component isn't reinitialized
            try {
                model(new Name("quad"));
            } catch (GuiException e) {
                /*Ignored*/
                logger().warn("Attempted to set model to quad for new renderable");
            }
        });
        return cast();
    }

    /**
     * This will scale the element
     */
    default T rotate(float angle) {
        get(RotationCmp.class).ifPresentOrElse(rotationCmp -> {
            rotationCmp.setAngle(angle);
        }, () -> {
            var rotationCmp = new RotationCmp(cast());
            rotationCmp.setAngle(angle);
            add(rotationCmp);//add before model so the component isn't reinitialized
            try {
                model(new Name("quad"));
            } catch (GuiException e) {
                /*Ignored*/
                logger().warn("Attempted to set model to quad for new renderable");
            }
        });
        return cast();
    }
}
