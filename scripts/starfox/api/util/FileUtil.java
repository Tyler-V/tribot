package scripts.starfox.api.util;

import org.tribot.util.Util;
import scripts.starfox.api.Printing;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Scanner;

import static java.nio.file.FileVisitResult.CONTINUE;

/**
 * @author Starfox
 */
public class FileUtil {

    /**
     * Returns the Sigma directory that is located in the TRiBot working directory.
     *
     * @return The Sigma directory that is located in the TRiBot working directory.
     */
    public static File getDir() {
        return new File(Util.getWorkingDirectory() + "/Sigma/");
    }

    /**
     * Returns the xml file that is used to save the following save file.
     *
     * @param fileName  The name of the file.
     * @param extension The extension of the file.
     * @param path      The directory path to be saved in.
     * @return The xml file that is used to save the following save file.
     */
    public static File getSaveFile(String fileName, String extension, String... path) {
        if (FileUtil.createFile(false, fileName, extension, path)) {
            return FileUtil.getFile(true, fileName, extension, path);
        } else {
            return null;
        }
    }

    /**
     * Returns a File located in the TRiBot directory folder.
     *
     * @param checkExists True if the method will return null if the file doesn't exist, false if the file should be returned regardless of its existence.
     * @param fileName    The name of the file.
     * @param extension   The extension of the file (dot not included).
     * @param dirPath     The directory path of the file. Null if the path should be the default directory only.
     * @return a File located in the TRiBot directory folder.
     */
    public static File getFile(boolean checkExists, String fileName, String extension, String... dirPath) {
        File triFolder = getDir();
        String restOfPath = "";
        if (dirPath != null) {
            for (String dirPart : dirPath) {
                restOfPath += dirPart + "/";
            }
        }
        restOfPath += fileName + "." + extension;
        File file = new File(triFolder, restOfPath);
        return !checkExists || file.exists() ? file : null;
    }

    /**
     * Returns the directory located in the TRiBot directory folder represented by the specified directory path.
     *
     * @param checkExists Whether or not the method will return null if the specified directory does not exist.
     * @param path        The directory path to the directory being returned.
     * @return the directory located in the TRiBot directory folder represented by the specified directory path.
     */
    public static File getDirectory(boolean checkExists, String... path) {
        String fileName = null;
        String[] newPath = null;
        if (path.length != 0) {
            fileName = path[path.length - 1];
            newPath = new String[path.length - 1];
            System.arraycopy(path, 0, newPath, 0, path.length - 1);
        }
        if (fileName != null && newPath != null) {
            return getFile(checkExists, fileName, "/", newPath);
        } else {
            return null;
        }
    }

    /**
     * Creates a file will the specified name, extension, and directory path. If any parts of the directory path do not exist, they will be created.
     *
     * Will return true if the specified file exists at the end of execution (regardless if it had already existed), and false otherwise.
     *
     * @param shouldWipe Determines whether the method should wipe the existing contents of the file or not.
     * @param fileName   The name of the file that is the be created.
     * @param extension  The extention that the file is to have.
     * @param dirPath    The directory path to the file that is being created.
     * @return true if the specified file exists at the end of execution (regardless if it had already existed), and false otherwise.
     */
    public static boolean createFile(boolean shouldWipe, String fileName, String extension, String... dirPath) {
        createDirectory(dirPath);
        File file = getFile(false, fileName, extension, dirPath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                return false;
            }
        } else if (shouldWipe) {
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                return false;
            }
        }
        return file.exists();
    }

    /**
     * Creates a directory based on the specified directory path.
     *
     * Will return true if the specified directory exists at the end of execution (regardless if it had already existed), and false otherwise.
     *
     * @param dirPath The directory path that the file will take.
     * @return true if the specified directory exists at the end of execution (regardless if it had already existed), and false otherwise.
     */
    public static boolean createDirectory(String... dirPath) {
        File directory = getDirectory(false, dirPath);
        if (directory != null) {
            if (directory.exists()) {
                return true;
            } else {
                return directory.mkdirs();
            }
        } else {
            return false;
        }
    }

    /**
     * Deletes a directory based on the specified directory path.
     *
     * Will return false if the specified directory exists at the end of execution (regardless if it didn't exist to begin with), and true otherwise.
     *
     * @param dirPath The directory path that the file will take.
     * @return false if the specified directory exists at the end of execution (regardless if it didn't exist to begin with), and true otherwise.
     */
    public static boolean deleteDirectory(String... dirPath) {
        File directory = getDirectory(false, dirPath);
        return deleteDirectory(directory);
    }

    /**
     * Deletes a directory based on the specified directory path.
     *
     * Will return false if the specified directory exists at the end of execution (regardless if it didn't exist to begin with), and true otherwise.
     *
     * @param directory The directory that is to be deleted.
     * @return false if the specified directory exists at the end of execution (regardless if it didn't exist to begin with), and true otherwise.
     */
    public static boolean deleteDirectory(File directory) {
        if (!directory.exists()) {
            return true;
        }
        Path dir = Paths.get(directory.getAbsolutePath());
        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file,
                        BasicFileAttributes attrs) throws IOException {
                    System.out.println("Deleting file: " + file);
                    Files.delete(file);
                    return CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir,
                        IOException exc) throws IOException {
                    System.out.println("Deleting dir: " + dir);
                    if (exc == null) {
                        Files.delete(dir);
                        return CONTINUE;
                    } else {
                        throw exc;
                    }
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return !directory.exists();
    }

    /**
     * Returns the contents of the specified text file.
     *
     * @param fileName  The name of the file that is being read.
     * @param extension The extension of the file.
     * @param dirPath   The directory path to the file that is being read.
     * @return the contents of the specified text file.
     */
    public static String getTextFileContentsExt(String fileName, String extension, String... dirPath) {
        File file = getFile(false, fileName, extension, dirPath);
        return getTextFileContents(file);
    }

    /**
     * Returns the contents of the specified text file.
     *
     * @param fileName The name of the file that is being read.
     * @param dirPath  The directory path to the file that is being read.
     * @return the contents of the specified text file.
     */
    public static String getTextFileContents(String fileName, String... dirPath) {
        return getTextFileContentsExt(fileName, "txt", dirPath);
    }

    /**
     * Returns the contents of the specified text file.
     *
     * @param file The file that is being read from.
     * @return the contents of the specified text file.
     */
    public static String getTextFileContents(File file) {
        if (file != null) {
            String contents = "";
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNext()) {
                    contents += scanner.next();
                }
            } catch (FileNotFoundException e) {
                return null;
            }
            return contents;
        } else {
            return null;
        }
    }

    public static void traverseTextFile(TextTraversable traversable, String fileName, String extension, String... dirPath) {
        File file = getFile(false, fileName, extension, dirPath);
        traverseTextFile(traversable, file);
    }

    public static void traverseTextFile(TextTraversable traversable, File file) {
        if (file != null) {
            try {
                Scanner scanner = new Scanner(file);
                int lineNumber = 0;
                while (scanner.hasNext()) {
                    lineNumber++;
                    traversable.traverseNext(scanner.nextLine(), lineNumber);
                }
            } catch (FileNotFoundException e) {
                Printing.err("File Not Found");
            }
        } else {
            Printing.err("File is null");
        }
    }

    public interface TextTraversable {

        public void traverseNext(String next, int lineNumber);
    }

    /**
     * Writes the specified text to the specified file.
     *
     * @param text      The text that is being written to the file.
     * @param fileName  The name of the file that is being written to.s
     * @param overwrite True if the contents should be overwritten, false otherwise.
     * @param dirPath   The directory path to the file that is being written to.
     * @return Whether or not the file was successfully written to.
     */
    public static boolean writeTextFileContents(String text, String fileName, boolean overwrite, String... dirPath) {
        return writeTextFileContents(text, fileName, "txt", overwrite, dirPath);
    }

    /**
     * Writes the specified text to the specified file.
     *
     * @param text      The text that is being written to the file.
     * @param fileName  The name of the file that is being written to.s
     * @param extension The extension of the file being read to without the dot.
     * @param overwrite True if the contents should be overwritten, false otherwise.
     * @param dirPath   The directory path to the file that is being written to.
     * @return Whether or not the file was successfully written to.
     */
    public static boolean writeTextFileContents(String text, String fileName, String extension, boolean overwrite, String... dirPath) {
        createFile(false, fileName, extension, dirPath);
        File file = getFile(false, fileName, extension, dirPath);
        return writeTextFileContents(text, file, overwrite);
    }

    /**
     * Writes the specified text to the specified file.
     *
     * @param text      The text that is being written to the file.
     * @param file      The file that is being written to.
     * @param overwrite True if the existing text should be overwritten, false otherwise.
     * @return Whether or not the file was successfully written to.
     */
    public static boolean writeTextFileContents(String text, File file, boolean overwrite) {
        FileWriter writer = null;
        if (file != null) {
            try {
                writer = new FileWriter(file.getAbsolutePath(), !overwrite);
                writer.write(text);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (IOException ex) {
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns all of the files in the specified directory that match the specified requirements, or null if the directory does not exist.
     *
     * If any part of the absolute file path (including the extension) contain the specified string, then the file will be included in the returned array. The requirements are not
     * case sensitive.
     *
     * @param reqs    The requirements.
     * @param dirPath The path.
     * @return All of the files in the specified directory that match the specified requirements, or null if the directory does not exist.
     */
    public static File[] getFilesInDirectory(final String reqs, final String... dirPath) {
        File dir = getDirectory(true, dirPath);
        if (dir == null) {
            return null;
        } else {
            return dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.getAbsolutePath().toLowerCase().contains(reqs.toLowerCase());
                }
            });
        }
    }
}
