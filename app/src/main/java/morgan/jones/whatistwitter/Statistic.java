package morgan.jones.whatistwitter;

/**
 * Created on 15/01/2019.
 *
 * @author Morgan Eifion Jones
 * @version 1.0
 */
public class Statistic
{
    private String name;
    private String value;

    public Statistic(String name, String value)
    {
        this.name = name;
        this.value = value;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}
