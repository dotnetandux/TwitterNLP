package morgan.jones.whatistwitter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity
{
    private BottomNavigationView navBar;

    private int currentFragment;
    public static Fragment staticFragment = null;
    private Category currentCat = null;

    private DataManager dataManager;
    private Fragment previousFrag;

    private static ProgressBar progressBar;
    private static ViewGroup.LayoutParams params1;
    private static ViewGroup.LayoutParams params2;
    private static ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navBar = findViewById(R.id.navigation_bar);
        dataManager = new DataManager();
        //FileReader.writeData(this, dataManager);
        FileReader.readData(this, dataManager);
        FileReader.readExternalFiles(this, dataManager);

        progressBar = findViewById(R.id.progress_bar_home);
        constraintLayout = findViewById(R.id.home_constraint);

        setupNavigation();
        setupProgress(false);
    }

    private void setupNavigation()
    {
        BottomNavigationHelper.removeShift(navBar);

        navBar.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                runNavigation(item.getItemId());
                return false;
            }
        });

        currentFragment = 0;
        loadFragment(new HomeFragment());
        navBar.getMenu().getItem(1).setChecked(true);
    }

    private void runNavigation(int itemId)
    {
        FileReader.readData(this, dataManager);
        Bundle bundle = new Bundle();
        Fragment fragment = null;

        if (itemId != currentFragment)
        {
            currentFragment = itemId;
            switch (itemId)
            {
                case R.id.icon_home:
                    fragment = new HomeFragment();
                    navBar.getMenu().getItem(1).setChecked(true);
                    setupProgress(false);
                    break;
                case R.id.icon_settings:
                    fragment = new SettingsFragment();
                    navBar.getMenu().getItem(0).setChecked(true);
                    break;
                case R.id.icon_graph:
                    fragment = new GraphFragment();
                    navBar.getMenu().getItem(2).setChecked(true);
                    setupProgress(true);
                    break;
            }

            //fragment.setArguments(bundle);
            loadFragment(fragment);
        }
    }

    public static void setupProgress(boolean isLoad)
    {
        params1 = progressBar.getLayoutParams();
        params2 = progressBar.getLayoutParams();

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        params2.height = 0; params2.width = 0;
        params1.height = 200; params1.width = 200;

        if (!isLoad)
        {
            constraintSet.connect(R.id.fragment_placeholder, ConstraintSet.TOP,
                    R.id.home_constraint, ConstraintSet.TOP);
            constraintSet.applyTo(constraintLayout);

            progressBar.setVisibility(View.INVISIBLE);
            progressBar.setLayoutParams(params2);
        }
        else
        {
            constraintSet.connect(R.id.fragment_placeholder, ConstraintSet.TOP,
                    R.id.progress_bar_home, ConstraintSet.BOTTOM);
            constraintSet.applyTo(constraintLayout);

            progressBar.setVisibility(View.VISIBLE);
            progressBar.setLayoutParams(params1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void loadCategoryMenu(Category category)
    {
        currentFragment = 0;
        currentCat = category;
        navBar.getMenu().getItem(1).setChecked(false);

        Bundle bundle = new Bundle();
        bundle.putSerializable("CATEGORY", category);
        Fragment fragment = new CategoryMenuFragment();
        fragment.setArguments(bundle);
        loadFragment(fragment);
    }

    public void loadCategoryFragment(Category category)
    {
        currentFragment = 0;
        currentCat = category;
        navBar.getMenu().getItem(1).setChecked(false);
        setupProgress(true);

        Bundle bundle = new Bundle();
        bundle.putSerializable("CATEGORY", category);
        final Fragment fragment = new CategoryFragment();
        fragment.setArguments(bundle);

        //startActivityForResult(new Intent(this, SplashScreen.class), 0);

        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                loadFragment(fragment);
            }
        };
        //thread.start();
        loadFragment(fragment);
    }

    @Override
    public void onBackPressed()
    {
        switch (currentFragment)
        {
            case 0:
                runNavigation(R.id.icon_home);
                break;
            case 100:
                loadFragment(previousFrag);
                break;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        runNavigation(R.id.icon_home);
    }

    public void reloadHome(int x)
    {
        runNavigation(R.id.icon_home);
        if (x == 0)
        {
            Toast.makeText(this, "Feed empty. No threads.", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadThreadFragment(TweetThread thread)
    {
        currentFragment = 100;
        previousFrag = staticFragment;
        navBar.getMenu().getItem(1).setChecked(false);

        Bundle bundle = new Bundle();
        bundle.putSerializable("THREAD", thread);
        Fragment fragment = new ThreadFragment();
        fragment.setArguments(bundle);
        loadFragment(fragment);
    }

    private void loadFragment(Fragment fragment)
    {
        if (staticFragment != fragment)
        {
            if (fragment != null)
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_placeholder,
                        fragment).commitAllowingStateLoss();
                staticFragment = fragment;
            }
        }
        else
        {
            fragment = staticFragment;
        }
    }
}
