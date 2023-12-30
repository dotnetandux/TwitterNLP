package morgan.jones.whatistwitter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class RecycleAdapterUsers extends RecyclerView.Adapter<RecycleAdapterUsers.ViewHolder>
{
    private Context context;
    private Category category;
    private DataManager dataManager;

    public RecycleAdapterUsers(Context context, Category category)
    {
        this.context = context;
        this.category = category;
        dataManager = new DataManager();
        FileReader.readData(context, dataManager);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_users_layout,
                parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        final RecycleAdapterUsers adapter = this;

        final ViewHolder viewHolder = holder;
        final String user = category.getUserIDs().get(position);
        viewHolder.usernameText.setText(user);

        if (category.getName().equals("Unassigned"))
        {
            viewHolder.userAction.setBackground(ContextCompat.getDrawable(context,
                    R.drawable.ic_person));
            viewHolder.userAction.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    // Remove from unassigned
                    category.getUserIDs().remove(user);

                    // Remove from unassigned in data manager
                    for (Category c : dataManager.getCategories())
                    {
                        if (c.getName().equals("Unassigned"))
                        {
                            c.getUserIDs().remove(user);
                        }
                    }

                    // Load categories into builder
                    int dialogPos = 0;
                    final String[] categories = new String[dataManager.getCategories().size()-2];
                    for (int i = 0; i < dataManager.getCategories().size(); i++)
                    {
                        String name = dataManager.getCategories().get(i).getName();
                        if ((!name.equals("Unassigned")) && (!name.equals("Mentions")))
                        {
                            categories[dialogPos] = name;
                            dialogPos++;
                        }
                    }

                    // Choose new category
                    android.support.v7.app.AlertDialog.Builder alertDialog = new
                            android.support.v7.app.AlertDialog.Builder(context);
                    alertDialog.setTitle("Choose assignment");
                    alertDialog.setItems(categories, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            for (Category c : dataManager.getCategories())
                            {
                                if (c.getName().equals(categories[i]))
                                {
                                    c.getUserIDs().add(user);
                                }
                            }

                            FileReader.writeData(context, dataManager);
                            Toast.makeText(context, user + " assigned to " + categories[i],
                                    Toast.LENGTH_SHORT).show();

                            // Update view
                            adapter.notifyItemRemoved(position);
                        }
                    });

                    alertDialog.show();
                }
            });
        }
        else
        {
            viewHolder.userAction.setBackground(ContextCompat.getDrawable(context,
                    R.drawable.ic_delete));
            viewHolder.userAction.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    // Remove from current category
                    category.getUserIDs().remove(user);

                    // Add to unassigned
                    // Remove from dataManager
                    for (Category c : dataManager.getCategories())
                    {
                        if (c.getName().equals("Unassigned"))
                        {
                            c.getUserIDs().add(user);
                        }
                        else if (c.getName().equals(category.getName()))
                        {
                            c.getUserIDs().remove(user);
                        }
                    }

                    FileReader.writeData(context, dataManager);
                    Toast.makeText(context, user + " moved to unassigned", Toast.LENGTH_SHORT)
                            .show();

                    // Update view
                    adapter.notifyItemRemoved(position);
                }
            });
        }
    }

    @Override
    public int getItemCount()
    {
        return category.getUserIDs().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView usernameText;
        private ImageButton userAction;
        private CardView card;

        private ViewHolder(View view)
        {
            super(view);

            usernameText = view.findViewById(R.id.card_users_username);
            userAction = view.findViewById(R.id.user_action);
            card = view.findViewById(R.id.card_layout_users);
        }
    }
}
