package generic.test;

import android.view.View;
import android.widget.EditText;

public class testCase extends applicationSpecificMethods {

    public testCase() throws ClassNotFoundException {
        super();
    }

    public void testCase() {
        waitForApplicationToStart(5000);
        //The below code is specific for the imdb application but serve as an example.
        getAndClickOnView("com.imdb.mobile:id/search", 0);
        solo.sleep(2000);
        enterTextInField("Test Pilot", 0);
        solo.sleep(5000);
        searchAndReport(searchTextCaseInsensitive("Search Results"), "Made a search.", "Was not able to confirm that a search was made.");
        searchAndReport(searchTextCaseInsensitive("Test Pilot"), "The requested search text was found during the search.", "Could not find the text which was searched for.");
        solo.clickOnText("Test Pilot");
        solo.sleep(5000);

//        //The below code will work for the skanetrafiken application.
//        solo.clickOnImage(0);
//        getAndClickOnView("se.fskab.android.reseplaneraren:id/travelplan_timefield", 0);
//        solo.sleep(2000);
//        getAndClickOnView("android:id/button3", 0);
//        getAndClickOnView("se.fskab.android.reseplaneraren:id/travelplan_stopfieldfrom", 0);
//        solo.enterText(0, "Malmö");
//        solo.sleep(2000);
//        searchAndReport(searchTextCaseInsensitive("Malmö C"), "Found the correct page.", "Didn't find the correct page.");
//        clickOnTextCaseInsensitive("Malmö C");
//        getAndClickOnView("se.fskab.android.reseplaneraren:id/travelplan_stopfieldto", 0);
//        solo.enterText(0, "Lund");
//        solo.sleep(2000);
//        searchAndReport(searchTextCaseInsensitive("Lund C"), "Found the correct page.", "Didn't find the correct page.");
//        clickOnTextCaseInsensitive("Lund C");
//        getAndClickOnView("se.fskab.android.reseplaneraren:id/menu_search", 0);
//        solo.sleep(5000);
//        searchAndReport(searchTextCaseInsensitive("Connection alternatives"), "Got to the correct page.", "Didn't go to the correct page.");

        //If we reach this point, the test case has been a success and we can report that.
        reportSuccess();

    }

}