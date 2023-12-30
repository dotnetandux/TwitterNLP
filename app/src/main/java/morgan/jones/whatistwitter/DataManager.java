package morgan.jones.whatistwitter;

import java.util.ArrayList;

public class DataManager
{
    private ArrayList<Category> categories;
    private ArrayList<String> stopwords;
    private ArrayList<String> pWords;
    private ArrayList<String> nWords;

    public DataManager()
    {
        this.categories = new ArrayList<>();
        this.stopwords = new ArrayList<>();
        this.pWords = new ArrayList<>();
        this.nWords = new ArrayList<>();
        categories.add(new Category("News"));
        categories.add(new Category("Celebs"));
        categories.add(new Category("Friends"));
        categories.add(new Category("Mentions"));
        categories.add(new Category("Unassigned"));
        test();
    }

    public ArrayList<Category> getCategories()
    {
        return categories;
    }

    private void test()
    {
        categories.get(0).getUserIDs().add("BuzzFeedNews");
        categories.get(0).getUserIDs().add("NBCNews");
        categories.get(0).getUserIDs().add("ABC");
        categories.get(0).getUserIDs().add("AppleNews");
        categories.get(0).getUserIDs().add("NASA");
        categories.get(0).getUserIDs().add("Discovery");
        categories.get(0).getUserIDs().add("nationalrailenq");
        categories.get(0).getUserIDs().add("Telegraph");
        categories.get(0).getUserIDs().add("BBCWorld");
        categories.get(0).getUserIDs().add("Independant");


        categories.get(1).getUserIDs().add("BorisJohnson");
        categories.get(1).getUserIDs().add("Madonna");
        categories.get(1).getUserIDs().add("gwenstefani");
        categories.get(1).getUserIDs().add("chrishemsworth");
        categories.get(1).getUserIDs().add("GordonRamsay");
        categories.get(1).getUserIDs().add("Schofe");
        categories.get(1).getUserIDs().add("prattprattpratt");
        categories.get(1).getUserIDs().add("SaraCarterDC");
        categories.get(1).getUserIDs().add("jimmyfallon");
        categories.get(1).getUserIDs().add("badbanana");

        categories.get(2).getUserIDs().add("10uisa");
        categories.get(2).getUserIDs().add("mizzi_michael");
        categories.get(2).getUserIDs().add("moniqueecooksey");
        categories.get(2).getUserIDs().add("Joe_rodon");
        categories.get(2).getUserIDs().add("discopandafire");
        categories.get(2).getUserIDs().add("CallumJ95");
        categories.get(2).getUserIDs().add("_TomWhite_");
        categories.get(2).getUserIDs().add("Lucy_Egan");
        categories.get(2).getUserIDs().add("lois_raay");
        categories.get(2).getUserIDs().add("TheNickCon");

        categories.get(4).getUserIDs().add("DanTaylor97");
        categories.get(4).getUserIDs().add("jessprosserx");
        categories.get(4).getUserIDs().add("cormacanderson");
        categories.get(4).getUserIDs().add("Aled_Butler");
        categories.get(4).getUserIDs().add("bryreesm");
        categories.get(4).getUserIDs().add("ucas_online");
        categories.get(4).getUserIDs().add("undergrndfacts");
        categories.get(4).getUserIDs().add("BambuBeachBar");
        categories.get(4).getUserIDs().add("WalesOnline");
        categories.get(4).getUserIDs().add("VancityReynolds");
    }

    public void setCategories(ArrayList<Category> categories)
    {
        this.categories = categories;
    }

    public ArrayList<String> getStopwords()
    {
        return stopwords;
    }

    public void setStopwords(ArrayList<String>words)
    {
        this.stopwords = words;
    }

    public ArrayList<String> getpWords() { return pWords; }

    public void setpWords(ArrayList<String> pWords) { this.pWords = pWords; }

    public ArrayList<String> getnWords() { return nWords; }

    public void setnWords(ArrayList<String> nWords) { this.nWords = nWords; }
}
