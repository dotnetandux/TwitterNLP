package morgan.jones.whatistwitter;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;

public class ThreadFragment extends Fragment
{
    private static View staticView = null;
    private View fragView;

    private RecyclerView recyclerView;
    private RecycleAdapter recycleAdapter;
    private TweetThread currentThread;

    private TextView keywords;
    private SearchView searchView;
    private TextView emotion;

    private List<Status> list = null;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState)
    {
        if (staticView == null)
        {
            fragView = inflater.inflate(R.layout.category_fragment, container, false);
            staticView = fragView;
        }
        else
        {
            fragView = staticView;
        }

        recyclerView = fragView.findViewById(R.id.category_frag_recycle);
        searchView = fragView.findViewById(R.id.search_view);
        emotion = fragView.findViewById(R.id.category_emotion);

        currentThread = (TweetThread) getArguments().getSerializable("THREAD");
        list = currentThread.getTweets();
        keywords = fragView.findViewById(R.id.category_name_menu);

        String s = "";

        if (currentThread.getKeywords().get(0).equals("UNASSIGNED"))
        {
            s = "Unassigned Tweets";
            keywords.setTextSize(25);
        }
        else
        {
            for (String words : currentThread.getKeywords())
            {
                if (!s.contains("\"" + words + "\""))
                {
                    s += "\"" + words + "\" ";
                }
            }

            keywords.setTextSize(15);
        }

        keywords.setText(s);

        setRecyclerView();
        setSearchView();
        setEmotion();

        return fragView;
    }

    private void setEmotion()
    {
        emotion.setVisibility(View.VISIBLE);
        emotion.setEnabled(true);
        emotion.setText(currentThread.getEmotion());

        switch (currentThread.getEmotion())
        {
            case "POSITIVE":
                emotion.setBackgroundColor(Color.GREEN);
                break;
            case "NEGATIVE":
                emotion.setBackgroundColor(Color.RED);
                    break;
            case "NEUTRAL":
                emotion.setBackgroundColor(Color.TRANSPARENT);
                break;
        }
    }

    private void setSearchView()
    {
        searchView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                searchView.setIconified(false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String s)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s)
            {
                s = s.toLowerCase();
                ArrayList<Status> matches = new ArrayList<>();

                if (s.equals("") || s == null)
                {
                    recycleAdapter = new RecycleAdapter(fragView.getContext(),
                            DataProcessor.getSortedTimeline(list),
                            currentThread.getKeywords());
                    recyclerView.setAdapter(recycleAdapter);
                }
                else
                {
                    for (Status tweet : list)
                    {
                        if (tweet.getText().toLowerCase().contains(s))
                        {
                            matches.add(tweet);
                        }
                    }

                    recycleAdapter = new RecycleAdapter(fragView.getContext(),
                            DataProcessor.getSortedTimeline(matches), currentThread.getKeywords());
                    recyclerView.setAdapter(recycleAdapter);
                }

                return false;
            }
        });
    }

    private void setRecyclerView()
    {
        list = DataProcessor.getSortedTimeline(list);
        recycleAdapter = new RecycleAdapter(fragView.getContext(),
                DataProcessor.getSortedTimeline(list), currentThread.getKeywords());

        RecyclerView.LayoutManager lm = new LinearLayoutManager(fragView.getContext());
        recyclerView.setLayoutManager(lm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recycleAdapter);
    }
}
