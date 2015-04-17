package generic.test;



import android.graphics.Bitmap;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import com.robotium.solo.Solo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



@SuppressWarnings("rawtypes")
public class MainClass extends ActivityInstrumentationTestCase2 {



    protected Solo solo;
    private static Class<?> launcherActivityClass;
    protected static final String LOG_PRINT_END_OF_TEST = "Test has ended. Timestamp = ";
    protected static final String BUTTON_TEXT_OK = "OK";
    protected static final String BUTTON_TEXT_TRY_AGAIN = "Försök igen";
    protected static final int timeout = 60000;
    protected static final String FILE_NAME_TESTCASE_LOG = "/data/local/tmp/logg.txt";



    static {
        try {
            //launcherActivityClass = Class.forName("se.fskab.android.reseplaneraren.Splash");
            launcherActivityClass = Class.forName("com.imdb.mobile.HomeActivity");
            //launcherActivityClass = Class.forName("se.sfbio.mobile.android.SplashActivity");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public MainClass() throws ClassNotFoundException {
         super(launcherActivityClass);
    }

    /**
     * The setup method will initiate a solo object and get instrumentation.
     */
    @Override
    protected void setUp() throws Exception {
        logging("Initiating setup....");
        logging("Setting up testcase: " + this.getClass().getSimpleName());
        logging("Will initate the solo object.");
        super.setUp();
        logging("Will iniate solo.");
        solo = new Solo(getInstrumentation());
        logging("Will get activity.");
        getActivity();
    }

    /**
     * Will tear down the activities when the test case has finsihed.
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        solo.finishOpenedActivities();
    }

    /**
     * Will wait for an application to start, that is getting to the start page of the
     * application. The time needs to wait needs to be set long enough to always load
     * start page in time.
     * @param t Time to wait for start page to appear,
     */
    public void waitForApplicationToStart(int t) {
        logging("Waiting for application to start.");
        solo.sleep(t);
    }
    /**
     * Will wait for an application to start. The class parameter in this method, should be a
     * class on the applications start page which needs to be loaded. Use for example
     * UIAutomaterViewer to see what classes are available on the applications start page.
     * @param classToLoad The class object we want to load.
     * @param nbrOfClassObjects The number of objects for the class we want to load.
     */
    public void waitForApplicationToStart(Class classToLoad, int nbrOfClassObjects) {
        logging("Waiting for application to start.");
        solo.waitForView(classToLoad, nbrOfClassObjects, 60);
    }
    /**
     * Will make a log entry in a text file logg.txt, which should be located on the device under
     * test at /data/local/tmp/. The text file can be pushed onto the device using adb push command.
     * This method will not create any text file itself.
     *
     * @param s String to be logged.
     */
    protected void logging(final String s) {
        try {
            String filename = FILE_NAME_TESTCASE_LOG;
            FileWriter fw = new FileWriter(filename, true); //the true will append the new data
            fw.write(getTimestampForLogPrint() + " " + s + "\r\n");//appends the string to the file
            fw.close();
        } catch (IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }

    /**
     * This function is used to report errors. It will first logg that an error has occurred, then
     * call the takeScreenshot function and finally assert. Is used when a defenitive error has been seen.
     *
     * @param s String to be logged.
     */
    public void reportError(final String s) {
        logging("An error has occured");
        logging(s);
        reportEndOfTest();
        try {
            takeScreenshot();
        } catch (IOException e) {
            logging(e.toString());
        }
        assertTrue("Error! " + s, false);
    }

    /**
     * Called by the testcase when test has either passed or failed.
     * The resulting log print contains a "filename friendly" timestamp that can be used to identify the log files.
     */
    public void reportEndOfTest() {
        logging(LOG_PRINT_END_OF_TEST + getTimestampFilenameFriendly());
    }

    /**
     * Called by the testcase when all success criteria have been fulfilled.
     * The resulting log print contains a "filename friendly" timestamp that can be used to identify the log files.
     */
    public void reportSuccess() {
        logging("Test case was PASS");
        reportEndOfTest();
    }

    /**
     * Returns a timestamp in a "filename friendly" format (no ":", ";", ".", etc.).
     *
     * @return The current timestamp on format yyyyMMdd-HHmmss. Includes full date, but not milliseconds.
     */
    protected String getTimestampFilenameFriendly() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.GERMANY);
        return sdf.format(date);
    }

    /**
     * See getTimestampFilenameFriendly
     *
     * @return See getTimestampFilenameFriendly
     */
    protected String getTimestampForScreenshotFileName() {
        return getTimestampFilenameFriendly();
    }

    /**
     * Returns a timestamp in a more precise style suitable for log prints.
     *
     * @return Timsestamp in format HH:mm:ss.SSS. Includes milliseconds, but not the full date.
     */
    protected String getTimestampForLogPrint() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS", Locale.GERMANY);
        return sdf.format(date);
    }

    /**
     * Returns the path in which screenshots will be stored.
     * This path is (probably) dependent on device model and Android version.
     * This method also sets the class variable, so it can be quickly accessed when we take screenshots.
     *
     * @return The path.
     */
    private File getScreenshotDirectory() {
        // The 3 directories below are NOT working.
        //File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS); // open failed: EACCES (Permission denied)
        //File directory = Environment.getExternalStorageDirectory(); // open failed: EACCES (Permission denied)
        //File directory = Environment.getDataDirectory(); // open failed: EACCES (Permission denied)

        // This gives the app's private file storage directory.
        // This is the only directory we are currently able to create files in.
        return solo.getCurrentActivity().getExternalFilesDir(null);
    }

    /**
     * Workaround since the Solo.takeScreenshot() doesn't work.
     * http://stackoverflow.com/questions/17021338/how-to-make-takescreenshot-work-on-robotium-when-calling-junit-via-command-lin
     */
    private void takeScreenshot() throws IOException {

        if (solo.equals(null)) {
            // This means that the error occurred before Solo object was initialized - probably database error in setUp() method.
            // Screenshot is not possible.
            logging("takeScreenshot(): Error occurred before Robotium framework was initialized, screenshot cannot be taken.");
            return;
        }

        String filename =
                "TakenScreenshot_" +
                        getTimestampForScreenshotFileName() +
                        "_" +
                        this.getClass().getSimpleName() + // <-- Name of Testcase class e.g. "testcaseTicketsTheathers"
                        ".jpg";
        File directory = getScreenshotDirectory();

        // Capture screenshot and store data in a Bitmap object.
        View view = solo.getCurrentViews().get(0).getRootView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

        // Create file to write to.
        File file = new File(directory, filename);
        if (file.equals(null)) {
            throw new IOException("Failed to open file for screenshot. ");
        }

        // Write to the file, and close the file stream when done.
        FileOutputStream outputstream;
        try {
            outputstream = new FileOutputStream(file);
        } catch (IOException e) {
            throw new IOException("Failed to open output stream for screenshot: " + e.toString());
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputstream);
        outputstream.close();

        // Writes the path of the screenshot to the log, so that the shell script can pull the files.
        String pathToScreenshot = file.toString().replace(
                "/storage/emulated/0/Android/data/",
                "/storage/emulated/legacy/Android/data/" // <-- Confirmed to be correct directory on all phones so far.
        );
        logging("Path to screenshot: " + pathToScreenshot);
    }


}