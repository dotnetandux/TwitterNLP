package morgan.jones.whatistwitter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import twitter4j.Status;

public class TweetThread implements Serializable
{
    private List<Status> tweets;
    private ArrayList<String> keywords;
    private String emotion;

    public TweetThread()
    {
        tweets = new ArrayList<>();
        keywords = new ArrayList<>();
            emotion = "NEUTRAL";
    }

    public List<Status> getTweets()
    {
        return tweets;
    }

    public void setTweets(List<Status> tweets)
    {
        this.tweets = tweets;
    }

    public ArrayList<String> getKeywords()
    {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords)
    {
        this.keywords = keywords;
    }

    public String getEmotion()
    {
        return emotion;
    }

    public void setEmotionPostivie()
    {
        this.emotion = "POSITIVE";
    }

    public void setEmotionNone()
    {
        this.emotion = "NEUTRAL";
    }

    public void setEmotionNegative()
    {
        this.emotion = "NEGATIVE";
    }
}
