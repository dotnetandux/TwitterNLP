package morgan.jones.whatistwitter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import java.util.List;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class MainActivity extends AppCompatActivity
{
    private TwitterManager tm;

    private Button loadButton;
    private Button loginButton;

    private boolean returningWithResult;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private ImageView logo;
    private ProgressBar progressBar;
    private ViewGroup.LayoutParams params1;
    private ViewGroup.LayoutParams params2;

    private DataManager dataManager;

    public static TweetStorage tweetStorage = new TweetStorage();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Super Code
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        returningWithResult = false;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        sharedPreferences = getApplicationContext().getSharedPreferences
                ("Prefs", 0);
        editor = sharedPreferences.edit();

        // Ini
        tm = new TwitterManager(this);
        dataManager = new DataManager();
        FileReader.writeData(this, dataManager);
        FileReader.readData(this, dataManager);
        // updateUI
        loadButton = findViewById(R.id.load_button);
        loginButton = findViewById(R.id.login_button);

        loadButton.setEnabled(false);
        loadButton.setVisibility(View.INVISIBLE);

        checkLoggedIn();
        setLoad();
        setupLogin();
        setupLogo(false);

        downloadTweets(dataManager, tm);
    }

    private void checkLoggedIn()
    {
        boolean x = sharedPreferences.getBoolean("is_logged_in", false);
        x = true;
        if (x)
        {
            tm.configure();
            try
            {
                tm.setAccessToken(tm.getTwitter().getOAuthAccessToken());
                AccessToken accessToken = tm.getAccessToken();
                tm.setUser(tm.getTwitter().showUser(accessToken.getUserId()));
                tm.setUserID(tm.getUser().getScreenName());

                downloadTweets(dataManager, tm);
                setupDownload();
                loadButton.setVisibility(View.VISIBLE);
                loadButton.setEnabled(true);
                loginButton.getBackground().setAlpha(100);
                loginButton.setEnabled(false);
            }
            catch (twitter4j.TwitterException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void setupLogin()
    {
        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                twitter4j.Twitter twitter = tm.getTwitter();
                RequestToken rt = null;

                try
                {
                    rt = twitter.getOAuthRequestToken();
                    Intent intent = new Intent(getApplicationContext(), TwitterLogin.class);
                    tm.setRequestToken(rt);

                    intent.putExtra(TwitterLogin.EXTRA_URL, rt.getAuthenticationURL());
                    startActivityForResult(intent, 100);
                }
                catch (twitter4j.TwitterException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            try
            {
                tm.configure();
                tm.setAccessToken(tm.getTwitter().getOAuthAccessToken());
                AccessToken accessToken = tm.getAccessToken();
                tm.setUser(tm.getTwitter().showUser(accessToken.getUserId()));
                tm.setUserID(tm.getUser().getScreenName());

                downloadTweets(dataManager, tm);
                setupDownload();
                loadButton.setVisibility(View.VISIBLE);
                loadButton.setEnabled(true);
                loginButton.getBackground().setAlpha(100);
                loginButton.setEnabled(false);

                editor.putBoolean("is_logged_in", true);
                editor.commit();
            }
            catch (twitter4j.TwitterException e)
            {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupLogo(boolean isLoad)
    {
        progressBar = findViewById(R.id.progress_bar_main);
        logo = findViewById(R.id.app_logo);
        params1 = logo.getLayoutParams();
        params2 = progressBar.getLayoutParams();

        params2.height = 0; params2.width = 0;
        params1.height = 200; params1.width = 200;

        if (!isLoad)
        {
            progressBar.setVisibility(View.INVISIBLE);
            logo.setVisibility(View.VISIBLE);

            progressBar.setLayoutParams(params2);
            params1.height = 400; params1.width = 400;
            logo.setLayoutParams(params1);
        }
        else
        {
            progressBar.setVisibility(View.VISIBLE);
            logo.setVisibility(View.INVISIBLE);

            progressBar.setLayoutParams(params1);
            logo.setLayoutParams(params2);
        }
    }

    private void setupDownload()
    {
        tm.getFollowees();
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                List<User> followees = null;
                while ((followees = tm.getFollowees()) == null || followees.size() == 0)
                {
                    try
                    {
                        Thread.sleep(2000);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                for (User userID : followees)
                {
                    String s = userID.getScreenName();
                    boolean found = false;

                    for (Category c : dataManager.getCategories())
                    {
                        for (String id : c.getUserIDs())
                        {
                            if (userID.getScreenName().toLowerCase().equals(id.toLowerCase()))
                            {
                                found = true;
                            }
                        }
                    }

                    if (!found)
                    {
                        for (Category c : dataManager.getCategories())
                        {
                            if (c.getName().equals("Unassigned"))
                            {
                                c.getUserIDs().add(userID.getScreenName());
                            }
                        }
                    }
                }

                FileReader.writeData(getApplicationContext(), dataManager);
            }
        });
        thread.start();
    }

    public void downloadTweets(final DataManager
            dataManager, final TwitterManager twitterManager)
    {
        twitterManager.getTimeline(dataManager);
    }

    private void setLoad()
    {
        loadButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                setupLogo(true);

                Thread thread = new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            Thread.sleep(15000);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        tweetStorage.setTwitter(tm.getTwitter());
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                    }
                });
                thread.start();
            }
        });
    }
}
