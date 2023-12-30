package morgan.jones.whatistwitter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class RecycleAdapterHome extends RecyclerView.Adapter<RecycleAdapterHome.ViewHolder>
{
    private Context context;
    private List<Category> categories;
    private RecycleAdapterHome recycleAdapter;

    private boolean isLongClick;

    public RecycleAdapterHome(Context context, List<Category> categories)
    {
        this.context = context;
        this.categories = categories;
        this.recycleAdapter = this;
        isLongClick = false;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout_home,
                parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position)
    {
        final DataManager dm = new DataManager();
        FileReader.readData(context, dm);

        final ViewHolder viewHolder = holder;
        final Category category = categories.get(position);
        viewHolder.itemView.setClickable(true);

        viewHolder.categoryName.setText(category.getName());
        if (category.getName().equals("Mentions"))
        {
            viewHolder.usersNo.setText("");
        }
        else
        {
            viewHolder.usersNo.setText("Users: " + category.getUserIDs().size());
        }

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                isLongClick = true;

                builder.setTitle("Confirm Deletion");
                builder.setMessage("Do you want to delete this category?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {

                    public void onClick(DialogInterface dialog, int which)
                    {
                        for (Category c : dm.getCategories())
                        {
                            if (c.getName().equals("Unassigned"))
                            {
                                for (String s : category.getUserIDs())
                                {
                                    c.getUserIDs().add(s);
                                }
                            }

                            if (c.getName().equals(category.getName()))
                            {
                                dm.getCategories().remove(c);
                                FileReader.writeData(context, dm);
                                categories.remove(position);
                                recycleAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });

                if (category.getName().equals("Unassigned") || category.getName().equals("Mentions"))
                {
                    Toast.makeText(context, "Cannot delete this category", Toast.LENGTH_SHORT)
                            .show();
                }
                else
                {
                    AlertDialog alert = builder.create();
                    alert.show();
                }

                final Thread thread = new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            this.wait(2000);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        isLongClick = false;
                    }
                });
                thread.start();

                return false;
            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (!isLongClick)
                {
                    viewHolder.itemView.setClickable(false);
                    HomeActivity activity = (HomeActivity) view.getContext();
                    activity.loadCategoryFragment(category);
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView categoryName;
        private TextView usersNo;
        private CardView card;
        private ImageButton edit;

        private ViewHolder(View view)
        {
            super(view);

             categoryName = view.findViewById(R.id.card_category_name);
             usersNo = view.findViewById(R.id.user_numbers);
             card = view.findViewById(R.id.card_layout);
             edit = view.findViewById(R.id.edit_button);
        }
    }
}
