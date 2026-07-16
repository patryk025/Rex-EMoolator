"use strict";

// RIWC / Sekai.dll physics tracer. The offsets below are RVAs/field offsets verified
// against the Sekai.dll loaded in Ghidra for Reksio i Wehikul Czasu.
const CONFIG = {
    moduleName: "Sekai.dll",
    outputPath: "D:\\riwc_physics_original.jsonl",
    moveObjectsExport: "?MoveObjects@ISekai@@QAEJPAM@Z",
    objectRegistryRva: 0x40624,
    flushEvery: 120,
    maxObjects: 4096,
    // null means every object. Example: new Set([301, 302, 319, 501])
    ids: null
};

const OFFSETS = {
    id: 0x08,
    active: 0x18,
    body: 0x1c,
    gravityCenter: 0x1b,
    maxVelocity: 0x48,
    mass: 0x58,
    g: 0x60,
    x: 0x68,
    y: 0x6c,
    z: 0x70
};

let installed = false;
let traceFile = null;
let frame = 0;
let time = 0.0;

function finiteOrNull(value) {
    return Number.isFinite(value) ? value : null;
}

function writeLine(value) {
    traceFile.write(JSON.stringify(value) + "\n");
}

function readObjects(sekai) {
    const registry = sekai.base.add(CONFIG.objectRegistryRva).readPointer();
    if (registry.isNull()) {
        return { objects: [], readErrors: 0 };
    }

    const count = registry.add(0x08).readS32();
    const entries = registry.add(0x0c).readPointer();
    if (count < 0 || count > CONFIG.maxObjects || (count > 0 && entries.isNull())) {
        throw new Error("invalid Sekai object registry: count=" + count + ", entries=" + entries);
    }

    const objects = [];
    let readErrors = 0;
    for (let i = 0; i < count; i++) {
        try {
            const object = entries.add(i * Process.pointerSize).readPointer();
            if (object.isNull()) {
                readErrors++;
                continue;
            }

            const id = object.add(OFFSETS.id).readS32();
            if (CONFIG.ids !== null && !CONFIG.ids.has(id)) {
                continue;
            }
            objects.push({
                id: id,
                x: finiteOrNull(object.add(OFFSETS.x).readFloat()),
                y: finiteOrNull(object.add(OFFSETS.y).readFloat()),
                z: finiteOrNull(object.add(OFFSETS.z).readFloat()),
                active: object.add(OFFSETS.active).readU8() !== 0,
                dynamic: !object.add(OFFSETS.body).readPointer().isNull(),
                gravity_center: object.add(OFFSETS.gravityCenter).readU8() !== 0,
                mass: finiteOrNull(object.add(OFFSETS.mass).readFloat()),
                g: finiteOrNull(object.add(OFFSETS.g).readDouble()),
                max_velocity: finiteOrNull(object.add(OFFSETS.maxVelocity).readFloat()),
                storageIndex: i
            });
        } catch (error) {
            readErrors++;
        }
    }

    // Match the emulator's deterministic ID order. Sort is made explicitly stable by
    // falling back to the original registry index for legal duplicate IDs.
    objects.sort((a, b) => (a.id - b.id) || (a.storageIndex - b.storageIndex));
    const ordinals = new Map();
    for (const object of objects) {
        const ordinal = ordinals.get(object.id) || 0;
        object.ordinal = ordinal;
        ordinals.set(object.id, ordinal + 1);
        delete object.storageIndex;
    }
    return { objects: objects, readErrors: readErrors };
}

function recordFrame(sekai, dtPointer) {
    if (traceFile === null) {
        return;
    }
    let reportedDt = 0.0;
    try {
        if (dtPointer !== null && !dtPointer.isNull()) {
            reportedDt = dtPointer.readFloat();
        }
    } catch (error) {
        reportedDt = 0.0;
    }
    // MoveObjects reports the smoothed value, while the internal world step caps it at 30 ms.
    const dt = Number.isFinite(reportedDt) && reportedDt > 0.0
        ? Math.min(reportedDt, 0.03)
        : reportedDt;
    if (Number.isFinite(dt) && dt > 0.0) {
        time += dt;
    }

    const snapshot = readObjects(sekai);
    const line = {
        type: "frame",
        frame: frame,
        reported_dt: finiteOrNull(reportedDt),
        dt: finiteOrNull(dt),
        time: finiteOrNull(time),
        objects: snapshot.objects
    };
    if (snapshot.readErrors !== 0) {
        line.read_errors = snapshot.readErrors;
    }
    writeLine(line);
    frame++;
    if (frame % CONFIG.flushEvery === 0) {
        traceFile.flush();
    }
}

function install(sekai) {
    if (installed || sekai.name.toLowerCase() !== CONFIG.moduleName.toLowerCase()) {
        return;
    }
    if (Process.arch !== "ia32" || Process.pointerSize !== 4) {
        throw new Error("this RIWC tracer expects the 32-bit game (ia32), got " + Process.arch);
    }

    const moveObjects = sekai.getExportByName(CONFIG.moveObjectsExport);
    traceFile = new File(CONFIG.outputPath, "w");
    writeLine({
        type: "meta",
        schema: 2,
        source: "original",
        coordinate_system: "sekai_world_x_y_up_z",
        module: sekai.name,
        module_path: sekai.path,
        module_base: sekai.base.toString(),
        registry_rva: "0x" + CONFIG.objectRegistryRva.toString(16),
        move_objects: moveObjects.toString()
    });
    traceFile.flush();

    Interceptor.attach(moveObjects, {
        onEnter(args) {
            // x86 thiscall keeps ISekai* in ECX; the only declared argument is float* outDt.
            this.sekaiThis = this.context.ecx;
            this.dtPointer = args[0];
        },
        onLeave(retval) {
            try {
                recordFrame(sekai, this.dtPointer);
            } catch (error) {
                if (traceFile !== null) {
                    writeLine({ type: "error", frame: frame, message: String(error) });
                    traceFile.flush();
                }
            }
        }
    });

    installed = true;
    console.log("[riwc-physics-trace] hooked " + moveObjects);
    console.log("[riwc-physics-trace] writing " + CONFIG.outputPath);
}

const alreadyLoaded = Process.findModuleByName(CONFIG.moduleName);
if (alreadyLoaded !== null) {
    install(alreadyLoaded);
} else if (typeof Process.attachModuleObserver === "function") {
    Process.attachModuleObserver({
        onAdded(module) {
            if (module.name.toLowerCase() === CONFIG.moduleName.toLowerCase()) {
                install(module);
            }
        }
    });
} else {
    // Compatibility fallback for Frida older than 16.7 when spawning the game suspended.
    const timer = setInterval(() => {
        const module = Process.findModuleByName(CONFIG.moduleName);
        if (module !== null) {
            clearInterval(timer);
            install(module);
        }
    }, 100);
}

rpc.exports = {
    flush() {
        if (traceFile !== null) traceFile.flush();
        return frame;
    },
    close() {
        if (traceFile !== null) {
            traceFile.flush();
            traceFile.close();
            traceFile = null;
        }
        return frame;
    }
};
