package com.hassan.android.fyp_app_final;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

// TODO: documentation
public class UserHome extends AppCompatActivity {

    // Main variables
    private String userType;
    private String userFirstNname;
    private String userLastNname;
    private String userDesignation;
    private String userID;
    private static boolean listDestroyed = false;

    // UI elements
    private LinearLayoutCompat fragmentHolder;
    private TextView usernameTextVeiw;
    private TextView userIDTextVeiw;
    private TextView userDesignationTextVeiw;

    // Fragments
    private FRGTeacherScheduleList teacherScheduleListFragment;
    private FRGHODTabPages hodTabPagesFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        fragmentHolder = findViewById(R.id.main_fragment_space);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        userType = "";
        userFirstNname= "";
        userLastNname= "";
        userDesignation= "";
        userID = "";
        Intent mainIntnet = getIntent();
        userFirstNname = mainIntnet.getStringExtra("userFirstName");
        userLastNname = mainIntnet.getStringExtra("userLastName");
        userDesignation = mainIntnet.getStringExtra("userDesignation");
        userID = mainIntnet.getStringExtra("userID");

        //Set text boxes that will carry details
        usernameTextVeiw = findViewById(R.id.tv_user_name);
        userIDTextVeiw = findViewById(R.id.tv_user_ID);
        userDesignationTextVeiw = findViewById(R.id.tv_user_designation);

        //Fill Details
        usernameTextVeiw.setText(userFirstNname + " " + userLastNname);
        userIDTextVeiw.setText(userID);
        if (userDesignation.equals(MainActivity.TEACHER_DESIGNATION_ASST_PROF))
            userDesignationTextVeiw.setText("Assistant Professor");
        else if (userDesignation.equals(MainActivity.TEACHER_DESIGNATION_HOD))
            userDesignationTextVeiw.setText("Professor / HOD");

        //initialize fragment based on account type
        userType = mainIntnet.getStringExtra("userType");
        if (userType.equals(MainActivity.ACCOUNT_TYPE_TEACHER)) {
            teacherScheduleListFragment = new FRGTeacherScheduleList();
            teacherScheduleListFragment.setUserID(userID);
            transaction.add(R.id.main_fragment_space, teacherScheduleListFragment);
            transaction.commit();
        } else if (userType.equals(MainActivity.ACCOUNT_TYPE_HOD)) {
            hodTabPagesFragment = new FRGHODTabPages();
            hodTabPagesFragment.setUserID(userID);
            transaction.add(R.id.main_fragment_space, hodTabPagesFragment);
            transaction.commit();
        }

        // Set the listDestroyed variable to false
        listDestroyed = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Intent menuIntent = getIntent();
        if (menuIntent.getStringExtra("userType").equals(MainActivity.ACCOUNT_TYPE_TEACHER))
            getMenuInflater().inflate(R.menu.teacher_action_menu, menu);
        else
            getMenuInflater().inflate(R.menu.user_action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            onLogOutClicked();
        }
        else if (userType.equals(MainActivity.ACCOUNT_TYPE_TEACHER) && item.getItemId() == R.id.request) {
            Log.d("formdebug", "Form showing...");
            onRequestClicked();
        } else if (item.getItemId() == R.id.refresh) {
            refreshClicked();
        }
        return super.onOptionsItemSelected(item);
    }

    //functionality for request button
    private void onRequestClicked() {
        // Initialize the form dialog and add userID into its arguments
        DialogFragment formDialog = new ExtraRequestFormDialog();
        Bundle bundle = new Bundle();
        ArrayList<CourseModel> courses = teacherScheduleListFragment.getCourses();

        if (courses.get(0).getCourseName().equals("NA")) {
            Toast.makeText(this, "No extra requests can be made.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Putting userID
        bundle.putString("userID", userID);

        // Putting course info
        ArrayList<String> courseNames = new ArrayList<>();
        ArrayList<String> courseTimes = new ArrayList<>();
        ArrayList<String> courseIDs = new ArrayList<>();

        for (int i = 0; i<courses.size(); i++) {
            courseIDs.add(courses.get(i).getCourseID());
            courseNames.add(courses.get(i).getCourseName());
            courseTimes.add(courses.get(i).getCourseDaySlot());
        }
        bundle.putStringArrayList("courseIDs", courseIDs);
        bundle.putStringArrayList("courseNames", courseNames);
        bundle.putStringArrayList("courseTimes", courseTimes);

        formDialog.setArguments(bundle);

        // Show the dialog
        formDialog.show(getSupportFragmentManager(), "formDialog");
    }

    //functionality for logout button
    private void onLogOutClicked() {
        AlertDialog.Builder logoutAlert = new AlertDialog.Builder(this);
        logoutAlert.setTitle("Confirm Logout");
        logoutAlert.setMessage("Are you sure you want to logout?");
        logoutAlert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listDestroyed = true;
                Intent mainActivityIntent = new Intent(UserHome.this, MainActivity.class);
                startActivity(mainActivityIntent);
            }
        });
        logoutAlert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(UserHome.this, "you chose to not logout.", Toast.LENGTH_SHORT).show();
            }
        });
        logoutAlert.show();
    }

    // Function that will refresh the activity
    private void refreshClicked() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        // Reloading the fragment loading process
        if (userType.equals(MainActivity.ACCOUNT_TYPE_TEACHER)) {
            // Remove old fragment
            transaction.remove(teacherScheduleListFragment);
            teacherScheduleListFragment = new FRGTeacherScheduleList();
            teacherScheduleListFragment.setUserID(userID);
            transaction.add(R.id.main_fragment_space, teacherScheduleListFragment);
            transaction.commit();
        } else if (userType.equals(MainActivity.ACCOUNT_TYPE_HOD)) {
            // Remove old fragment
            transaction.remove(hodTabPagesFragment);
            hodTabPagesFragment = new FRGHODTabPages();
            hodTabPagesFragment.setUserID(userID);
            transaction.add(R.id.main_fragment_space, hodTabPagesFragment);
            transaction.commit();
        }
    }

    /**
     * Fucntion that will tell outside functions if the activity is listDestroyed or working
     * @return boolean: listDestroyed variable state
     */
    public static boolean isListDestroyed() {
        return listDestroyed;
    }

    /**
     * Function that sends its context to whoever asks for it
     */
    public static Context getContext() {
        return getContext();
    }
}
