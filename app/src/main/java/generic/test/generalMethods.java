package generic.test;

import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Random;

public class generalMethods extends MainClass {

    public generalMethods() throws ClassNotFoundException {
        super();
    }

    /**
     * Clicks on a button with the specified String. Does not take into account upper or lower case.
     * This is the same as calling solo.clickOnText(text), but matches regardless of case and works for regular expressions.
     * @param text The text String to search for. This should contain no special characters.
     */
    protected void clickOnTextCaseInsensitive(String text) {
        logging("Will click on text " + text + ".");
        solo.clickOnText("(?i)" + text);
    }

    /**
     * Searches for the specified String, and returns True if the String was found.
     * Does not take into account upper or lower case.
     * This is the same as calling solo.searchText(text), but matches regardless of case and works for regular expressions.
     * @param text The text String to search for. This should contain no special characters.
     * @return True if the String was found.
     */
    protected boolean searchTextCaseInsensitive(String text) {
        logging("Will search for " + text + ".");
        return solo.searchText("(?i)" + text);
    }

    /**
     * Searches for the specified String, and returns True if the String was found.
     * Does not take into account upper or lower case.
     * This is the same as calling solo.searchText(text), but matches regardless of case and works for regular expressions.
     * @param text        The text String to search for. This should contain no special characters.
     * @param onlyVisible Whether or not Solo should attempt to scroll in order to find the specified String.
     * @return True if the String was found.
     */
    protected boolean searchTextCaseInsensitive(String text, boolean onlyVisible) {
        logging("Will search for " + text + ", but only if visible on screen.");
        return solo.searchText("(?i)" + text,onlyVisible );
    }


    protected void searchAndReport(final boolean b, final String s, final String f) {
        if (b) {
            logging(s);
        }
        if (!b) {
            reportError(f);
        }
    }


    /**
     * Check if the view resource id name exist in the current activity views.
     * Otherwise there is an error will be reported and the test case fails.
     * @param name Resource id of the object
     * @return True if object exists.l
     */
    public boolean doesItemExist(final String name) {
        logging("Will start looking for the object " + name + ".");
        if (solo.getCurrentActivity().findViewById(solo.getCurrentActivity().getResources().getIdentifier(name, "id", solo.getCurrentActivity().getPackageName())) != null) {
            return true;
        } else {
            reportError("Was not able to find the object " + name + " and therefore the test case fails.");
            return false;
        }
    }

    /**
     * Check if the view resource id name exist in the current activity views.
     * If object is not found, it will return false without reporting error.
     * @param name Resource id of the object
     * @return True if object exists.l
     */
    public boolean doesItemExistWithoutFailure(final String name) {
        logging("Will start looking for the object " + name + ".");
        if (solo.getCurrentActivity().findViewById(solo.getCurrentActivity().getResources().getIdentifier(name, "id", solo.getCurrentActivity().getPackageName())) != null) {
            return true;
        } else {
            logging("Could not find the object " + name + " but will not fail test case.");
            return false;
        }
    }

    /**
     * Will first check if the view exists and if so click on it.
     * @param resourceID The resource id of the view we want to click on.
     * @param index The index of the view we want to click on.
     */
    public void getAndClickOnView (String resourceID, int index){
        doesItemExist(resourceID);
        View view = solo.getView(resourceID, index);
        logging("Found a view with resource id " + resourceID + " and will click on it.");
        solo.clickOnView(view);
    }


    /**
     * This method reads the text from a text view within the application.
     * Be aware of the index if there is more than one text view with the same resource id.
     * @param s The resource id of the view we want read from.
     * @param index The index of the text view we want to read from.
     * @return The text string from the TextView.
     */
    public String readTextFromTextView (String s, int index){
        TextView readFromApp = (TextView) solo.getView(s, index);
        String fromApp = (String) readFromApp.getText();
        logging("String read from application: " + fromApp + ".");
        return fromApp;


    }
    /**
     * First clear a edit text and then enters text into it. It will finish by
     * emulating an enter key press.
     * @param s The text we want to type.
     * @param index The index of the text field
     */
    public void enterTextInField(String s, int index){
        logging("Will write " + s + " to a edit text field.");
        solo.clearEditText(index);
        solo.typeText(index, s);
        ImeDone();
    }


    /**
     * Will check if the items from the provided list exists within the application using text search.
     * @param list A list containing the strings we want to search for in the list.
     * @param scroll If true, the method will scroll to the top of the list between checking for each item.
     */
    public void checkItemsInListFromArray(final ArrayList<String> list, boolean scroll, String verifyLink, Class classToLoadInListView, Class classToLoadInListViewOnLinkPage, final int noObjects, final int numberTries) {
        logging("Size of list is " + list.size());
        for (int i = 0; i < list.size(); i++) {
            if (scroll){solo.scrollToTop();}
            logging("Now searching for " + list.get(i));
            loadView(classToLoadInListView, noObjects, numberTries, "Timed out when waiting for link list page to load.");
            searchAndReport(searchTextCaseInsensitive(list.get(i)), list.get(i) + " was successfully found.", list.get(i) + " was not found inte the list.");
            clickOnTextCaseInsensitive(list.get(i));
            loadView(classToLoadInListViewOnLinkPage, noObjects, numberTries, "The links page did not load fast enough.");
            //To check that the link we are in is for the correct movie
            searchAndReport(searchTextCaseInsensitive(verifyLink), "The link " + verifyLink + " contains a specific text for verification of link correctness.", "The link " + verifyLink + " contains a specific text for verification of link correctness");
            searchAndReport(searchTextCaseInsensitive(list.get(i)), "The link " + list.get(i) + " was correct", "The link " + list.get(i) + " could not be found.");

            solo.goBack();
        }
    }

    /**
     * Will check every 5 seconds if there is a progress bar currently in view.
     * If so, it will make a sleep and then check agian. After 1 minute it will report an error.
     * This method will search for the standard android widget progressbar.
     */
    protected void waitForProgressBar() {
        int t = 0;
        final int NTIMES_MAX = 15;
        while (!solo.getCurrentViews(android.widget.ProgressBar.class).isEmpty() && t < NTIMES_MAX) {
            logging("There was a progress bar.");
            solo.sleep(5000);
            t++;
            if (t == NTIMES_MAX) {
                reportError("There was a progress bar that time out.");
            }

        }
    }

    /**
     * Will check every 5 seconds if there is a progress bar currently in view.
     * If so, it will make a sleep and then check again. After 1 minute it will report an error.
     * This method will search for a progress bar other than the standard. Of course this could be
     * an object representing a progress bar such that it shows when a page is loading.
     * @param progressbarClass The class which represents a progress bar.
     */
    protected <T extends View> void waitForProgressBar(final Class<T> progressbarClass) {
        int t = 0;
        final int NTIMES_MAX = 15;
        while (!solo.getCurrentViews(progressbarClass).isEmpty() && t < NTIMES_MAX) {
            logging("There was a progress bar.");
            solo.sleep(5000);
            t++;
            if (t == NTIMES_MAX) {
                reportError("There was a progress bar that time out.");
            }

        }
    }

    /**
     * Waits for a specified view to be loaded within the time out limit. If the view is not loaded, it will first
     * check if there is a progress bar (checking for the standard Android progress bar)
     * and then wait for it to disappear (or the function will eventually
     * report an error) and then will check if there is any choice to try again.
     *
     * @param classToLoad              The class to load. This needs to be a subclass of android.view.View.
     * @param noObjects                The number of objects of the specified class that this method will expect.
     * @param numberTries              Use this parameter to allow multiple attempts to find the View. Useful if network conditions are suboptimal.
     * @param descriptionOfViewLoading A description of the view. This String will be printed in the logfile if the View is not loaded correctly.
     * @return True if the View is loaded successfully.
     */
    protected <T extends View> boolean loadView(final Class<T> classToLoad, final int noObjects, final int numberTries, final String descriptionOfViewLoading) {
        logging("Will wait for " + classToLoad + " to be loaded.");
        if (!solo.waitForView(classToLoad, noObjects, timeout)) {
            logging("The first attmept to load the view timed out");
            for (int i = 0; i < numberTries; i++) {
                logging("This is try number " + i + " that we have made so far.");
                //The below checks should search for a way to reconnect if there is any
                //This could be that the application gives you the option to reload etc.
                //Below are two examples of buttons that may appear when having connection issues.
                //Needs to be adjusted with respect of application under test.

//                if (searchTextCaseInsensitive(BUTTON_TEXT_OK)) {
//                    logging("Found a OK button and will press it.");
//                    clickOnTextCaseInsensitive(BUTTON_TEXT_OK);
//                } else if (searchTextCaseInsensitive(BUTTON_TEXT_TRY_AGAIN)) {
//                    logging("Found a TRY AGAIN button and will press it.");
//                    clickOnTextCaseInsensitive(BUTTON_TEXT_TRY_AGAIN);
//                }

                waitForProgressBar();
                if (solo.waitForView(classToLoad, noObjects, timeout)) {
                    logging("Class " + classToLoad + " was loaded correctly.");
                    return true;
                }
            }
            logging(descriptionOfViewLoading);
            reportError("Was not able to load view " + classToLoad);
        }
        logging("Class " + classToLoad + " was loaded correctly.");
        return true;
    }



    /**
     * Waits for a specified view to be loaded within the time out limit. If the view is not loaded, it will first
     * check if there is a progress bar (checking for the standard Android progress bar)
     * and then wait for it to disappear (or the function will eventually
     * report an error) and then will check if there is any choice to try again.
     * @param classToLoad              The class to load. This needs to be a subclass of android.view.View.
     * @param noObjects                The number of objects of the specified class that this method will expect.
     * @param numberTries              Use this parameter to allow multiple attempts to find the View. Useful if network conditions are suboptimal.
     * @param descriptionOfViewLoading A description of the view. This String will be printed in the logfile if the View is not loaded correctly.
     * @param progressbarClass         The class that the progress bar is contained in.
     * @return True if the View is loaded successfully.
     */
    protected <T extends View> boolean loadView(final Class<T> classToLoad, final int noObjects, final int numberTries, final String descriptionOfViewLoading, final Class<T> progressbarClass) {
        logging("Will wait for " + classToLoad + " to be loaded.");
        if (!solo.waitForView(classToLoad, noObjects, timeout)) {
            logging("The first attmept to load the view timed out");
            for (int i = 0; i < numberTries; i++) {
                logging("This is try number " + i + " that we have made so far.");
                //The below checks should search for a way to reconnect if there is any
                //This could be that the application gives you the option to reload etc.
                //Below are two examples of buttons that may appear when having connection issues.
                //Needs to be adjusted with respect of application under test.

//                if (searchTextCaseInsensitive(BUTTON_TEXT_OK)) {
//                    logging("Found a OK button and will press it.");
//                    clickOnTextCaseInsensitive(BUTTON_TEXT_OK);
//                } else if (searchTextCaseInsensitive(BUTTON_TEXT_TRY_AGAIN)) {
//                    logging("Found a TRY AGAIN button and will press it.");
//                    clickOnTextCaseInsensitive(BUTTON_TEXT_TRY_AGAIN);
//                }

                waitForProgressBar(progressbarClass);
                if (solo.waitForView(classToLoad, noObjects, timeout)) {
                    logging("Class " + classToLoad + " was loaded correctly.");
                    return true;
                }
            }
            logging(descriptionOfViewLoading);
            reportError("Was not able to load view " + classToLoad);
        }
        logging("Class " + classToLoad + " was loaded correctly.");
        return true;
    }

    /**
     * Returns a random number between 0 and the provided parameter-1
     * @param i This -1 will be the highest possible random number returned.
     * @return A randomly generated number.
     */
    public int getRandomNumber(int i){
        Random rn = new Random();
        return rn.nextInt(i);
    }

    /**
     * This method will simulate a done/enter button press.Robotium will not be able to communicate
     * with anything else than the launched application (the class launced).
     * This will give problems when typing in text and the soft keyboard is up which this method fixes.
     */
    public void ImeDone() //throws Exception
    {
        //Grab a reference to your EditText.  This code grabs the first Edit Text in the Activity
        //Alternatively, you can get the EditText by resource id or using a method like getCurrentEditTexts()
        //Make sure it's final, we'll need it in a nested block of code.
        final EditText editText = solo.getEditText(0);

        //Create a runnable which triggers the onEditorAction callback
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                editText.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        };

        //Use Solo to get the current activity, and pass our runnable to the UI thread.
        solo.getCurrentActivity().runOnUiThread(runnable);
    }


}
