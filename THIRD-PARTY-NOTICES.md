# Third-Party Notices

> This file is generated from the Gradle runtime dependency graph. Do not edit it manually.
> Run `./gradlew updateThirdPartyNotices` after changing dependencies.

The complete HTML and JSON reports are generated in `build/reports/dependency-license/`.
If a new dependency introduces a license outside `gradle/allowed-licenses.json`, `checkLicense` will fail.
Known upstream metadata mismatches can be documented in `gradle/license-notice-overrides.json`.

## Dependency Summary

| Dependency | Version | License | License URL |
| :--- | :--- | :--- | :--- |
| com.badlogicgames.gdx-controllers:gdx-controllers-core | 2.2.4 | The Apache Software License, Version 2.0 | <http://www.apache.org/licenses/LICENSE-2.0.txt> |
| com.badlogicgames.gdx-controllers:gdx-controllers-desktop | 2.2.4 | The Apache Software License, Version 2.0 | <http://www.apache.org/licenses/LICENSE-2.0.txt> |
| com.badlogicgames.gdx:gdx | 1.14.0 | The Apache Software License, Version 2.0 | <http://www.apache.org/licenses/LICENSE-2.0.txt> |
| com.badlogicgames.gdx:gdx-backend-lwjgl3 | 1.14.0 | The Apache Software License, Version 2.0 | <http://www.apache.org/licenses/LICENSE-2.0.txt> |
| com.badlogicgames.gdx:gdx-box2d-platform | 1.14.0 | The Apache Software License, Version 2.0 | <http://www.apache.org/licenses/LICENSE-2.0.txt> |
| com.badlogicgames.gdx:gdx-freetype | 1.14.0 | The Apache Software License, Version 2.0 | <http://www.apache.org/licenses/LICENSE-2.0.txt> |
| com.badlogicgames.gdx:gdx-freetype-platform | 1.14.0 | The Apache Software License, Version 2.0 | <http://www.apache.org/licenses/LICENSE-2.0.txt> |
| com.badlogicgames.gdx:gdx-jnigen-loader | 2.5.2 | The Apache Software License, Version 2.0 | <http://www.apache.org/licenses/LICENSE-2.0.txt> |
| com.badlogicgames.gdx:gdx-platform | 1.14.0 | The Apache Software License, Version 2.0 | <http://www.apache.org/licenses/LICENSE-2.0.txt> |
| com.badlogicgames.jamepad:jamepad | 2.26.5.0 | The Apache Software License, Version 2.0 | <http://www.apache.org/licenses/LICENSE-2.0.txt> |
| com.badlogicgames.jlayer:jlayer | 1.0.1-gdx | GNU Lesser General Public License (LGPL), Version 2.1 | <http://www.fsf.org/licensing/licenses/lgpl.txt> |
| com.github.oshi:oshi-core | 6.6.3 | MIT | <https://opensource.org/licenses/MIT> |
| net.java.dev.jna:jna | 5.18.1 | Apache-2.0 | <https://github.com/java-native-access/jna> |
| net.java.dev.jna:jna-platform | 5.14.0 | Apache-2.0 | <https://github.com/java-native-access/jna> |
| org.ini4j:ini4j | 0.5.4 | Apache 2 | <http://www.apache.org/licenses/LICENSE-2.0.txt> |
| org.jcraft:jorbis | 0.0.17 | GNU Lesser General Public License | <http://www.gnu.org/copyleft/lesser.html> |
| org.lwjgl:lwjgl | 3.3.3 | BSD-3-Clause | <https://www.lwjgl.org/license> |
| org.lwjgl:lwjgl-glfw | 3.3.3 | BSD-3-Clause | <https://www.lwjgl.org/license> |
| org.lwjgl:lwjgl-jemalloc | 3.3.3 | BSD-3-Clause | <https://www.lwjgl.org/license> |
| org.lwjgl:lwjgl-openal | 3.3.3 | BSD-3-Clause | <https://www.lwjgl.org/license> |
| org.lwjgl:lwjgl-opengl | 3.3.3 | BSD-3-Clause | <https://www.lwjgl.org/license> |
| org.lwjgl:lwjgl-stb | 3.3.3 | BSD-3-Clause | <https://www.lwjgl.org/license> |
| org.ode4j:core | 0.5.4 | BSD-3-Clause | <https://github.com/tzaeschke/ode4j> |
| org.slf4j:slf4j-api | 2.0.16 | MIT License | <http://www.opensource.org/licenses/mit-license.php> |

## Manual Overrides

The following entries intentionally override published dependency metadata:
- `net.java.dev.jna:jna:5.18.1`: Upstream declares JNA as LGPL-2.1-or-later or Apache License 2.0. This project uses the Apache 2.0 option for JNA. Source: <https://github.com/java-native-access/jna>
- `net.java.dev.jna:jna-platform:5.14.0`: Upstream declares JNA as LGPL-2.1-or-later or Apache License 2.0. This project uses the Apache 2.0 option for the JNA platform artifact as well. Source: <https://github.com/java-native-access/jna>
- `org.ode4j:core:0.5.4`: Upstream Maven Central metadata publishes LGPL only, but the upstream repository states that ode4j is dual-licensed under LGPL v2.1 and BSD 3-clause and that users may choose either license. Source: <https://github.com/tzaeschke/ode4j>

## LGPL Components

The following runtime dependencies currently resolve to LGPL-family licenses:
- `com.badlogicgames.jlayer:jlayer:1.0.1-gdx`
- `org.jcraft:jorbis:0.0.17`

For redistribution, prefer `./gradlew desktop:distZip`: LGPL components remain as separate JARs in `lib/`, so they can be replaced without rebuilding the project.
The shadow JAR keeps classes unrelocated, but replacing them requires unpacking and repacking the archive.
