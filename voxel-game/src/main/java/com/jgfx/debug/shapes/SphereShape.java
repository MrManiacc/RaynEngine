package com.jgfx.debug.shapes;

import com.jgfx.engine.assets.model.Vao;
import com.jgfx.engine.assets.shader.Shader;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.util.par.ParShapes.*;

/**
 * This will draw a sphere at the given position
 */
public class SphereShape extends IShape {
    @Setter private Vector3f position;
    private final float radius;
    private Vao model;
    private final Matrix4f modelMatrix;

    public SphereShape(Vector3f position, Vector4f color, float radius) {
        super(color);
        this.position = position;
        this.radius = radius;
        this.modelMatrix = new Matrix4f();
        wireframe = true;
    }

    /**
     * This will build the sphere shape
     *
     * @param multi
     */
    @Override
    protected void build(boolean multi) {
        var mesh = par_shapes_create_parametric_sphere(16, 32);
        if (mesh != null) {
            par_shapes_scale(mesh, radius, radius, radius);
            var vc = mesh.npoints();
            var points = mesh.points(vc * 3);
            var tc = mesh.ntriangles();
            var indices = mesh.triangles(tc * 3);
            var uvs = mesh.tcoords(vc * 2);
//            meshData.addUvs(uvs);
            meshData.addVertices(points);
            meshData.addIndices(indices);
            par_shapes_free_mesh(mesh);
        }
        if (!multi) {
            if (model != null)
                model.delete();
            //Creates a shape with
            model = Vao.create(2);
            model.bind();
            model.createAttribute(meshData.getVertices(), 3);
            model.createAttribute(meshData.getUvs(), 2);
            model.createIndexBuffer(meshData.getIndices());
            model.unbind();
        }
        loaded = true;
    }

    @Override
    public void draw(Shader shader) {
        shader.loadMat4("modelMatrix", modelMatrix());
        model.draw();
    }

    /**
     * @return returns the computed model matrix
     */
    private Matrix4f modelMatrix() {
        return modelMatrix.identity().translate(position);
    }
}
