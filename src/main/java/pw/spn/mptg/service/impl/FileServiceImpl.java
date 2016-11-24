package pw.spn.mptg.service.impl;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import pw.spn.mptg.service.FileService;

@Service
public final class FileServiceImpl implements FileService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Path createTempDirectory(String name) {
        try {
            return Files.createTempDirectory(name);
        } catch (IOException e) {
            logger.error("Unable to create temp directory {}.", name);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path createTempFile(String name) {
        try {
            return Files.createTempFile(name, null);
        } catch (IOException e) {
            logger.error("Unable to create temp file {}.", name);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeToFile(Path dir, String fileName, byte[] content) {
        Path file = dir.resolve(fileName);
        try {
            Files.write(file, content);
        } catch (IOException e) {
            logger.error("Unable to write content to file {}.", fileName);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteFile(Path file) {
        try {
            Files.delete(file);
        } catch (IOException e) {
            logger.warn("Unable to delete file {}", file);
            logger.warn(e.getMessage());
        }
    }

    @Override
    public void deleteDirectory(Path dir) {
        try {
            Files.walkFileTree(dir, new FileVisitor<Path>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    deleteFile(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    logger.warn("Visit file failed {}", file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    deleteFile(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.warn("Unable to delete directory {}", dir);
            logger.warn(e.getMessage());
        }
    }

    @Override
    public byte[] readFromFile(Path file) {
        try {
            return Files.readAllBytes(file);
        } catch (IOException e) {
            logger.error("Unable to read file {}.", file);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path createDirectories(Path root, String other) {
        Path path = root.resolve(other);
        try {
            return Files.createDirectories(path);
        } catch (IOException e) {
            logger.error("Unable to create directories {}.", path);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createFile(Path dir, String fileName) {
        Path path = dir.resolve(fileName);
        try {
            Files.createFile(path);
        } catch (IOException e) {
            logger.error("Unable to create file {}.", path);
            throw new RuntimeException(e);
        }
    }
}
