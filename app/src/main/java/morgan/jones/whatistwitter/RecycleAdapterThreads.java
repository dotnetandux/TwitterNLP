package morgan.jones.whatistwitter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecycleAdapterThreads extends RecyclerView.Adapter<RecycleAdapterThreads.ViewHolder>
{
    private Context context;
    private List<TweetThread> threads;

    public RecycleAdapterThreads(Context context, List<TweetThread> threads)
    {
        this.context = context;
        this.threads = threads;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout_threads,
                parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        final ViewHolder viewHolder = holder;
        final TweetThread thread = threads.get(position);
        viewHolder.itemView.setClickable(true);

        if (thread.getKeywords().size() == 0)
        {
            thread.getKeywords().add("NONE");
        }

        if (thread.getKeywords().get(0).equals("UNASSIGNED"))
        {
            viewHolder.keywords.setText("Unassigned Tweets");
            viewHolder.keywords.setTextSize(20);
            viewHolder.keywords.setForegroundGravity(Gravity.CENTER);
        }
        else
        {
            String s = "";
            for(String x : thread.getKeywords())
            {
                if (!(x.equals("")))
                {
                    // Check for duplicates
                    if (!(s.contains(x)))
                    {
                        s += "\"" + x + "\" ";
                    }
                }
            }

            viewHolder.keywords.setText(s);
        }

        String text1 = "<font color='#38A1F3'>Tweets: " + thread.getTweets().size()
                +"</font>";
        viewHolder.streamNo.setText(Html.fromHtml(text1));
        String text2 = "<font color='#38A1F3'>Emotion: " + thread.getEmotion() + "</font>";
        viewHolder.emotion.setText(Html.fromHtml(text2));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                viewHolder.itemView.setClickable(false);
                HomeActivity activity = (HomeActivity) view.getContext();
                activity.loadThreadFragment(thread);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return threads.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView keywords;
        private TextView streamNo;
        private TextView emotion;

        private ViewHolder(View view)
        {
            super(view);

             keywords = view.findViewById(R.id.card_thread_keywords);
             streamNo = view.findViewById(R.id.card_thread_stream_no);
             emotion = view.findViewById(R.id.card_thread_emotion);
             emotion = view.findViewById(R.id.card_thread_emotion);
        }
    }
}
