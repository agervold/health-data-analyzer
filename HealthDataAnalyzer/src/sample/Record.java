package sample;

import org.w3c.dom.Element;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Record {
    private String mType;
    private Date mDate;
    private double mValue;
    private ArrayList<HashMap<Date, Double>> mEntries;
    private String mUnit;

    public Record(Element element) {
        mType = element.getAttribute("type");
        mValue = Double.parseDouble(element.getAttribute("value"));
        mUnit = element.getAttribute("unit");

        Date dateWithTime = null;
        String[] startDateInput = element.getAttribute("startDate").split(" ");
        try {
            mDate = new SimpleDateFormat("yyyy-MM-dd").parse(element.getAttribute("creationDate").split(" ")[0]);
            dateWithTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(startDateInput[0] + " " + startDateInput[1]);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mEntries = new ArrayList<>();
        HashMap<Date, Double> entryMap = new HashMap<>();
        entryMap.put(dateWithTime, mValue);
        mEntries.add(entryMap);
    }

    public String getType() {
        return mType;
    }

    public Date getDate() {
        return mDate;
    }

    public double getValue() {
        return mValue;
    }

    public void setValue(double value) {
        mValue = value;
    }

    public ArrayList<HashMap<Date, Double>> getEntries() {
        return mEntries;
    }

    public void addEntry(HashMap<Date, Double> map) {
        mEntries.add(map);
    }

    public String getUnit() {
        return mUnit;
    }
}
