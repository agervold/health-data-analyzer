//package sample;
//
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.scene.chart.BarChart;
//import javafx.scene.chart.XYChart;
//import javafx.scene.control.SelectionMode;
//import javafx.scene.control.TreeItem;
//import javafx.scene.control.TreeView;
//import javafx.scene.text.Text;
//import org.w3c.dom.*;
//import org.xml.sax.SAXException;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//import java.beans.EventHandler;
//import java.io.File;
//import java.io.IOException;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//public class Controller2 {
//
//    @FXML private TreeView<String> TreeView;
//    @FXML private BarChart mBarChart;
//    @FXML private Text statsSteps;
//    @FXML private Text statsDays;
//    @FXML private Text statsMean;
//    @FXML private Text statsMedian;
//    @FXML private Text statsBestMonth;
//    @FXML private Text statsWorstMonth;
//    @FXML private Text statsBestDay;
//    @FXML private Text statsWorstDay;
//
//    private HashMap<String, Integer> daysData = new HashMap<>();
//    private int mSteps = 0;
//    private int mDays;
//    private int mMean;
//    private int mMedian;
//    private int mBestDayValue = 0;
//    private String mBestDayDate;
//    private XYChart.Data mBestDayPoint;
//    private int mWorstDayValue = Integer.MAX_VALUE;
//    private String mWorstDayDate;
//    private XYChart.Data mWorstDayPoint;
//
//    private int mBestMonthValue = 0;
//    private String mBestMonthName;
//
//    private int mWorstMonthValue = Integer.MAX_VALUE;
//    private String mWorstMonthName;
//
//    private HashMap<String, TreeMap<String, TreeMap<String, ArrayList<Record>>>> mMap;
//
//    //private final static String[] MONTHS = {"Jan.", "Feb.", "Mar.", "Apr.", "May", "June", "July", "Aug.", "Sept.", "Oct.", "Nov.", "Dec."};
//    private final static String[] MONTHS =
//            {
//                    "january",
//                    "february",
//                    "march",
//                    "april",
//                    "may",
//                    "june",
//                    "july",
//                    "august",
//                    "september",
//                    "october",
//                    "november",
//                    "december"
//            };
//
//    public Controller2() {
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        try {
//            File inputFile = new File("C:\\Users\\Agervold\\Documents\\export.xml");
//            DocumentBuilder builder = factory.newDocumentBuilder();
//            Document doc = builder.parse(inputFile);
//
//            doc.getDocumentElement().normalize();
//
//            NodeList nodeList = doc.getElementsByTagName("Record");
//
//            mMap = new HashMap<>();
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//
//            for (int temp = 0; temp < nodeList.getLength(); temp++) {
//                Node nNode = nodeList.item(temp);
//                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
//                    Element eElement = (Element) nNode;
//
//                    String creationDateInput = eElement.getAttribute("creationDate");
//                    String[] creationDateInputSplit = creationDateInput.split(" ");
//                    String creationDate = creationDateInputSplit[0];
//                    String[] creationDateSplit = creationDate.split("-");
//                    String creationDay = creationDateSplit[2];
//                    String creationMonth = creationDateSplit[1];
//                    String creationYear = creationDateSplit[0];
//
//                    String type = eElement.getAttribute("type");
//
//                    // Check if type exists
//                    if (mMap.get(type) == null) { // if type doesn't exist
//                        TreeMap<String, TreeMap<String, ArrayList<Record>>> typeMap = new TreeMap<>();
//                        TreeMap<String, ArrayList<Record>> yearMap = new TreeMap<>();
//                        for (int i = 1; i < 13; i++) {
//                            String month = i < 10 ? "0"+i : ""+i;
//                            ArrayList<Record> list = new ArrayList<>();
//                            if (month.equals(creationMonth)) {
//                                String dateString = eElement.getAttribute("creationDate");
//                                dateString = dateString.split(" ")[0];
//                                Date parsedDate = null;
//                                try {
//                                    parsedDate = formatter.parse(dateString);
//                                } catch (ParseException e) {
//                                    e.printStackTrace();
//                                }
//
//                                Date recordDate = parsedDate;
//                                double recordValue = Double.parseDouble(eElement.getAttribute("value"));
//                                ArrayList<Double> recordEntries = new ArrayList<>();
//                                recordEntries.add(recordValue);
//                                String recordUnit = eElement.getAttribute("unit");
//                                list.add(new Record(type, recordDate, recordValue, recordEntries, recordUnit));
//                            }
//                            yearMap.put(month, list);
//                        }
//                        typeMap.put(creationYear, yearMap);
//                        mMap.put(type, typeMap);
//                    } else { // if type does exist
//                        // Check if year exists
//                        if (mMap.get(type).get(creationYear) == null) { // if year doesn't exist
//                            TreeMap<String, ArrayList<Record>> yearMap = new TreeMap<>();
//                            for (int i = 1; i < 13; i++) {
//                                String month = i < 10 ? "0"+i : ""+i;
//                                ArrayList<Record> list = new ArrayList<>();
//                                if (month.equals(creationMonth)) {
//                                    String dateString = eElement.getAttribute("creationDate");
//                                    dateString = dateString.split(" ")[0];
//                                    Date parsedDate = null;
//                                    try {
//                                        parsedDate = formatter.parse(dateString);
//                                    } catch (ParseException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    Date recordDate = parsedDate;
//                                    double recordValue = Double.parseDouble(eElement.getAttribute("value"));
//                                    ArrayList<Double> recordEntries = new ArrayList<>();
//                                    recordEntries.add(recordValue);
//                                    String recordUnit = eElement.getAttribute("unit");
//                                    list.add(new Record(type, recordDate, recordValue, recordEntries, recordUnit));
//                                }
//                                yearMap.put(month, list);
//                            }
//                            mMap.get(type).put(creationYear, yearMap);
//                        } else { // if year does exist
//                            String dateString = eElement.getAttribute("creationDate");
//                            dateString = dateString.split(" ")[0];
//                            Date parsedDate = null;
//                            try {
//                                parsedDate = formatter.parse(dateString);
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//                            double recordValue = Double.parseDouble(eElement.getAttribute("value"));
//                            ArrayList<Double> recordEntries = new ArrayList<>();
//                            recordEntries.add(recordValue);
//                            String recordUnit = eElement.getAttribute("unit");
//
//                            // Check if month is empty
//                            TreeMap yearMap = (TreeMap) mMap.get(type).get(creationYear);
//                            ArrayList<Record> monthList = (ArrayList) yearMap.get(creationMonth);
//                            if (monthList.size() == 0) { // if month is empty
//                                monthList.add(new Record(type, parsedDate, recordValue, recordEntries, recordUnit));
//                            } else { // if month isn't empty
//                                // Check if month contains date
//                                boolean dateAlreadyExisted = false;
//                                for (Record record : monthList) {
//                                    if (record.getDate().equals(parsedDate)) { // if date already existed
//                                        double val = record.getValue();
//                                        record.setValue(val + recordValue);
//                                        record.addEntry(recordValue);
//                                        dateAlreadyExisted = true;
//                                    }
//                                }
//                                if (!dateAlreadyExisted) { // if date didn't already exist
//                                    monthList.add(new Record(type, parsedDate, recordValue, recordEntries, recordUnit));
//                                }
//                            }
//                        }
//                    }
//
//                    //OLD METHOD
////                    if (eElement.getAttribute("type").equals("HKQuantityTypeIdentifierStepCount")) {
////                        int eValue = Integer.parseInt(eElement.getAttribute("value"));
////                        String eDate = eElement.getAttribute("creationDate").split(" ")[0];
////                        String eMonth = eDate.split("-")[1];
////
////                        int day = daysData.containsKey(eDate) ? daysData.get(eDate) : 0;
////                        daysData.put(eDate, day+eValue);
////
////                        int month = monthsData.containsKey(eMonth) ? monthsData.get(eMonth) : 0;
////                        monthsData.put(eMonth, month+eValue);
////
////                        mSteps += eValue;
////                    }
//                }
//            }
//        } catch (ParserConfigurationException | IOException | SAXException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @FXML
//    private void initialize() {
//        mBarChart.setLegendVisible(false);
//
//        TreeItem<String> dummyRoot = new TreeItem<>();
//        TreeMap<String, TreeMap<String, ArrayList<Record>>> stepMap = mMap.get("HKQuantityTypeIdentifierStepCount");
//        for (String type : mMap.keySet()) {
//            // TODO: Clean up
//            String[] typeName = type.split("HKQuantityTypeIdentifier")[1].split("(?<=.)(?=\\p{Lu})");
//            String typeName2 = "";
//            for (String aTypeName : typeName) {
//                typeName2 += aTypeName + " ";
//            }
//            TreeItem<String> typeItem = new TreeItem<>(typeName2);
//
//            for (String year : stepMap.keySet()) {
//                TreeItem<String> yearItem = new TreeItem<>(year);
//                for (String month : MONTHS) {
//                    int index = 0;
//                    for (int i = 0; i < MONTHS.length; i++) {
//                        if (MONTHS[i].equals(month)) {
//                            index = i+1;
//                            break;
//                        }
//                    }
//                    String monthIndex = index < 10 ? "0"+index : ""+index;
//                    if (stepMap.get(year).get(monthIndex).size() > 0) {
//                        month = month.substring(0,1).toUpperCase() + month.substring(1);
//                        TreeItem<String> monthItem = new TreeItem<> (month);
//                        TreeMap<String, ArrayList<Record>> yearMap = stepMap.get(year);
//                        for (Record record : yearMap.get(monthIndex)) {
//                            monthItem.getChildren().add(new TreeItem<>(record.getDate().toString()));
//                        }
//                        yearItem.getChildren().add(monthItem);
//                    }
//                }
//                typeItem.getChildren().add(yearItem);
//            }
//            dummyRoot.getChildren().add(typeItem);
//        }
//
//        TreeView.setRoot(dummyRoot);
//        TreeView.setShowRoot(false);
//        TreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//
//        TreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//            mBarChart.getData().clear();
//            if (TreeView.getSelectionModel().getSelectedItems().size() == 1) {
//                String value = newValue.getValue().toLowerCase();
//                XYChart.Series series = new XYChart.Series();
//
//                if (newValue.getParent().getValue() == null) { // if type is selected
//                    System.out.println("Type: "+value);
//                } else if (newValue.getParent().getParent().getValue() == null) { // if year is selected
//                    System.out.println("Year: "+value);
//                    String type = "HKQuantityTypeIdentifier" + newValue.getParent().getValue().replace(" ", "");
//                    series = graphYear(mMap.get(type).get(value));
//                } else if (newValue.getParent().getParent().getParent().getValue() == null) { // if month is selected
//                    System.out.println("Month: "+value);
//                    String type = "HKQuantityTypeIdentifier" + newValue.getParent().getParent().getValue().replace(" ", "");
//                    String year = newValue.getParent().getValue();
//                    int index = 0;
//                    for (int i = 0; i < MONTHS.length; i++) {
//                        if (MONTHS[i].equals(value)) {
//                            index = i+1;
//                            break;
//                        }
//                    }
//                    String month = index < 10 ? "0"+index : ""+index;
//                    series = graphMonth(mMap.get(type).get(year).get(month));
//                } else { // if day is selected
//                    System.out.println("Day: "+value);
//                    String type = "HKQuantityTypeIdentifier" + newValue.getParent().getParent().getParent().getValue().replace(" ", "");
//                    String year = newValue.getParent().getParent().getValue();
//                    String month = newValue.getParent().getValue().toLowerCase();
//                    int index = 0;
//                    for (int i = 0; i < MONTHS.length; i++) {
//                        if (MONTHS[i].equals(month)) {
//                            index = i+1;
//                            break;
//                        }
//                    }
//                    month = index < 10 ? "0"+index : ""+index;
//                    int day = 0;
//                    ObservableList<TreeItem<String>> siblings = newValue.getParent().getChildren();
//                    for (int i = 0; i < siblings.size(); i++) {
//                        if (siblings.get(i) == newValue) {
//                            day = i;
//                            break;
//                        }
//                    }
//                    series = graphDay(mMap.get(type).get(year).get(month).get(day).getEntries());
//                }
//                mBarChart.getData().add(series);
//                setStats();
//
//                /*
//                if (newValue.getParent().getValue() == null) { // if year is selected
//                    series = graphYear(stepMap.get(value));
//                } else { // if month is selected
//                    int index = 0;
//                    for (int i = 0; i < MONTHS.length; i++) {
//                        if (MONTHS[i].equals(value)) {
//                            index = i+1;
//                            break;
//                        }
//                    }
//                    String month = index < 10 ? "0"+index : ""+index;
//                    String year = newValue.getParent().getValue();
//                    series = graphMonth(stepMap.get(year).get(month));
//                    //mBarChart.getData().add(series1); //Adds new data on top
//                }
//                mBarChart.getData().add(series);
//                setStats();
//                */
//            } else {
//                String year = TreeView.getSelectionModel().getSelectedItems().get(0).getParent().getValue();
//                int steps = 0;
//                int days = 0;
//                for (int val  = 0; val < TreeView.getSelectionModel().getSelectedItems().size(); val++) {
//                    String value = TreeView.getSelectionModel().getSelectedItems().get(val).getValue().toLowerCase();
//                    XYChart.Series series = new XYChart.Series();
//                    int index = 0;
//                    for (int i = 0; i < MONTHS.length; i++) {
//                        if (MONTHS[i].equals(value)) {
//                            index = i+1;
//                            break;
//                        }
//                    }
//                    String month = index < 10 ? "0"+index : ""+index;
//                    ArrayList<Record> data = stepMap.get(year).get(month);
//                    series = graphMonth(data);
//                    mBarChart.getData().add(series);
//                    steps += mSteps;
//                    days += mDays;
//                }
//                mSteps = steps;
//                mDays = days;
//                mMean = mSteps / mDays;
//                setStats();
//            }
//
//        });
//    }
//
//    private XYChart.Series graphDay(ArrayList<Double> data) {
//        XYChart.Series<String, Double> series = new XYChart.Series<>();
//
//        for (Double val : data) {
//            series.getData().add(new XYChart.Data<>(String.valueOf(val), val)); //TODO: Add time
//        }
//
//        return series;
//    }
//
//    private XYChart.Series graphMonth(ArrayList<Record> data) {
//        mSteps = 0;
//        mDays = data.size();
//
//        XYChart.Series<String, Double> series = new XYChart.Series<>();
//
//        ArrayList<Double> temp = new ArrayList<>();
//
//        for (Record record : data) {
//            double value = record.getValue();
//            mSteps += value;
//            series.getData().add(new XYChart.Data<>(record.getDate().toString(), value));
//            temp.add(value);
//        }
//
//        mMedian = getMedian(temp);
//        mMean = mSteps / mDays;
//
//        return series;
//    }
//
//    private XYChart.Series graphYear(TreeMap<String, ArrayList<Record>> data) {
//        mSteps = 0;
//        mDays = 0;
//
//        XYChart.Series<String, Double> series = new XYChart.Series<>();
//
//        ArrayList<Double> temp = new ArrayList<>();
//
//        for (String key : data.keySet()) {
//            ArrayList<Record> month = data.get(key);
//            for (Record record : month) {
//                double value = record.getValue();
//                mSteps += value;
//                mDays++;
//                series.getData().add(new XYChart.Data<>(record.getDate().toString(), value));
//                temp.add(value);
//            }
//        }
//
//        mMedian = getMedian(temp);
//        mMean = mSteps / mDays;
//
//        return series;
//    }
//
//    private XYChart.Series graphAll(TreeMap<String, TreeMap<String, ArrayList<Record>>> data) {
//        XYChart.Series<String, Double> series = new XYChart.Series<>();
//
//        SortedSet<String> keys = new TreeSet<>(daysData.keySet());
//
//        for (String key : keys) {
//            XYChart.Data point = new XYChart.Data<>(key, daysData.get(key));
//
//            int value = daysData.get(key);
//            if (value > mBestDayValue) {
//                mBestDayValue = value;
//                mBestDayDate = key;
//                mBestDayPoint = point;
//            } else if (value < mWorstDayValue) {
//                mWorstDayValue = value;
//                mWorstDayDate = key;
//                mWorstDayPoint = point;
//            }
//
//            series.getData().add(point);
//        }
//
//        return series;
//    }
//
//    private int getMedian(ArrayList<Double> data) {
//        data.sort(Double::compare);
//        int len = data.size();
//        if (len % 2 == 0) {
//            return (int) (data.get(len/2) + data.get(len/2-1)) / 2;
//        } else {
//            return (data.get(len/2)).intValue();
//        }
//    }
//
//    private void setStats() {
//        statsSteps.setText("Steps: " + mSteps);
//        statsDays.setText("Days: " + mDays);
//        statsMean.setText("Mean: " + mMean);
//        statsMedian.setText("Median: " + mMedian);
//
//        /*
//        statsBestMonth.setText("Best Month: ?");
//        statsWorstMonth.setText("Worst Month: ?");
//        statsBestDay.setText(String.format("Best Day: %d - %s", mBestDayValue, mBestDayDate));
//        statsBestDay.setOnMouseClicked(event -> mBestDayPoint.getNode().setStyle("-fx-background-color: blue"));
//        statsWorstDay.setText(String.format("Worst Day: %d - %s", mWorstDayValue, mWorstDayDate));
//        statsWorstDay.setOnMouseClicked(event -> mWorstDayPoint.getNode().setStyle("-fx-background-color: blue"));
//        */
//    }
//}
//
///*
//            if (TreeView.getSelectionModel().getSelectedItems().size() == 1) {
//                String value = newValue.getValue().toLowerCase();
//                System.out.println(value);
//                XYChart.Series series = new XYChart.Series();
//
//                if (newValue.getParent().getValue() == null) { // if year is selected
//                    series = graphYear(stepMap.get(value));
//                } else { // if month is selected
//                    int index = 0;
//                    for (int i = 0; i < MONTHS.length; i++) {
//                        if (MONTHS[i].equals(value)) {
//                            index = i+1;
//                            break;
//                        }
//                    }
//                    String month = index < 10 ? "0"+index : ""+index;
//                    String year = newValue.getParent().getValue();
//                    series = graphMonth(stepMap.get(year).get(month));
//                    //mBarChart.getData().add(series1); //Adds new data on top
//                }
//                mBarChart.getData().add(series);
//                setStats();
//            } else {
//                String year = TreeView.getSelectionModel().getSelectedItems().get(0).getParent().getValue();
//                int steps = 0;
//                int days = 0;
//                for (int val  = 0; val < TreeView.getSelectionModel().getSelectedItems().size(); val++) {
//                    String value = TreeView.getSelectionModel().getSelectedItems().get(val).getValue().toLowerCase();
//                    XYChart.Series series = new XYChart.Series();
//                    int index = 0;
//                    for (int i = 0; i < MONTHS.length; i++) {
//                        if (MONTHS[i].equals(value)) {
//                            index = i+1;
//                            break;
//                        }
//                    }
//                    String month = index < 10 ? "0"+index : ""+index;
//                    ArrayList<Record> data = stepMap.get(year).get(month);
//                    series = graphMonth(data);
//                    mBarChart.getData().add(series);
//                    steps += mSteps;
//                    days += mDays;
//                }
//                mSteps = steps;
//                mDays = days;
//                mMean = mSteps / mDays;
//                setStats();
//            }
// */



















/*
private XYChart.Series<String, Double> graphDay(ArrayList<HashMap<Date, Double>> data) {
    XYChart.Series<String, Double> series = new XYChart.Series<>();

    for (HashMap<Date, Double> map : data) {
        Date key = (Date) map.keySet().toArray()[0];
        double val = map.get(key);
        series.getData().add(new XYChart.Data<>(key.toString(), val));
        mSteps += val;
    }

    setStats(0);
    return series;
}

private XYChart.Series<String, Double> graphMonth(ArrayList<Record> data) {
    mSteps = 0;
    mDays = data.size();
    mBestDayValue = 0;
    mWorstDayValue = Integer.MAX_VALUE;

    XYChart.Series<String, Double> series = new XYChart.Series<>();

    ArrayList<Double> temp = new ArrayList<>();

    for (Record record : data) {
        double value = record.getValue();
        mSteps += value;
        XYChart.Data<String, Double> point = new XYChart.Data<>(record.getDate().toString(), value);
        series.getData().add(point);
        temp.add(value);

        updateStats(value, point, record);
    }

    mMedian = getMedian(temp);
    mMean = mSteps / mDays;

    setStats(1);
    return series;
}

private XYChart.Series<String, Double> graphYear(TreeMap<String, ArrayList<Record>> data) {
    mSteps = 0;
    mDays = 0;
    mBestDayValue = 0;
    mWorstDayValue = Integer.MAX_VALUE;
    mBestMonthValue = 0;
    mWorstMonthValue = Integer.MAX_VALUE;

    XYChart.Series<String, Double> series = new XYChart.Series<>();

    ArrayList<Double> temp = new ArrayList<>();

    for (String key : data.keySet()) {
        ArrayList<Record> month = data.get(key);
        int monthValue = 0;
        for (Record record : month) {
            double value = record.getValue();
            mSteps += value;
            mDays++;
            monthValue += value;
            //series.getData().add(new XYChart.Data<>(record.getDate().toString(), value));
            XYChart.Data<String, Double> point = new XYChart.Data<>(record.getDate().toString(), value);
            series.getData().add(point);
            temp.add(value);

            updateStats(value, point, record);
        }
        if (monthValue > mBestMonthValue) {
            mBestMonthValue = monthValue;
            mBestMonthName = MONTHS[Integer.valueOf(key)-1];
        } else if (monthValue < mWorstMonthValue && monthValue > 0) {
            mWorstMonthValue = monthValue;
            mWorstMonthName = MONTHS[Integer.valueOf(key)-1];
        }
    }

    mMedian = getMedian(temp);
    mMean = mSteps / mDays;

    setStats(2);
    return series;
}

private XYChart.Series<String, Double> graphAll(TreeMap<String, TreeMap<String, ArrayList<Record>>> data) {
    mSteps = 0;
    mDays = 0;
    mBestDayValue = 0;
    mWorstDayValue = Integer.MAX_VALUE;
    mBestMonthValue = 0;
    mWorstMonthValue = Integer.MAX_VALUE;

    XYChart.Series<String, Double> series = new XYChart.Series<>();

    ArrayList<Double> temp = new ArrayList<>();

    for (String year : data.keySet()) {
        TreeMap<String, ArrayList<Record>> year2 = data.get(year);
        for (String key : year2.keySet()) {
            ArrayList<Record> month = year2.get(key);
            int monthValue = 0;
            for (Record record : month) {
                double value = record.getValue();
                //mSteps += value;
                //mDays++;
                XYChart.Data<String, Double> point = new XYChart.Data<>(record.getDate().toString(), value);
                series.getData().add(point);
                temp.add(value);
                mSteps += value;
                mDays++;
                monthValue += value;
                updateStats(value, point, record);
            }
            if (monthValue > mBestMonthValue) {
                mBestMonthValue = monthValue;
                mBestMonthName = MONTHS[Integer.valueOf(key)-1];
            } else if (monthValue < mWorstMonthValue && monthValue > 0) {
                mWorstMonthValue = monthValue;
                mWorstMonthName = MONTHS[Integer.valueOf(key)-1];
            }
        }
    }
    mMedian = getMedian(temp);
    mMean = mSteps / mDays;

    setStats(2);
    return series;
}
*/