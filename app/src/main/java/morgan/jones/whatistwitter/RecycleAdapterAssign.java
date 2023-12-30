package morgan.jones.whatistwitter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecycleAdapterAssign extends RecyclerView.Adapter<RecycleAdapterAssign.ViewHolder>
{
    private Context context;
    private String category;
    private DataManager dataManager;
    private ArrayList<String> unassigned;

    public RecycleAdapterAssign(Context context, String category, ArrayList<String> unassigned)
    {
        this.context = context;
        this.category = category;
        dataManager = new DataManager();
        this.unassigned = unassigned;
        FileReader.readData(context, dataManager);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_assign_layout,
                parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        final String userID = unassigned.get(position);
        final String catName = category;

        holder.checkBox.setChecked(false);

        holder.usernameText.setText(userID);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    for (Category c : dataManager.getCategories())
                    {
                        if (c.getName().equals("Unassigned"))
                        {
                            for (int i = 0; i < c.getUserIDs().size(); i++)
                            {
                                if (c.getUserIDs().get(i).toLowerCase().equals(userID
                                        .toLowerCase()))
                                {
                                    c.getUserIDs().remove(i);
                                }
                            }
                        }
                        else if (c.getName().toLowerCase().equals(catName))
                        {
                            c.getUserIDs().add(userID);
                        }
                    }
                }
                else
                {
                    for (Category c : dataManager.getCategories())
                    {
                        if (c.getName().equals("Unassigned"))
                        {
                            c.getUserIDs().add(userID);
                        }
                        else if (c.getName().toLowerCase().equals(catName))
                        {
                            for (int i = 0; i < c.getUserIDs().size(); i++)
                            {
                                if (c.getUserIDs().get(i).toLowerCase().equals(userID
                                        .toLowerCase()))
                                {
                                    c.getUserIDs().remove(i);
                                }
                            }
                        }
                    }
                }

                FileReader.writeData(context, dataManager);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return unassigned.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView usernameText;
        private CheckBox checkBox;

        private ViewHolder(View view)
        {
            super(view);

            usernameText = view.findViewById(R.id.card_assign_username);
            checkBox = view.findViewById(R.id.assign_checkbox);
        }
    }
}
