package scripts.starfox.api;

import org.tribot.util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The ErrorReporting class allows you to safely report errors to files for debugging purposes.
 *
 * @author Nolan
 */
public class ErrorReporting {

    /**
     * Reports an exception via printing the stack trace to a file.
     *
     * @param exception The exception to report.
     * @param directory The directory in which to place the report.
     * @param fileName  The report file name.
     * @return True if the exception was successfully reported, false otherwise.
     */
    public static boolean report(Exception exception, File directory, String fileName) {
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directory.getAbsolutePath() + "/" + fileName + ".txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException ex) {
            System.out.println("There was a problem creating a log file for the error: " + exception.getMessage());
            return false;
        }
        try {
            try (PrintWriter writer = new PrintWriter(file)) {
                exception.printStackTrace(writer);
                writer.flush();
            }
        } catch (FileNotFoundException ex) {
            System.out.println("The file was not found: " + file.getAbsolutePath());
            return false;
        }
        System.out.println("Successfully reported " + exception + " to " + directory.getAbsolutePath());
        return true;
    }

    /**
     * Reports the specified exception to the sigma exceptions folder with a file name that is equivalent to timestamp_throwable_hash.
     *
     * @param exception The exception to report.
     * @return True if the exception was reported successfully, false otherwise.
     */
    public static boolean report(Exception exception) {
        return report(exception, new File(Util.getWorkingDirectory().getAbsolutePath() + "/Sigma/exceptions"),
                "" + new SimpleDateFormat("h:mm a").format(new Date()).replaceAll(":", ",") + "_" + exception.getClass().getSimpleName()
                + "_" + exception.hashCode());
    }
}
