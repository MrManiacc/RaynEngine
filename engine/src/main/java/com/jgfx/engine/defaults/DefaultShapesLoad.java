package com.jgfx.engine.defaults;

import com.jgfx.assets.context.CoreContext;
import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.engine.assets.model.Vao;
import com.jgfx.engine.load.SingleStepLoadProcess;
import com.jgfx.engine.utils.ShapeUtils;

/**
 * Loads the default shapes
 */
public class DefaultShapesLoad extends SingleStepLoadProcess {
    public DefaultShapesLoad() {
        super("Loading default shapes");
    }

    @Override
    public boolean step() {
        var shapeUtils = CoreContext.put(new ShapeUtils());
        addShapes(shapeUtils);
        return true;
    }

    private void addShapes(ShapeUtils utils) {
        var quadVao = Vao.create(2, Vao.ARRAYS_TRIANGLE_STRIPS);
        //The quad
        {
            quadVao.bind();
            quadVao.createAttribute(new float[]{-1, 1, -1, -1, 1, 1, 1, -1}, 2);
            quadVao.createAttribute(new float[]{
                    0, 0,
                    1, 0,
                    0, 1,
                    1, 1},
                    2);
            quadVao.unbind();
            utils.add(new ResourceUrn("engine", "shapes", "quad"), quadVao);
        }
    }
}
