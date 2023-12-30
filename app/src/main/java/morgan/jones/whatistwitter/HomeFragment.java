package morgan.jones.whatistwitter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class HomeFragment extends Fragment
{
    private static View staticView;
    private View fragView;

    private RecyclerView recyclerView;
    private DataManager dm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        if (staticView == null)
        {
            fragView = inflater.inflate(R.layout.home_fragment, container, false);
            staticView = fragView;
        }
        else
        {
            fragView = staticView;
        }

        recyclerView = fragView.findViewById(R.id.home_frag_recycle);
        dm = new DataManager();
        FileReader.readData(getContext(), dm);

        setRecyclerView();

        return fragView;
    }

    private void setRecyclerView()
    {
        final ArrayList<Category> categories = dm.getCategories();

        final RecycleAdapterHome recycleAdapterHome = new RecycleAdapterHome(fragView.getContext(),
                categories);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(fragView.getContext());
        recyclerView.setLayoutManager(lm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recycleAdapterHome);

        final ItemTouchHelper itemHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT)
        {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target)
            {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
            {
                if (!(dm.getCategories().get(viewHolder.getAdapterPosition()).getName().equals
                        ("Mentions")))
                {
                    FileReader.readData(fragView.getContext(), dm);
                    FileReader.readExternalFiles(fragView.getContext(), dm);
                    HomeActivity activity = (HomeActivity) fragView.getContext();
                    activity.loadCategoryMenu(dm.getCategories().get(viewHolder.getAdapterPosition()));
                }
                else
                {
                    Toast.makeText(staticView.getContext(), "Cannot edit Mentions category",
                            Toast.LENGTH_SHORT).show();
                    recyclerView.setAdapter(recycleAdapterHome);
                }
            }
        });

        itemHelper.attachToRecyclerView(recyclerView);
    }
}
