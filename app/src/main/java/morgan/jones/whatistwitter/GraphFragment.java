package morgan.jones.whatistwitter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import twitter4j.Category;
import twitter4j.Status;

public class GraphFragment extends Fragment
{
    private View staticView;
    private View fragView;

    private RecyclerView recyclerView;
    private RecyclerView recyclerViewKeywords;
    private RecycleAdapterGraph recycleAdapter;
    private RecycleAdapterGraph recycleAdapterGraphKeywords;

    private DataManager dm;

    private ArrayList<Statistic> data;
    private ArrayList<Statistic> dataWords;
    private ArrayList<ArrayList<TweetThread>> allThreads;
    private ArrayList<List<Status>> allStatuses;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        if (staticView == null)
        {
            fragView = inflater.inflate(R.layout.graph_fragment, container, false);
            staticView = fragView;
        }
        else
        {
            fragView = staticView;
        }

        recyclerView = fragView.findViewById(R.id.graph_recycleview);
        recyclerViewKeywords = fragView.findViewById(R.id.graph_recycleview_keywords);

        dm = new DataManager();
        FileReader.readData(getContext(), dm);
        FileReader.readExternalFiles(getContext(), dm);

        allThreads = new ArrayList<>();
        allStatuses = MainActivity.tweetStorage.getTweets();
        data = new ArrayList<>();
        dataWords = new ArrayList<>();

        readData();
        HomeActivity.setupProgress(false);
        return fragView;
    }

    private void loadData()
    {
        // Total number of threads
        int totalThreads = 0;
        for (ArrayList<TweetThread> thread : allThreads)
        {
            totalThreads += thread.size();
        }
        data.add(new Statistic("Total Threads", Integer.toString(totalThreads)));

        // Average threads
        int averageThreads = totalThreads / allThreads.size();
        data.add(new Statistic("Average Threads per Category", Integer.toString(
                averageThreads)));

        // Average tweets per thread
        int averageTweets = 0;
        for (List<Status> s : allStatuses)
        {
            averageTweets += s.size();
        }
        averageTweets = averageTweets / totalThreads;
        data.add(new Statistic("Average Tweets per Thread", Integer.toString(averageTweets)));

        // New tweets today
        int todaysTweets = 0;
        // Dates
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date yesterday = calendar.getTime();

        for (List<Status> list : allStatuses)
        {
            for (Status s : list)
            {
                if (s.getCreatedAt().after(yesterday))
                {
                    todaysTweets ++;
                }
            }
        }
        data.add(new Statistic("Tweets today", Integer.toString(todaysTweets)));

        // Mentions
        data.add(new Statistic("Mentions", Integer.toString(MainActivity.tweetStorage
                .getMentions())));

        // Most popular keywords overall
        dataWords = DataProcessor.getMostPopWords(allThreads, dm);

        setRecycler();
    }

    private void setRecycler()
    {
        // Numbers
        recycleAdapter = new RecycleAdapterGraph(fragView.getContext(), data);

        final RecyclerView.LayoutManager lm = new LinearLayoutManager(fragView.getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(lm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recycleAdapter);

        // Keywords
        recycleAdapterGraphKeywords = new RecycleAdapterGraph(fragView.getContext(), dataWords);
        final RecyclerView.LayoutManager lm2 = new LinearLayoutManager(fragView.getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerViewKeywords.setLayoutManager(lm2);
        recyclerViewKeywords.setItemAnimator(new DefaultItemAnimator());
        recyclerViewKeywords.setAdapter(recycleAdapterGraphKeywords);
    }

    private void readData()
    {
        for (int i = 0; i < allStatuses.size(); i++)
        {
            List<Status> list = allStatuses.get(i);
            if(list.size() > 0)
            {
                if (!dm.getCategories().get(i).getName().equals("Unassigned") || !dm.getCategories()
                        .get(i).getName().equals("Mentions"))
                {
                    allThreads.add(DataProcessor.getThreads(list, dm));
                }
            }
        }

        loadData();
    }
}
