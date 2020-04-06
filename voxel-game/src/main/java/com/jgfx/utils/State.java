package com.jgfx.utils;

/**
 * Stores the current state of the chunk,
 * unloaded: the chunk should be removed from the system
 * blocks loaded: the chunk has access to it's blocks,
 * mesh loaded: the chunk has it's blocks built, meaning the neighbors are at least block loaded stage
 * needs rebuild: the chunk has a mesh, but it's block have been updated so it's needs to rebuild it's mesh
 * model loaded: the chunk has built the mesh and now is ready to load the vertex data to opengl
 */
public enum State {
    UNLOADED, BLOCKS_LOADED, LOADING_MESH, MESH_LOADED, NEEDS_REMOVAL, NEEDS_REBUILD, MODEL_REBUILT, MODEL_LOADED;
}
