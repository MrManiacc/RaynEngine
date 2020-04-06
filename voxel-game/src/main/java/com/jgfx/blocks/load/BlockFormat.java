package com.jgfx.blocks.load;

import com.jgfx.assets.files.AbstractAssetFileFormat;
import com.jgfx.assets.files.AssetDataFile;
import com.jgfx.assets.urn.ResourceUrn;
import com.jgfx.blocks.data.BlockData;
import com.jgfx.utils.Side;
import com.jgfx.blocks.data.BlockElement;
import com.jgfx.blocks.data.BlockFace;
import org.joml.Vector3i;
import org.joml.Vector4i;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Optional;

public class BlockFormat extends AbstractAssetFileFormat<BlockData> {
    public BlockFormat() {
        super("block");
    }

    /**
     * Loads the image byte buffer data
     *
     * @param urn    The urn identifying the asset being loaded.
     * @param inputs The inputs corresponding to this asset
     * @return returns the parsed texture data
     * @throws IOException throws exception if the file isn't found
     */
    @Override
    public BlockData load(ResourceUrn urn, List<AssetDataFile> inputs) throws IOException {
        var block = inputs.get(0);
        return parseBlock(block.getPath().toFile());
    }

    /**
     * @return returns the parsed block for the file
     */
    private BlockData parseBlock(File file) {
        var parser = new JSONParser();
        try (Reader reader = new FileReader(file)) {
            var root = (JSONObject) parser.parse(reader);
            Optional<ResourceUrn> parent = Optional.empty();
            if (root.containsKey("parent"))
                parent = Optional.of(new ResourceUrn((String) root.get("parent")));
            var textures = (JSONObject) root.get("textures");
            var id = (long) root.get("id");
            var displayName = (String) root.get("displayName");
            var blockData = new BlockData((int) id, displayName, parent);
            if (root.containsKey("elements")) {
                var elements = (JSONArray) root.get("elements");
                for (JSONObject element : (Iterable<JSONObject>) elements)
                    blockData.addElement(parseElement(blockData, element, textures));
            }
            return blockData;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return returns a block element from the parsed object
     */
    private BlockElement parseElement(BlockData data, JSONObject element, JSONObject textures) {
        var fromArray = (JSONArray) element.get("from");
        var x1 = (long) fromArray.get(0);
        var y1 = (long) fromArray.get(1);
        var z1 = (long) fromArray.get(2);
        var from = new Vector3i((int) x1, (int) y1, (int) z1);
        var toArray = (JSONArray) element.get("to");
        var x2 = (long) toArray.get(0);
        var y2 = (long) toArray.get(1);
        var z2 = (long) toArray.get(2);
        var to = new Vector3i((int) x2, (int) y2, (int) z2);
        var blockElement = new BlockElement(from, to);
        var faces = (JSONObject) element.get("faces");
        parseFaces(faces, blockElement, textures);
        return blockElement;
    }

    /**
     * Parses all of the faces for the block element
     */
    private void parseFaces(JSONObject faces, BlockElement element, JSONObject textures) {
        for (var dir : Side.values()) {
            var name = dir.name().toLowerCase();
            if (faces.containsKey(name))
                element.addFace(parseFace((JSONObject) faces.get(name), dir, textures));
        }
    }

    /**
     * @return returns a parsed face for the given direction
     */
    private BlockFace parseFace(JSONObject face, Side direction, JSONObject textures) {
        var uvArray = (JSONArray) face.get("uv");
        var x1 = (long) uvArray.get(0);
        var y1 = (long) uvArray.get(1);
        var x2 = (long) uvArray.get(2);
        var y2 = (long) uvArray.get(3);
        var uv = new Vector4i((int) x1, (int) y1, (int) x2, (int) y2);
        var textureUrn = new ResourceUrn((String) textures.get(face.get("texture")));
        return new BlockFace(direction, uv, textureUrn);
    }

}
