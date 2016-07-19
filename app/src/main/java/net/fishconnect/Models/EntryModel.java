package net.fishconnect.Models;

/**
 * Created by MY on 6/4/2016.
 */
public class EntryModel {
    private String Slnumber;

    private String entry_time;

    public EntryModel()
    {

    }

    public void setSlnumber(String slnumber) {
        Slnumber = slnumber;
    }

    public void setEntry_time(String entry_time) {
        this.entry_time = entry_time;
    }



    public String getSlnumber() {
        return Slnumber;
    }

    public String getEntry_time() {
        return entry_time;
    }
}
