package pl.genschu.bloomooemulator.loader;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.geometry.points.Point3D;
import pl.genschu.bloomooemulator.interpreter.variable.types.WorldVariable;
import pl.genschu.bloomooemulator.loader.helpers.BinaryHelper;
import pl.genschu.bloomooemulator.utils.FileUtils;
import pl.genschu.bloomooemulator.world.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SEKLoader {
    public static void loadSek(WorldVariable variable) {
        Gdx.app.log("WorldVariable", "Loading SEK: " + variable.getName());
        String filePath = FileUtils.resolveRelativePath(variable);
        try (FileInputStream f = new FileInputStream(filePath)) {
            readHeader(variable, f);
            readEntities(variable, f);
            Gdx.app.log("WorldVariable", "Loaded SEK: " + variable.getName());
        } catch (Exception e) {
            Gdx.app.error("WorldVariable", "Error while loading SEK: " + e.getMessage(), e);
        }
    }

    private static void readHeader(WorldVariable variable, FileInputStream f) throws IOException {
        String magic = BinaryHelper.readString(f, 19);
        if(!magic.equals("SEKAI81080701915004")) {
            throw new IllegalArgumentException("Not a valid SEK file. Expected magic: SEKAI81080701915004, got: " + magic);
        }
        variable.setEntityCount(BinaryHelper.readIntLE(f));
    }

    private static void readEntities(WorldVariable variable, FileInputStream f) throws IOException {
        int entityCount = variable.getEntityCount();

        for (int i = 0; i < entityCount; i++) {
            int typeId = BinaryHelper.readIntLE(f);

            switch (typeId) {
                case 1:
                    parseSceneObject(variable, f);
                    break;
                case 4:
                    parsePointsData(variable, f);
                    break;
                default:
                    Gdx.app.log("WorldVariable", "Unknown entity type: " + typeId);
                    break;
            }
        }
    }

    private static void parseSceneObject(WorldVariable variable, FileInputStream f) throws IOException {
        GameObject obj = new GameObject();

        if(variable.getGameObjects().containsKey(obj.getId())) {
            // TODO: investigate how this works (duplicate objects as separate objects or as single object?)
            Gdx.app.log("WorldVariable", "Duplicate entity id: " + obj.getId()+", adding new object with the same id");
        }

        int length = BinaryHelper.readIntLE(f);
        int entityId = BinaryHelper.readIntLE(f);
        int flags = BinaryHelper.readIntLE(f); // ?, if 3, object can move and its position is read from file, if 0 and other position is ignored and is set to (0, 0, 0)

        float objX = BinaryHelper.readFloatLE(f);
        float objY = BinaryHelper.readFloatLE(f);
        float objZ = BinaryHelper.readFloatLE(f);
        float unknown7 = BinaryHelper.readFloatLE(f);
        float rotationZ = BinaryHelper.readFloatLE(f); // very weird angle mapping
        float unknown9 = BinaryHelper.readFloatLE(f);
        float unknown10 = BinaryHelper.readFloatLE(f); // have impact on speed
        float unknown11 = BinaryHelper.readFloatLE(f);
        float unknown12 = BinaryHelper.readFloatLE(f);

        obj.setX(objX);
        obj.setY(objY);
        obj.setZ(objZ);
        obj.setRotationZ(rotationZ);
        obj.setId(entityId);
        obj.setRigidBody(flags == 3); // TODO: verify

        int numProps = BinaryHelper.readIntLE(f);
        for (int i = 0; i < numProps; i++) {
            String name = BinaryHelper.readString(f);
            f.skip(4); // padding
            String value = BinaryHelper.readString(f);
            EntityProp prop = new EntityProp(name, value);
            obj.addProp(prop);
        }

        int unknown13 = BinaryHelper.readIntLE(f); // padding?
        int numTriangles = BinaryHelper.readIntLE(f);

        List<MeshTriangle> triangles = new ArrayList<>();
        for (int i = 0; i < numTriangles; i++) {
            String material = BinaryHelper.readString(f);
            TriangleVertex[] triangle = new TriangleVertex[3];
            for (int j = 0; j < 3; j++) {
                float x = BinaryHelper.readFloatLE(f);
                float y = BinaryHelper.readFloatLE(f);
                float z = BinaryHelper.readFloatLE(f);
                float normalX = BinaryHelper.readFloatLE(f);
                float normalY = BinaryHelper.readFloatLE(f);
                float normalZ = BinaryHelper.readFloatLE(f);
                float u = BinaryHelper.readFloatLE(f);
                float v = BinaryHelper.readFloatLE(f);
                triangle[j] = new TriangleVertex(new Point3D(x, y, z), new Point3D(normalX, normalY, normalZ), u, v);
            }
            float unknown25 = BinaryHelper.readFloatLE(f);
            float unknown26 = BinaryHelper.readFloatLE(f);
            float unknown27 = BinaryHelper.readFloatLE(f);
            MeshTriangle meshTriangle = new MeshTriangle(material, triangle, unknown25, unknown26, unknown27);
            triangles.add(meshTriangle);
        }
        Mesh mesh = new Mesh(triangles);
        obj.setMesh(mesh);

        variable.addGameObject(obj);
    }

    private static void parsePointsData(WorldVariable variable, FileInputStream f) throws IOException {
        PointsData obj = new PointsData();

        int length = BinaryHelper.readIntLE(f); // =noOfPoints*12+numberOfPaths*12+56, usable data size without padding
        int unknown2 = BinaryHelper.readIntLE(f); // 9000 or 9001?
        f.skip(44); // only NULL bytes
        int numPoints = BinaryHelper.readIntLE(f);
        int numPaths = BinaryHelper.readIntLE(f);

        for (int i = 0; i < numPoints; i++) {
            float x = BinaryHelper.readFloatLE(f);
            float y = BinaryHelper.readFloatLE(f);
            float z = BinaryHelper.readFloatLE(f);
            f.skip(4); // padding
            obj.addPoint(new Point3D(x, y, z));
        }

        for (int i = 0; i < numPaths; i++) {
            int firstId = BinaryHelper.readIntLE(f);
            int secondId = BinaryHelper.readIntLE(f);
            int unknown = BinaryHelper.readIntLE(f); // in test only value 3
            obj.addPath(firstId, secondId, unknown);
        }

        variable.addPoint(obj);
    }
}
