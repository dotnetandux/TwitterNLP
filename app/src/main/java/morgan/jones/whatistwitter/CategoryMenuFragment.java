package morgan.jones.whatistwitter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import twitter4j.PagableResponseList;
import twitter4j.User;

public class CategoryMenuFragment extends Fragment
{
    private static View staticView = null;
    private View fragView;

    private RecyclerView recyclerView;
    private RecycleAdapterUsers recycleAdapter;
    private EditText categoryName;
    private SearchView searchView;
    private TextView categoryEmotion;

    private Category currentCategory;
    private DataManager dataManager;

    private String originalName;

    private ArrayList<String> userIDs;

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
        categoryName = fragView.findViewById(R.id.category_name_menu);
        categoryEmotion = fragView.findViewById(R.id.category_emotion);

        currentCategory = (Category) getArguments().getSerializable("CATEGORY");
        dataManager = new DataManager();
        FileReader.readData(getContext(), dataManager);

        originalName = currentCategory.getName();

        final ArrayList<PagableResponseList<User>> ids = new ArrayList<>();

        setRecyclerView();
        setSearch();
        setEditName();

        HomeActivity.setupProgress(false);

        return fragView;
    }

    private void setEditName()
    {
        if (originalName.toLowerCase().equals("unassigned") || originalName.toLowerCase()
                .equals("mentions"))
        {
            categoryName.setEnabled(false);
        }
        else
        {
            categoryName.setEnabled(true);

            categoryName.setOnFocusChangeListener(new View.OnFocusChangeListener()
            {
                @Override
                public void onFocusChange(View view, boolean b)
                {
                    String newName = categoryName.getText().toString().toLowerCase();
                    if(!(newName.equals(originalName)))
                    {
                        for (Category c : dataManager.getCategories())
                        {
                            if (c.getName().equals(newName))
                            {
                                Toast.makeText(getContext(), "Category already present",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else if (c.getName().equals(originalName))
                            {
                                c.setName(categoryName.getText().toString());
                                FileReader.writeData(getContext(), dataManager);
                                originalName = categoryName.getText().toString();
                            }
                        }
                    }
                }
            });
        }
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
                Category newCat = new Category(currentCategory.getName());

                if (s.equals("") || s == null)
                {
                    recycleAdapter = new RecycleAdapterUsers(fragView.getContext(),
                            currentCategory);
                    recyclerView.setAdapter(recycleAdapter);
                }
                else
                {
                    s = s.toLowerCase();
                    ArrayList<String> matches = new ArrayList<>();

                    for (String string : userIDs)
                    {
                        if (string.toLowerCase().startsWith(s.toLowerCase()))
                        {
                            matches.add(string);
                        }
                    }

                    newCat.setUserIDs(matches);
                    recycleAdapter = new RecycleAdapterUsers(fragView.getContext(), newCat);
                    recyclerView.setAdapter(recycleAdapter);
                }

                return false;
            }
        });
    }

    private void setCategory()
    {
        categoryName.setText(currentCategory.getName());
        userIDs = currentCategory.getUserIDs();

        ViewGroup.LayoutParams params = categoryEmotion.getLayoutParams();
        params.height = 0;
        categoryEmotion.setLayoutParams(params);
    }

    private void setRecyclerView()
    {
        setCategory();
        recycleAdapter = new RecycleAdapterUsers(fragView.getContext(), currentCategory);

        RecyclerView.LayoutManager lm = new LinearLayoutManager(fragView.getContext());
        recyclerView.setLayoutManager(lm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recycleAdapter);
    }
}
