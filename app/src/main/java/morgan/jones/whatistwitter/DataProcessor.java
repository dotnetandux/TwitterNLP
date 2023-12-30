package morgan.jones.whatistwitter;

import android.app.AlertDialog;
import android.media.MediaCodec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.Status;

public class DataProcessor
{
    private static ArrayList<String> filterWords(ArrayList<String> tweet, DataManager dm)
{
    ArrayList<String> resultKeywords = new ArrayList<>();
    String[] puncs = new String[]{";", ":", ",", ".", "?", "-", "_", "+", "~", "--", ""};

    // Emoji unicodes
    final String emoji = ("(?:[\uD83C\uDF00-\uD83D\uDDFF]|[\uD83E\uDD00-\uD83E\uDDFF]|" +
            "[\uD83D\uDE00-\uD83D\uDE4F]|[\uD83D\uDE80-\uD83D\uDEFF]|" +
            "[\u2600-\u26FF]\uFE0F?|[\u2700-\u27BF]\uFE0F?|\u24C2\uFE0F?|" +
            "[\uD83C\uDDE6-\uD83C\uDDFF]{1,2}|" + "[\uD83C\uDD70\uD83C\uDD71\uD83C\uDD7E" +
            "\uD83C\uDD7F\uD83C\uDD8E\uD83C\uDD91-\uD83C\uDD9A]\uFE0F?|" +
            "[\u0023\u002A\u0030-\u0039]\uFE0F?\u20E3|[\u2194-\u2199\u21A9-\u21AA]" +
            "\uFE0F?|[\u2B05-\u2B07\u2B1B\u2B1C\u2B50\u2B55]\uFE0F?|" +
            "[\u2934\u2935]\uFE0F?|[\u3030\u303D]\uFE0F?|[\u3297\u3299]\uFE0F?|" +
            "[\uD83C\uDE01\uD83C\uDE02\uD83C\uDE1A\uD83C\uDE2F\uD83C\uDE32-" +
            "\uD83C\uDE3A\uD83C\uDE50\uD83C\uDE51]\uFE0F?|" + "[\u203C\u2049]\uFE0F?|" +
            "[\u25AA\u25AB\u25B6\u25C0\u25FB-\u25FE]\uFE0F?|" +
            "[\u00A9\u00AE]\uFE0F?|[\u2122\u2139]\uFE0F?|\uD83C\uDC04\uFE0F?|" +
            "\uD83C\uDCCF\uFE0F?|" +
            "[\u231A\u231B\u2328\u23CF\u23E9-\u23F3\u23F8-\u23FA]\uFE0F?)+");

    for (String word : tweet)
    {
        // Found means not keyword in final list
        boolean found = false;

        if (word.startsWith("#") || word.startsWith("@")){}
        // Hashtags or handles are automatically keywords
        else
        {
            // Check for emoji
            Matcher matcher = Pattern.compile(emoji).matcher(word);
            if (matcher.find())
            {
                found = true;
            }

            // If its a link
            if (word.startsWith("http"))
            {
                found = true;
            }

            // Check if its a punctuation
            for (String punc : puncs)
            {
                if (word.equals(punc))
                {
                    found = true;
                }
            }

            // Check if its a stopword
            for (String x : dm.getStopwords())
            {
                if (word.toLowerCase().equals(x.toLowerCase()))
                {
                    found = true;
                }
            }

            // Check not already present
            if (resultKeywords.size() > 0)
            {
                for (int x = 0; x < resultKeywords.size(); x++)
                {
                    if (word.toLowerCase().equals(resultKeywords.get(x).toLowerCase()))
                    {
                        found = true;
                    }
                }
            }
        }

        if (!found)
        {
            resultKeywords.add(word);
        }
    }

    return highlightSpecificKeywords(resultKeywords);
}

    private static ArrayList<String> highlightSpecificKeywords(ArrayList<String> keywords)
    {
        ArrayList<String> result = new ArrayList<>();
        result.addAll(keywords);

        // Give # and @ more points

        for (String s : keywords)
        {
            if (s.startsWith("@") || s.startsWith("#"))
            {
                // Duplicate
                result.add(s);
            }
        }

        return result;
    }

    public static ArrayList<TweetThread> getThreads(List<Status> timeline, DataManager dm)
    {
        ArrayList<TweetThread> threadList = new ArrayList<>();
        String delimiter = " |\n|\t"; // Whitespace delimiter

        // Assign first tweet to first thread
        TweetThread newThread = new TweetThread();
        String[] tweetText = removePunctuation(timeline.get(0).getText()).split(delimiter);
        ArrayList<String> keywords = new ArrayList<>();
        for (String s : tweetText)
        {
            keywords.add(s);
        }
        keywords = filterWords(keywords, dm);
        // Add to thread
        newThread.setKeywords(keywords);
        newThread.getTweets().add(timeline.get(0));
        threadList.add(newThread);

        // Loop through every tweet
        for (int i = 1; i < timeline.size(); i++)
        {
            Status currentStatus = timeline.get(i);

            // Check not retweet
            if (!(currentStatus.getText().startsWith("RT")))
            {
                // Get words from tweet in focus
                tweetText = removePunctuation(currentStatus.getText()).split(delimiter);
                keywords = new ArrayList<>();
                for (String s : tweetText)
                {
                    keywords.add(s);
                }
                keywords = filterWords(keywords, dm);

                // If tweet has keywords
                if (keywords.size() > 0)
                {
                    boolean hasMatch = false;
                    boolean isRepeat = false;
                    TweetThread highestMatch = null;
                    double highest = 0;

                    // Compare against all existing threads
                    for (int x = 0; x < threadList.size(); x++)
                    {
                        TweetThread currentThread = threadList.get(x);

                        boolean found = false;
                        // Check tweet not already present in thread
                        for (Status status : currentThread.getTweets())
                        {
                            if (status.equals(currentStatus))
                            {
                                found = true;
                                isRepeat = true;
                            }
                        }

                        // If not a repeat then run comparison
                        if (!found)
                        {
                            double comparison = compareTweets(currentThread.getKeywords(),
                                    keywords);

                            // Minimum match value
                            if (comparison > 0.4)
                            {
                                if (comparison > highest)
                                {
                                    highest = comparison;
                                    highestMatch = currentThread;
                                }

                                hasMatch = true;
                            }
                        }
                    }

                    // If not repeated
                    if (!isRepeat)
                    {
                        if (highestMatch != null)
                        {
                            highestMatch.getTweets().add(currentStatus);
                        }

                        // If has no match, get own new thread
                        if (!hasMatch)
                        {
                            // New thread with no tweets
                            TweetThread secondThread = new TweetThread();
                            secondThread.setKeywords(keywords);
                            secondThread.getTweets().add(currentStatus);
                            threadList.add(secondThread);
                        }
                    }
                }
                else // If it doesn't have any keywords
                {
                    // New thread with no tweets
                    TweetThread thirdThread = new TweetThread();
                    thirdThread.setKeywords(keywords);
                    thirdThread.getTweets().add(currentStatus);
                    threadList.add(thirdThread);
                }
            }
        }

        threadList = organiseSingleTweets(threadList);
        countEmotiveWords(threadList, dm);

        return threadList;
    }

    private static String removePunctuation(String tweetText)
    {
        String result = "";
        String[] puncs = new String[]{";", ":", ",", ".", "?", "-", "_", "+", "~"};

        for (int i = 0; i < tweetText.length(); i++)
        {
            String character = String.valueOf(tweetText.charAt(i));
            boolean found = false;

            for (String x : puncs)
            {
                if (character.equals(x))
                {
                    found = true;
                }
            }

            if (!found)
            {
                result += character;
            }
        }

        //result = tweetText;
        return result;
    }

    public static ArrayList<Statistic> getMostPopWords(ArrayList<ArrayList<TweetThread>> allThreads,
                                                       DataManager dm)
    {
        ArrayList<Statistic> keywords = new ArrayList<>();
        ArrayList<Statistic> finalList = new ArrayList<>();

        // First keyword
        ArrayList<String> words = allThreads.get(0).get(0).getKeywords();
        keywords.add(new Statistic("1", words.get(0)));

        for (ArrayList<TweetThread> list : allThreads)
        {
            for (TweetThread thread : list)
            {
                words = thread.getKeywords();
                for (int i = 0; i < words.size(); i++)
                {
                    boolean found = false;
                    for (Statistic stat : keywords)
                    {
                        if (words.get(i).toLowerCase().equals(stat.getValue().toLowerCase()))
                        {
                            int x = Integer.valueOf(stat.getName());
                            x ++;
                            stat.setName(Integer.toString(x));
                            found = true;
                        }
                    }

                    if (!found)
                    {
                        if (!words.get(i).equals(""))
                        {
                            keywords.add(new Statistic("1", words.get(i)));
                        }
                    }
                }
            }
        }

        int highest;
        Statistic highStat;
        for (int x = 0; x <= 10; x++)
        {
            Statistic stat = null;
            highStat = null;
            highest = 1;

            for (int i = 0; i < keywords.size(); i++)
            {
                stat = keywords.get(i);

                if ((Integer.valueOf(stat.getName()) > highest))
                {
                    highest = Integer.valueOf(stat.getName());
                    highStat = stat;
                }
            }

            if (highStat != null)
            {
                finalList.add(new Statistic(highStat.getName() + " times", "\"" +
                        highStat.getValue() + "\""));
                keywords.remove(highStat);
            }
        }

        return finalList;
    }

    private static ArrayList<TweetThread> organiseSingleTweets(ArrayList<TweetThread> threads)
    {
        ArrayList<TweetThread> allThreads = new ArrayList<>();

        TweetThread uncategorised = new TweetThread();
        ArrayList<String> keywords = new ArrayList<>();
        keywords.add("UNASSIGNED");
        uncategorised.setKeywords(keywords);

        for (int i = 0; i < threads.size(); i++)
        {
            TweetThread thread = threads.get(i);
            if (thread.getTweets().size() == 1)
            {
                uncategorised.getTweets().add(thread.getTweets().get(0));
            }
            else
            {
                allThreads.add(thread);
            }
        }

        allThreads.add(uncategorised);

        return allThreads;
    }

    private static void countEmotiveWords(ArrayList<TweetThread> threads, DataManager dm)
    {
        String[] puncs = new String[]{";", ":", ",", ".", "?", "-", "_", "+", "~"};
        String delimeter = " |\n|\t";

        for (TweetThread thread : threads)
        {
            double percentPositiveThread = 0;
            double percentNegativeThread = 0;
            double amountOfWords = 0;

            for (Status status : thread.getTweets())
            {
                String words[] = status.getText().split(delimeter);

                double pTally = 0;
                double nTally = 0;
                double total = 0;

                for (String word : words)
                {
                    boolean found = false;

                    // Check not punctuation
                    for (String punc : puncs)
                    {
                        if (punc.equals(word.toLowerCase()))
                        {
                            found = true;
                        }
                    }
                    if (!found)
                    {
                        total++;
                    }

                    // Check for pos
                    for (String x : dm.getpWords())
                    {
                        if (x.toLowerCase().equals(word.toLowerCase()))
                        {
                            pTally++;
                        }
                    }

                    // Check for neg
                    for (String x : dm.getnWords())
                    {
                        if (x.toLowerCase().equals(word.toLowerCase()))
                        {
                            nTally++;
                        }
                    }

                    amountOfWords += total;
                    percentPositiveThread += pTally;
                    percentNegativeThread += nTally;
                }
            }

            // Percentage of words which are pos/neg in the thread
            percentPositiveThread = percentPositiveThread / amountOfWords;
            percentNegativeThread = percentNegativeThread / amountOfWords;

            if (percentPositiveThread > percentNegativeThread)
            {
                thread.setEmotionPostivie();
            }
            else if (percentNegativeThread > percentNegativeThread)
            {
                thread.setEmotionNegative();
            }
            else
            {
                thread.setEmotionNone();
            }
        }
    }

    public static double compareTweets(ArrayList<String> keywords, ArrayList<String> tweet2)
    {
        double cosine = 0;
        double magnitudeA = 0;
        double magnitudeB = 0;

        Map<String, Integer> a = getKeywordFrequency(keywords);
        Map<String, Integer> b = getKeywordFrequency(tweet2);

        HashSet<String> intersection = new HashSet<>(a.keySet());
        intersection.retainAll(b.keySet());

        for (String s : intersection)
        {
            cosine += a.get(s) * b.get(s);
        }

        for (String k : a.keySet())
        {
            magnitudeA += Math.pow(a.get(k), 2);
        }

        for (String k : b.keySet())
        {
            magnitudeB += Math.pow(b.get(k), 2);
        }

        return cosine / Math.sqrt(magnitudeA * magnitudeB);
    }

    private static Map<String, Integer> getKeywordFrequency(ArrayList<String> keywords)
    {
        Map<String, Integer> keywordsMap = new HashMap<>();

        for (String s : keywords)
        {
            Integer n = keywordsMap.get(s);
            if (n == null)
            {
                n = 1;
            }
            else
            {
                n++;
            }
            keywordsMap.put(s, n);
        }

        return keywordsMap;
    }

    public static List<Status> getSortedTimeline(List<Status> list)
    {
        List<Status> unsorted = list;
        List<Status> sorted = new ArrayList<>();
        Status focus = null;

        Boolean recent;
        int pos = list.size();

        // Checks there is more than 1 meeting
        if (unsorted.size() < 2)
        {
            sorted = list;
        }
        else
        {
            // While there is more than one meeting to be sorted
            while (unsorted.size() > 1)
            {
                recent = true;
                focus = unsorted.get(pos-1); // Meeting at last position

                // Check every meeting in list...
                for (int i = 0; i < unsorted.size(); i ++)
                {
                    //...Against every meeting in list
                    if (!focus.equals(unsorted.get(i)))
                    {
                        // Check if meeting i is the earliest on record
                        if (focus.getCreatedAt().before(unsorted.get(i).getCreatedAt()))
                        {
                            recent = false;
                        }
                    }
                }

                if (recent)
                {
                    // Add to sorted list and remove from 'need sorting list'
                    sorted.add(focus);
                    unsorted.remove(focus);
                    pos = unsorted.size();
                }
                else
                {
                    // Move back in list
                    pos = pos - 1;
                }
            }

            // Add last meeting to end of list
            if (unsorted.size() == 1)
            {
                sorted.add(unsorted.get(0));
            }
        }

        return sorted;
    }
}
