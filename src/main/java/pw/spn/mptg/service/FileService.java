package pw.spn.mptg.service;

import java.nio.file.Path;

public interface FileService {
    Path createTempDirectory(String name);

    Path createTempFile(String name);

    void writeToFile(Path dir, String fileName, byte[] content);

    void deleteFile(Path file);

    void deleteDirectory(Path dir);

    byte[] readFromFile(Path file);

    Path createDirectories(Path root, String other);

    void createFile(Path dir, String fileName);
}
