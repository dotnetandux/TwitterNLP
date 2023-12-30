package morgan.jones.whatistwitter;

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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;

public class CategoryFragment extends Fragment
{
    private static View staticView = null;
    private View fragView;

    private RecyclerView recyclerView;
    private RecycleAdapterThreads recycleAdapter;
    private TextView categoryName;
    private SearchView searchView;
    private TextView categoryEmotion;

    //private TwitterManager twitterManager;
    private Category currentCategory;
    private DataManager dataManager;

    private ArrayList<TweetThread> allThreads;
    private List<Status> list = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
        searchView.setQuery("", false);
        categoryName = fragView.findViewById(R.id.category_name_menu);
        categoryEmotion = fragView.findViewById(R.id.category_emotion);

        //twitterManager = new TwitterManager(fragView.getContext());
        currentCategory = (Category) getArguments().getSerializable("CATEGORY");
        dataManager = new DataManager();
        FileReader.readData(getContext(), dataManager);
        FileReader.readExternalFiles(getContext(), dataManager);

        setRecyclerView();
        setSearch();
        setCategory();
        HomeActivity.setupProgress(false);

        return fragView;
    }

    private void setSearch()
    {
        searchView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
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
                if (s.equals("") || s == null)
                {
                    recycleAdapter = new RecycleAdapterThreads(fragView.getContext(), allThreads);
                    recyclerView.setAdapter(recycleAdapter);
                }
                else
                {
                    s = s.toLowerCase();
                    ArrayList<TweetThread> matches = new ArrayList<>();

                    for (TweetThread t : allThreads)
                    {
                        for (String x : t.getKeywords())
                        {
                            if (x.toLowerCase().startsWith(s.toLowerCase()))
                            {
                                matches.add(t);
                            }
                        }
                    }

                    recycleAdapter = new RecycleAdapterThreads(fragView.getContext(), matches);
                    recyclerView.setAdapter(recycleAdapter);
                }

                return false;
            }
        });
    }

    private void setCategory()
    {
        categoryName.setText(currentCategory.getName());
        categoryEmotion.setEnabled(false);
        categoryEmotion.setVisibility(View.INVISIBLE);

        ViewGroup.LayoutParams params = categoryEmotion.getLayoutParams();
        params.height = 0;
        categoryEmotion.setLayoutParams(params);
    }

    private void setRecyclerView()
    {
        list = null;
        for (int i = 0; i < dataManager.getCategories().size(); i++)
        {
            if (dataManager.getCategories().get(i).getName().equals(currentCategory.getName()))
            {
                if (i >= MainActivity.tweetStorage.getTweets().size())
                {
                    Toast.makeText(getContext(), "Error: No Tweets", Toast.LENGTH_LONG).show();
                }
                else
                {
                    list = MainActivity.tweetStorage.getTweets().get(i);
                }
            }
        }

        if (list == null)
        {
            HomeActivity activity = (HomeActivity) fragView.getContext();
            activity.reloadHome(0);
        }
        else if (list.size() == 0)
        {
            HomeActivity activity = (HomeActivity) fragView.getContext();
            activity.reloadHome(0);
        }
        else
        {
            allThreads = DataProcessor.getThreads(list, dataManager);
            recycleAdapter = new RecycleAdapterThreads(fragView.getContext(), allThreads);

            RecyclerView.LayoutManager lm = new LinearLayoutManager(fragView.getContext());
            recyclerView.setLayoutManager(lm);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(recycleAdapter);
        }
    }
}
