package morgan.jones.whatistwitter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.ArrayList;
import java.util.List;

import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterManager
{
    private final static String CONSUMER_KEY = "key";
    private final static String CONSUMER_SECRET = "key";
    private final static String ACCESS_TOKEN = "key";
    private final static String ACCESS_SECRET = "key";

    private twitter4j.Twitter twitter;
    private AccessToken accessToken;
    private RequestToken requestToken;
    private String oAuth;

    private String userID;
    private User user;

    private Context context;

    private List<Status> statuses = null;
    private List<User> friendList;
    private List<User> totalFriendList;

    public TwitterManager(Context context)
    {
        friendList = null;
        totalFriendList = new ArrayList<>();
        this.context = context;
        userID = "Test";
        oAuth = "oAuth_verifier";

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey(CONSUMER_KEY);
        cb.setOAuthConsumerSecret(CONSUMER_SECRET);
        //cb.setOAuthAccessToken(ACCESS_TOKEN);
        //cb.setOAuthAccessTokenSecret(ACCESS_SECRET);

        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
    }

    public void configure()
    {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey(CONSUMER_KEY);
        cb.setOAuthConsumerSecret(CONSUMER_SECRET);
        cb.setOAuthAccessToken(ACCESS_TOKEN);
        cb.setOAuthAccessTokenSecret(ACCESS_SECRET);

        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
    }

    public String getoAuth()
    {
        return oAuth;
    }

    public void setoAuth(String oAuth)
    {
        this.oAuth = oAuth;
    }

    public twitter4j.Twitter getTwitter()
    {
        return twitter;
    }

    public void setTwitter(twitter4j.Twitter twitter)
    {
        this.twitter = twitter;
    }

    public AccessToken getAccessToken()
    {
        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken)
    {
        this.accessToken = accessToken;
        this.twitter.setOAuthAccessToken(accessToken);
    }

    public RequestToken getRequestToken() {
        return requestToken;
    }

    public void setRequestToken(RequestToken requestToken)
    {
        this.requestToken = requestToken;
    }

    public String getUserID()
    {
        return userID;
    }

    public void setUserID(String userID)
    {
        this.userID = userID;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public List<User> getFollowees()
    {
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                long cursor = -1;
                while (cursor != 0)
                {
                    try
                    {
                        friendList = twitter.getFriendsList(userID, cursor, 200);
                        cursor = ((PagableResponseList<User>) friendList).getNextCursor();
                        totalFriendList.addAll(friendList);
                    }
                    catch (twitter4j.TwitterException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

        return totalFriendList;
    }

    public List<Status> getTimeline(DataManager dm)
    {
        final DataManager dataManager = dm;

        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Paging p = new Paging();
                    p.setCount(100);

                    for (Category category : dataManager.getCategories())
                    {
                        if (!category.getName().equals("Mentions"))
                        {
                            statuses = twitter.getUserTimeline(category.getUserIDs().get(0), p);
                            if (category.getName().equals("Unassigned"))
                            {
                                boolean x = true;
                            }
                            for (int i = 1; i < category.getUserIDs()
                                    .size(); i++)
                            {
                                List<Status> x = twitter.getUserTimeline(category.getUserIDs().
                                        get(i), p);
                                statuses.addAll(x);
                            }
                        }
                        else
                        {
                            statuses = twitter.getMentionsTimeline(p);
                            MainActivity.tweetStorage.setMentions(statuses.size());
                        }

                        MainActivity.tweetStorage.getTweets().add(statuses);
                        statuses = null;
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        return statuses;
    }
}
