package morgan.jones.whatistwitter;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class    FileReader
{
    private static final String STOP_WORDS = "stopwords_twitter.txt";
    private static final String DATA_FILE = "data.txt";
    private static final String POSITIVE_WORDS = "p_words_twitter.txt";
    private static final String NEGATIVE_WORDS = "n_words_twitter.txt";

    public static void writeData(Context context, DataManager data)
    {
        FileOutputStream output = null;

        try
        {
            output = context.openFileOutput(DATA_FILE, context.MODE_PRIVATE);
            output.write(outputFormatter(data).getBytes());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if (output != null)
        {
            try
            {
                output.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static String getFilePath(Context context)
    {
        InputStream input = null;

        try
        {
            input = context.getAssets().open(STOP_WORDS);
            //input = context.openFileInput(STOP_WORDS);
            InputStreamReader reader = new InputStreamReader(input);
            return input.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "Fucked it bruv";
        }
    }

    public static void readExternalFiles(Context context, DataManager data)
    {
        InputStream input = null;

        try
        {
            input = context.getAssets().open(STOP_WORDS);
            InputStreamReader reader = new InputStreamReader(input);
            BufferedReader br = new BufferedReader(reader);
            String text;

            while ((text = br.readLine()) != null)
            {
                data.getStopwords().add(text);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if (input != null)
        {
            try
            {
                input.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        input = null;

        try
        {
            input = context.getAssets().open(POSITIVE_WORDS);
            InputStreamReader reader = new InputStreamReader(input);
            BufferedReader br = new BufferedReader(reader);
            String text;

            while ((text = br.readLine()) != null)
            {
                data.getpWords().add(text);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if (input != null)
        {
            try
            {
                input.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        input = null;

        try
        {
            input = context.getAssets().open(NEGATIVE_WORDS);
            InputStreamReader reader = new InputStreamReader(input);
            BufferedReader br = new BufferedReader(reader);
            String text;

            while ((text = br.readLine()) != null)
            {
                data.getnWords().add(text);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if (input != null)
        {
            try
            {
                input.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void readData(Context context, DataManager data)
    {
        FileInputStream input = null;
        data.setCategories(new ArrayList<Category>());

        try
        {
            input = context.openFileInput(DATA_FILE);
            InputStreamReader reader = new InputStreamReader(input);
            BufferedReader br = new BufferedReader(reader);
            String text;

            while ((text = br.readLine()) != null)
            {
                formatData(text, data);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if (input != null)
        {
            try
            {
                input.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static void formatData(String line, DataManager data)
    {
        String[] x = line.split(";");
        if (x.length > 1)
        {
            String[] ids = x[1].split("@");

            String name = x[0];
            Category c = new Category(name);

            for (String s : ids)
            {
                c.getUserIDs().add(s);
            }

            data.getCategories().add(c);
        }
        else
        {
            data.getCategories().add(new Category(x[0]));
        }
    }

    private static String outputFormatter(DataManager data)
    {
        String output = "";

        for (Category c : data.getCategories())
        {
            output += c.getName() + ";";
            for (String s : c.getUserIDs())
            {
                output += s + "@";
            }

            output += "\n";
        }

        return output;
    }
}