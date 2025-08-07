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
            int entityCount = readHeader(variable, f);
            readEntities(variable, f, entityCount);
            Gdx.app.log("WorldVariable", "Loaded SEK: " + variable.getName());
        } catch (Exception e) {
            Gdx.app.error("WorldVariable", "Error while loading SEK: " + e.getMessage(), e);
        }
    }

    private static int readHeader(WorldVariable variable, FileInputStream f) throws IOException {
        String magic = BinaryHelper.readString(f, 16);
        if(!magic.equals("SEKAI81080701915")) {
            throw new IllegalArgumentException("Not a valid SEK file. Expected magic: SEKAI81080701915, got: " + magic);
        }
        String version = BinaryHelper.readString(f, 3);
        variable.setSekVersion(version);
        return BinaryHelper.readIntLE(f); // entityCount
    }

    private static void readEntities(WorldVariable variable, FileInputStream f, int entityCount) throws IOException {
        switch (variable.getSekVersion()) {
            case "004":
                for (int i = 0; i < entityCount; i++) {
                    int typeId = BinaryHelper.readIntLE(f);
                    int length = BinaryHelper.readIntLE(f);

                    switch (typeId) {
                        case 1:
                            parseSceneObject(variable, f);
                            break;
                        case 4:
                            parsePointsData(variable, f);
                            break;
                        default:
                            Gdx.app.log("WorldVariable", "Unknown entity type: " + typeId);
                            f.skip(length); // skip unknown entity data
                            break;
                    }
                }
            case "003": // It was present in Sekai.dll and their loading functions was slightly different
            case "002":
                throw new RuntimeException("Unsupported SEK version: " + variable.getSekVersion());
            default:
                throw new RuntimeException("Unknown SEK version: " + variable.getSekVersion());
        }
    }

    private static GameObject.GameObjectBuilder parseObjectHeader(FileInputStream f) throws IOException {
        int entityId = BinaryHelper.readIntLE(f);
        int flags = BinaryHelper.readIntLE(f); // ?, if 3, object can move and its position is read from file, if 0 and other position is ignored and is set to (0, 0, 0)

        float objX = BinaryHelper.readFloatLE(f);
        float objY = BinaryHelper.readFloatLE(f);
        float objZ = BinaryHelper.readFloatLE(f);
        float unknown7 = BinaryHelper.readFloatLE(f); // 0 or 1
        float rotationZ = BinaryHelper.readFloatLE(f); // very weird angle mapping
        float unknown9 = BinaryHelper.readFloatLE(f); // 0?
        float bodyDimensionX = BinaryHelper.readFloatLE(f); // radius (cylinder and sphere) or lx (box)
        float bodyDimensionY = BinaryHelper.readFloatLE(f); // length (cylinder) or ly (box)
        float bodyDimensionZ = BinaryHelper.readFloatLE(f); // lz (box)

        GameObject.GameObjectBuilder object = GameObject.builder();

        int numProps = BinaryHelper.readIntLE(f);
        for (int i = 0; i < numProps; i++) {
            String name = BinaryHelper.readString(f);
            f.skip(4); // padding?
            String value = BinaryHelper.readString(f);

            if(name.trim().equals("entityDef")) {
                // let's parse it up
                // split by \r\n
                String[] parts = value.split("\r\n");

                // every prop has format parameter(value1, value2, ...);
                for (String part : parts) {
                    if (part.contains("(") && part.contains(")")) {
                        String propName = part.substring(0, part.indexOf('(')).trim();
                        String propValues = part.substring(part.indexOf('(') + 1, part.indexOf(')')).trim();
                        String[] values = propValues.split(",");
                        switch (propName) {
                            case "geomType":
                                String geomType = values[0].trim();
                                switch (geomType) {
                                    case "box":
                                        object.geomType(0);
                                        break;
                                    case "cylinder":
                                        object.geomType(1);
                                        break;
                                    default:
                                        Gdx.app.error("WorldVariable", "Unknown geomType: " + geomType);
                                        break;
                                }
                                break;
                            case "mass":
                                object.mass(Float.parseFloat(values[0].trim()));
                                break;
                            case "mu":
                                object.mu(Float.parseFloat(values[0].trim()));
                                break;
                            case "friction":
                                object.mu2(Float.parseFloat(values[0].trim()));
                                break;
                            case "bounce":
                                object.bounce(Float.parseFloat(values[0].trim()));
                                break;
                            case "bounceVel":
                                object.bounceVelocity(Float.parseFloat(values[0].trim()));
                                break;
                            case "maxVel":
                                object.maxVelocity(Float.parseFloat(values[0].trim()));
                                break;
                            case "limit":
                                if (values.length == 6) {
                                    object.limits(
                                            Float.parseFloat(values[0].trim()), // minX
                                            Float.parseFloat(values[1].trim()), // minY
                                            Float.parseFloat(values[2].trim()), // minZ
                                            Float.parseFloat(values[3].trim()), // maxX
                                            Float.parseFloat(values[4].trim()), // maxY
                                            Float.parseFloat(values[5].trim())  // maxZ
                                    );
                                } else {
                                    Gdx.app.error("WorldVariable", "Invalid limit values: " + propValues);
                                }
                                break;
                            default:
                                Gdx.app.log("WorldVariable", "Unknown property: " + propName + " with values: " + propValues);
                                break;
                        }
                    } else {
                        Gdx.app.error("WorldVariable", "Unsupported property format: " + part);
                    }
                }
            }
        }

        object.id(entityId)
                .rigidBody(flags == 3)
                .position(objX, objY, objZ)
                .rotationZ(rotationZ)
                .dimensions(new float[]{bodyDimensionX, bodyDimensionY, bodyDimensionZ});

        return object;
    }

    private static void parseSceneObject(WorldVariable variable, FileInputStream f) throws IOException {
        GameObject.GameObjectBuilder builder = parseObjectHeader(f);

        int geometryType = BinaryHelper.readIntLE(f);
        int numTriangles = BinaryHelper.readIntLE(f);

        builder.geomType(geometryType);

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

        variable.getPhysicsEngine().createBody(builder.build(), new Mesh(triangles));
    }

    private static void parsePointsData(WorldVariable variable, FileInputStream f) throws IOException {
        GameObject.GameObjectBuilder builder = parseObjectHeader(f);

        int numPoints = BinaryHelper.readIntLE(f);
        int numPaths = BinaryHelper.readIntLE(f);

        for (int i = 0; i < numPoints; i++) {
            float x = BinaryHelper.readFloatLE(f);
            float y = BinaryHelper.readFloatLE(f);
            float z = BinaryHelper.readFloatLE(f);
            f.skip(4); // padding (4th value in dVector3, used for better alignment)
            //obj.addPoint(new Point3D(x, y, z));
        }

        for (int i = 0; i < numPaths; i++) {
            int firstId = BinaryHelper.readIntLE(f);
            int secondId = BinaryHelper.readIntLE(f);
            int unknown = BinaryHelper.readIntLE(f); // in test only value 3
            //obj.addPath(firstId, secondId, unknown);
        }

        //variable.addPoint(obj);
    }
}
