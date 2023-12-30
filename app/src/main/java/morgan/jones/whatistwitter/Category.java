package morgan.jones.whatistwitter;

import java.io.Serializable;
import java.util.ArrayList;

public class Category implements Serializable
{
    private String name;
    private ArrayList<String> userIDs;

    public Category(String name)
    {
        this.name = name;
        userIDs = new ArrayList<>();
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public ArrayList<String> getUserIDs()
    {
        return userIDs;
    }

    public void setUserIDs(ArrayList<String> userIDs)
    {
        this.userIDs = userIDs;
    }
}
