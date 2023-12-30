package morgan.jones.whatistwitter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created on 24/03/2019.
 *
 * @author Morgan Eifion Jones
 * @version 1.0
 */
public class AssignUsersFragment extends Fragment
{
    private static View staticView = null;
    private View fragView;

    private ImageButton previousBtn;
    private ImageButton nextBtn;
    private TextView catTitle;
    private RecyclerView recyclerView;

    private DataManager dataManager;
    private RecycleAdapterAssign recycleAdapter;
    private ArrayList<Category> categories;

    private int currentCategory;
    private ArrayList<String> unassigned;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        if (staticView == null)
        {
            fragView = inflater.inflate(R.layout.assign_users_fragment, container, false);
            staticView = fragView;
        }
        else
        {
            fragView = staticView;
        }

        previousBtn = fragView.findViewById(R.id.previous_cat);
        nextBtn = fragView.findViewById(R.id.next_cat);
        catTitle = fragView.findViewById(R.id.assign_cat_name);
        recyclerView = fragView.findViewById(R.id.assign_frag_recycle);

        dataManager = new DataManager();
        FileReader.readData(getContext(), dataManager);

        currentCategory = 0;
        categories = new ArrayList<>();

        for (Category c : dataManager.getCategories())
        {
            if (c.getName().equals("Unassigned"))
            {
                unassigned = c.getUserIDs();
            }
            else if (!c.getName().equals("Mentions"))
            {
                categories.add(c);
            }
        }

        HomeActivity.setupProgress(false);

        setupTitle();
        setButtons();
        setRecyclerView();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Assign users");
        builder.setMessage("The users displayed are currently unassigned. Check the box" +
                " next to the username to assign said user to the category titled at the top" +
                " of the screen");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

        return fragView;
    }

    private void setButtons()
    {
        previousBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                currentCategory --;
                setupTitle();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                currentCategory ++;
                setupTitle();
            }
        });
    }

    private void setupTitle()
    {
        if (currentCategory < categories.size())
        {
            setRecyclerView();

            nextBtn.setEnabled(true);
            if (currentCategory != 0)
            {
                previousBtn.setEnabled(true);
                previousBtn.getBackground().setAlpha(255);
            }
            else
            {
                previousBtn.setEnabled(false);
                previousBtn.getBackground().setAlpha(100);
            }

            catTitle.setText(categories.get(currentCategory).getName());
        }
        else if (currentCategory == categories.size())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Setup complete");
            builder.setMessage("Now redirecting to main screen");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void setRecyclerView()
    {
        FileReader.readData(getContext(), dataManager);
        for (Category c : dataManager.getCategories())
        {
            if (c.getName().equals("Unassigned"))
            {
                unassigned = c.getUserIDs();
            }
        }

        String category = categories.get(currentCategory).getName();

        recycleAdapter = new RecycleAdapterAssign(fragView.getContext(), category,
                unassigned);

        RecyclerView.LayoutManager lm = new LinearLayoutManager(fragView.getContext());
        recyclerView.setLayoutManager(lm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recycleAdapter);
    }
}
