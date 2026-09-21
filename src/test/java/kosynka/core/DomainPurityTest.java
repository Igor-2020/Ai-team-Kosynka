package kosynka.core;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

/**
 * Guards the architecture rule: the kosynka.core domain layer must not depend on
 * Swing or AWT in any way.
 */
class DomainPurityTest {

    @Test
    void domainLayerHasNoUiDependencies() throws IOException {
        Path coreDir = Paths.get("src", "main", "java", "kosynka", "core");
        assertTrue(Files.isDirectory(coreDir), "core source directory must exist");

        try (Stream<Path> walk = Files.walk(coreDir)) {
            List<Path> sources = walk
                    .filter(path -> path.toString().endsWith(".java"))
                    .collect(Collectors.toList());

            assertFalse(sources.isEmpty(), "expected core source files");

            for (Path source : sources) {
                String content = Files.readString(source);
                assertFalse(content.contains("javax.swing"),
                        source + " must not import or reference javax.swing");
                assertFalse(content.contains("java.awt"),
                        source + " must not import or reference java.awt");
            }
        }
    }
}
