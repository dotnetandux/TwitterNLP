package morgan.jones.whatistwitter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecycleAdapterGraph extends RecyclerView.Adapter<RecycleAdapterGraph.ViewHolder>
{
    private Context context;
    private ArrayList<Statistic> data;

    public RecycleAdapterGraph(Context context, ArrayList<Statistic> data)
    {
        this.context = context;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout_stats,
                parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        holder.statTitle.setText(data.get(position).getName());
        holder.statNum.setText(data.get(position).getValue());

        ViewGroup.LayoutParams params;
        params = holder.cardView.getLayoutParams();

        if (data.get(position).getValue().matches(".*[a-zA-Z]+.*"))
        {
            holder.statNum.setTextSize(25);
            params.height = 400;
            holder.cardView.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView statNum;
        private TextView statTitle;
        private CardView cardView;

        private ViewHolder(View view)
        {
            super(view);

            statNum = view.findViewById(R.id.card_stat_num);
            statTitle = view.findViewById(R.id.card_stat_title);
            cardView = view.findViewById(R.id.card_layout_stats);
        }
    }
}
