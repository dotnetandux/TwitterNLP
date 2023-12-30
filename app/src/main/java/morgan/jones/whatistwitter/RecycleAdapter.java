package morgan.jones.whatistwitter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import twitter4j.TwitterException;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder>
{
    private Context context;
    private List<Status> allTweets;
    private ArrayList<String> keywords;
    private ArrayList<String> nWords;
    private ArrayList<String> pWords;

    private Uri uri;

    private DataManager dm;

    public RecycleAdapter(Context context, List<Status> tweets, ArrayList<String> keywords)
    {
        this.context = context;
        this.allTweets = tweets;
        this.keywords = keywords;
        dm = new DataManager();
        FileReader.readExternalFiles(context, dm);

        emotionWords();
    }

    private void emotionWords()
    {
        pWords = dm.getpWords();
        nWords = dm.getnWords();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout,
                parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        ViewHolder viewHolder = holder;
        final Status status = allTweets.get(position);
        String[] puncs = new String[]{";", ":", ",", ".", "?", "-", "_", " ", "--"};
        int positiveOrNegative = 0; // 0 for neutral, 1 for positive, -1 for negative

        viewHolder.usernameText.setText(status.getUser().getName());
        viewHolder.date.setText("~ " + status.getCreatedAt());

        String[] words = status.getText().split(" ");
        String tweet = "";

        final ArrayList<String> hyperlinks = new ArrayList<>();

        for (String s : words)
        {
            boolean found = false;
            for (String k : keywords)
            {
                if (!tweet.contains(" " + k) && !tweet.contains(k + " "))
                {
                    if (k.toLowerCase().equals(s.toLowerCase()))
                    {
                        tweet += "<font color='#38A1F3'><b> " + s + "</b></font>";
                        found = true;
                    }
                    else
                    {
                        boolean foundP = false;
                        for (String x : puncs)
                        {
                            //foundP = false;
                            if (k.toLowerCase().startsWith(s.toLowerCase()) && s.endsWith(x))
                            {
                                foundP = true;
                                found = true;
                            }
                        }

                        if (foundP)
                        {
                            tweet += "<font color='#38A1F3'><b> " + s.substring(0,
                                    s.length()-2) +
                                    "</b></font>" + s.substring(s.length()-1);
                        }
                    }
                }
            }

            if (!found)
            {
                if (!(s.startsWith("http")))
                {
                    if (!(s.contains("http")))
                    {
                        boolean foundEmot = false;

                        for (String pWord : dm.getpWords())
                        {
                            if (pWord.toLowerCase().equals(s.toLowerCase()))
                            {
                                tweet += " <font color='#00CC00'><b>" + s +
                                        "</b></font>";
                                foundEmot = true;
                            }
                            else
                            {
                                boolean foundP = false;
                                for (String x : puncs)
                                {
                                    if (pWord.toLowerCase().startsWith(s.toLowerCase()) && s.
                                            endsWith(x))
                                    {
                                        foundP = true;
                                        foundEmot = true;
                                    }
                                }

                                if (foundP)
                                {
                                    tweet += " <font color='#00CC00'><b>" + s.substring(0,
                                            s.length()-2) +
                                            "</b></font>" + s.substring(s.length()-1);
                                }
                            }
                        }

                        for (String nWord : dm.getnWords())
                        {
                            if (nWord.toLowerCase().equals(s.toLowerCase()))
                            {
                                tweet += " <font color='#FF0000'><b>" + s +
                                        "</b></font>";
                                foundEmot = true;
                            }
                            else
                            {
                                boolean foundP = false;
                                for (String x : puncs)
                                {
                                    if (nWord.toLowerCase().startsWith(s.toLowerCase()) && s.
                                            endsWith(x))
                                    {
                                        foundP = true;
                                        foundEmot = true;
                                    }
                                }

                                if (foundP)
                                {
                                    tweet += " <font color='#FF0000'><b>" + s.substring(0,
                                            s.length()-2) +
                                            "</b></font>" + s.substring(s.length()-1);
                                }
                            }
                        }

                        if (!foundEmot)
                        {
                            tweet += " " + s;
                        }
                    }
                }
                else
                {
                    hyperlinks.add(s);
                }
            }
        }

        boolean endsWithPunc = false;
        for (String x : puncs)
        {
            if (tweet.endsWith(x))
            {
                endsWithPunc = true;
            }
        }

        // Confirms end of tweet (for evaluation)
        if (!endsWithPunc)
        {
            tweet += ".";
        }

        viewHolder.tweetText.setText(Html.fromHtml(tweet));

        if (hyperlinks.size() == 0)
        {
            viewHolder.linkButton.setEnabled(false);
        }
        else
        {
            viewHolder.linkButton.setEnabled(true);
            viewHolder.linkButton.setBackgroundResource(R.drawable.ic_link_blue_24dp);
        }

        viewHolder.linkButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                uri = null;

                if (hyperlinks.size() == 1)
                {
                    uri = Uri.parse(hyperlinks.get(0));
                }
                else
                {
                    String links[] = new String[hyperlinks.size()];
                    for (int i = 0; i < hyperlinks.size(); i++)
                    {
                        links[i] = hyperlinks.get(i);
                    }

                    android.support.v7.app.AlertDialog.Builder alertDialog = new
                            android.support.v7.app.AlertDialog.Builder(context);
                    alertDialog.setTitle("Choose link to follow");
                    alertDialog.setItems(links, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            uri = Uri.parse(hyperlinks.get(i));
                        }
                    });
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });

        viewHolder.favourite.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    MainActivity.tweetStorage.getTwitter().createFavorite(status.getId());
                    holder.favourite.setEnabled(false);
                    holder.favourite.setBackgroundResource(R.drawable.ic_favorite_border_blue_24dp);
                }
                catch (TwitterException e)
                {
                    e.printStackTrace();
                }
            }
        });

        viewHolder.retweet.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    MainActivity.tweetStorage.getTwitter().retweetStatus(status.getId());
                    holder.retweet.setEnabled(false);
                    holder.retweet.setBackgroundResource(R.drawable.ic_autorenew_blue_24dp);
                }
                catch (TwitterException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return allTweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView usernameText;
        private TextView tweetText;
        private TextView date;

        private ImageButton linkButton;
        private ImageButton favourite;
        private ImageButton retweet;

        private ViewHolder(View view)
        {
            super(view);

            usernameText = view.findViewById(R.id.card_username);
            tweetText = view.findViewById(R.id.card_tweet);
            date = view.findViewById(R.id.card_date);
            linkButton = view.findViewById(R.id.tweet_hyperlink);
            favourite = view.findViewById(R.id.tweet_like);
            retweet = view.findViewById(R.id.tweet_retweet);
        }
    }
}
