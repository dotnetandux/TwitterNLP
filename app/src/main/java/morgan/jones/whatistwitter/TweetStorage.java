package morgan.jones.whatistwitter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;

public class TweetStorage implements Serializable
{
    private ArrayList<List<Status>> downloadedTweets;
    private int mentions;
    private Twitter twitter;

    public Twitter getTwitter()
    {
        return twitter;
    }

    public void setTwitter(Twitter twitter)
    {
        this.twitter = twitter;
    }

    public int getMentions()
    {
        return mentions;
    }

    public void setMentions(int mentions)
    {
        this.mentions = mentions;
    }

    public TweetStorage()
    {
        this.downloadedTweets = new ArrayList<>();
    }

    public ArrayList<List<Status>> getTweets()
    {
        return downloadedTweets;
    }

    public void setTweets(ArrayList<List<Status>> tweets)
    {
        this.downloadedTweets = tweets;
    }
}
