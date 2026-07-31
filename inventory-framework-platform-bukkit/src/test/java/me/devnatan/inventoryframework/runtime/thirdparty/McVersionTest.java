package me.devnatan.inventoryframework.runtime.thirdparty;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class McVersionTest {

    @Test
    @DisplayName("Parse Spigot-style version")
    void parseSpigotVersion() {
        assertEquals(new McVersion(1, 20, 6), McVersion.parse("1.20.6-R0.1-SNAPSHOT"));
    }

    @Test
    @DisplayName("Parse Spigot-style version without patch")
    void parseSpigotVersionWithoutPatch() {
        // The API revision that follows must not be read as the patch number.
        assertEquals(new McVersion(1, 21, 0), McVersion.parse("1.21-R0.1-SNAPSHOT"));
        assertEquals(new McVersion(26, 1, 0), McVersion.parse("26.1-R0.1-SNAPSHOT"));
    }

    @Test
    @DisplayName("Parse Paper build version")
    void parsePaperBuildVersion() {
        // Paper publishes builds of the year-based versions with the build number in place of the
        // patch: everything from "build" on is the build identifier, not the version.
        assertEquals(new McVersion(26, 2, 0), McVersion.parse("26.2.build.84-stable"));
        assertEquals(new McVersion(26, 2, 0), McVersion.parse("26.2.build.84-stable-SNAPSHOT"));
    }

    @Test
    @DisplayName("Parse plain version")
    void parsePlainVersion() {
        assertEquals(new McVersion(26, 2, 1), McVersion.parse("26.2.1"));
        assertEquals(new McVersion(26, 2, 0), McVersion.parse("26.2"));
    }
}
