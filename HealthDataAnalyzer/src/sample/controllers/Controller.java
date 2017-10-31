package sample.controllers;

import javafx.animation.ScaleTransition;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import sample.CustomTreeItem;
import sample.Record;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Controller {
    @FXML private TreeView<String> TreeView;
    @FXML private BarChart<String, Double> mBarChart;
    @FXML private LineChart<Integer, Integer> mLineChart;
    @FXML private ImageView heartIcon;
    @FXML private HBox statsContainer;
    @FXML private Text statsSteps;
    @FXML private Text statsDays;
    @FXML private Text statsMean;
    @FXML private Text statsMedian;
    @FXML private Text statsBestMonth;
    @FXML private Text statsWorstMonth;
    @FXML private Text statsBestDay;
    @FXML private Text statsWorstDay;

    private ScaleTransition mPulseAnimation;

    private XYChart.Series<String, Double> mBarChartSeries;
    private XYChart.Series<Integer, Integer> mLineChartSeries;

    private int mStatSteps = 0;
    private int mStatDays;
    private int mStatMean;
    private ArrayList<Double> mMedianList;
    private double mMedian;
    private String mUnit;

    private int mBestDayValue;
    private Date mBestDayDate;
    private XYChart.Data mBestDayPoint;
    private int mWorstDayValue;
    private Date mWorstDayDate;
    private XYChart.Data mWorstDayPoint;

    private int mBestMonthValue;
    private String mBestMonthName;
    private int mWorstMonthValue;
    private String mWorstMonthName;

    private double mStatWeightStart;
    private double mStatWeightCurrent;
    private double mStatWeightLowest;
    private double mStatWeightHighest;
    private int mStatWeightCounter;

    private HashMap<String, TreeMap<String, TreeMap<String, ArrayList<Record>>>> mMap;

    //private final static String[] MONTHS = {"Jan.", "Feb.", "Mar.", "Apr.", "May", "June", "July", "Aug.", "Sept.", "Oct.", "Nov.", "Dec."};
    private final static String[] MONTHS =
            {
                    "january",
                    "february",
                    "march",
                    "april",
                    "may",
                    "june",
                    "july",
                    "august",
                    "september",
                    "october",
                    "november",
                    "december"
            };

    public Controller(File file) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            if (file == null) file = new File("C:\\Users\\Agervold\\Documents\\export.xml");
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("Record");

            mMap = new HashMap<>();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatterTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            for (int temp = 0; temp < nodeList.getLength(); temp++) {
                Node nNode = nodeList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    String startDateInput = eElement.getAttribute("startDate");
                    String[] startDateInputSplit = startDateInput.split(" ");
                    String startDate = startDateInputSplit[0];
                    String startTime = startDateInputSplit[1];

                    String creationDateInput = eElement.getAttribute("creationDate");
                    String[] creationDateInputSplit = creationDateInput.split(" ");
                    String creationDate = creationDateInputSplit[0];
                    String[] creationDateSplit = creationDate.split("-");
                    String creationMonth = creationDateSplit[1];
                    String creationYear = creationDateSplit[0];

                    String recordType = eElement.getAttribute("type");
                    double recordValue = Double.parseDouble(eElement.getAttribute("value"));

                    Date dateWithoutTime = null;
                    Date dateWithTime = null;
                    try {
                        dateWithoutTime = formatter.parse(creationDate);
                        dateWithTime = formatterTime.parse(startDate + " " + startTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    // Check if type exists
                    if (mMap.get(recordType) == null) { // if type doesn't exist
                        TreeMap<String, TreeMap<String, ArrayList<Record>>> typeMap = new TreeMap<>();
                        TreeMap<String, ArrayList<Record>> yearMap = new TreeMap<>();
                        for (int i = 1; i < 13; i++) {
                            String month = i < 10 ? "0"+i : ""+i;
                            ArrayList<Record> list = new ArrayList<>();
                            if (month.equals(creationMonth)) {
                                list.add(new Record(eElement));
                            }
                            yearMap.put(month, list);
                        }
                        typeMap.put(creationYear, yearMap);
                        mMap.put(recordType, typeMap);
                    } else { // if type does exist
                        // Check if year exists
                        if (mMap.get(recordType).get(creationYear) == null) { // if year doesn't exist
                            TreeMap<String, ArrayList<Record>> yearMap = new TreeMap<>();
                            for (int i = 1; i < 13; i++) {
                                String month = i < 10 ? "0"+i : ""+i;
                                ArrayList<Record> list = new ArrayList<>();
                                if (month.equals(creationMonth)) {
                                    list.add(new Record(eElement));
                                }
                                yearMap.put(month, list);
                            }
                            mMap.get(recordType).put(creationYear, yearMap);
                        } else { // if year does exist
                            HashMap<Date, Double> entryMap = new HashMap<>();
                            entryMap.put(dateWithTime, recordValue);

                            // Check if month is empty
                            TreeMap<String, ArrayList<Record>> yearMap = mMap.get(recordType).get(creationYear);
                            ArrayList<Record> monthList = yearMap.get(creationMonth);
                            if (monthList.size() == 0) { // if month is empty
                                monthList.add(new Record(eElement));
                            } else { // if month isn't empty
                                // Check if month contains date
                                boolean dateAlreadyExisted = false;
                                for (Record record : monthList) {
                                    if (record.getDate().equals(dateWithoutTime)) { // if date already existed
                                        double val = record.getValue();
                                        record.setValue(val + recordValue);
                                        record.addEntry(entryMap);
                                        dateAlreadyExisted = true;
                                    }
                                }
                                if (!dateAlreadyExisted) { // if date didn't already exist
                                    monthList.add(new Record(eElement));
                                }
                            }
                        }
                    }
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    @FXML private void initialize() {
        mBarChart.setLegendVisible(false);
        mLineChart.setCreateSymbols(false);
        mLineChart.setLegendVisible(false);


        mPulseAnimation = new ScaleTransition(Duration.millis(2000), heartIcon);
        mPulseAnimation.setByX(.3f);
        mPulseAnimation.setByY(.3f);
        mPulseAnimation.setCycleCount(Integer.MAX_VALUE);
        mPulseAnimation.setAutoReverse(true);

        mPulseAnimation.play();

        createTreeView();
        bindTreeView();
    }

    private void createTreeView() {
        PseudoClass subElementPseudoClass = PseudoClass.getPseudoClass("sub-tree-item");

        TreeView.setCellFactory(tv -> {
            TreeCell<String> cell = new TreeCell<String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    //setDisclosureNode(null);

                    if (empty) {
                        setText("");
                        setGraphic(null);
                    } else {
                        setText(item); // appropriate text for item

                        ImageView imageView = new ImageView();
                        imageView.setFitHeight(25);
                        imageView.setFitWidth(37.5);
                        //TODO: Only set icons for First Level (type)

                        switch (item) {
                            case "Body Mass ":
                                imageView.setImage(new Image("/images/scale_icon_circle.png"));
                                //setDisclosureNode(imageView);
                                setGraphic(imageView);
                                break;
                            case "Distance Walking Running ":
                                imageView.setImage(new Image("/images/distance_icon_circle.png"));
                                //setDisclosureNode(imageView);
                                setGraphic(imageView);
                                break;
                            case "Height ":
                                imageView.setImage(new Image("/images/height_icon_circle.png"));
                                //setDisclosureNode(imageView);
                                setGraphic(imageView);
                                break;
                            case "Step Count ":
                                imageView.setImage(new Image("/images/footprint_icon_circle.png"));
                                //setDisclosureNode(imageView);
                                setGraphic(imageView);
                                break;
                            case "Flights Climbed ":
                                imageView.setImage(new Image("/images/stair_icon_circle.png"));
                                //setDisclosureNode(imageView);
                                setGraphic(imageView);
                                break;
                            default:
                                setGraphic(null);
                                break;
                        }
                        //Image expandedImage = new Image("/images/footprint_white_icon.png");
                    }
                }
            };

            cell.treeItemProperty().addListener((obs, oldTreeItem, newTreeItem) -> {
                cell.pseudoClassStateChanged(subElementPseudoClass,
                        newTreeItem != null && newTreeItem.getParent() != cell.getTreeView().getRoot());
            });

            return cell ;
        });

        ChangeListener<Boolean> expandedListener = (obs, wasExpanded, isNowExpanded) -> {
            if (isNowExpanded) {
                ReadOnlyProperty<?> expandedProperty = (ReadOnlyProperty<?>) obs ;
                Object itemThatWasJustExpanded = expandedProperty.getBean();
                TreeView.getRoot().getChildren().stream().filter(item -> item != itemThatWasJustExpanded).forEach(item -> {
                    item.setExpanded(false);
                });
            }
        };

        TreeItem<String> dummyRoot = new TreeItem<>();
        TreeMap<String, TreeMap<String, ArrayList<Record>>> typeMap;// = mMap.get("HKQuantityTypeIdentifierStepCount"); //todo wtf is this
        for (String typeIterator : mMap.keySet()) {
            typeMap = mMap.get(typeIterator);
            // TODO: Clean up
            String[] typeArray = typeIterator.split("HKQuantityTypeIdentifier")[1].split("(?<=.)(?=\\p{Lu})");
            String type = "";
            for (String aTypeName : typeArray) {
                type += aTypeName + " ";
            }
            type = type.trim();

            CustomTreeItem typeItem = new CustomTreeItem(type, "type");
            typeItem.expandedProperty().addListener(expandedListener);

            for (String year : typeMap.keySet()) {
                CustomTreeItem yearItem = new CustomTreeItem(year, "year");
                for (String month : MONTHS) {
                    int index = 0;
                    for (int i = 0; i < MONTHS.length; i++) {
                        if (MONTHS[i].equals(month)) {
                            index = i+1;
                            break;
                        }
                    }
                    String monthIndex = index < 10 ? "0"+index : ""+index;
                    if (typeMap.get(year).get(monthIndex).size() > 0) {
                        month = month.substring(0,1).toUpperCase() + month.substring(1);
                        CustomTreeItem monthItem = new CustomTreeItem(month, "month");
                        TreeMap<String, ArrayList<Record>> yearMap = typeMap.get(year);
                        for (Record record : yearMap.get(monthIndex)) {
                            monthItem.getChildren().add(new CustomTreeItem(formatDateString(record.getDate(), false), "day"));
                        }
                        yearItem.getChildren().add(monthItem);
                    }
                }
                typeItem.getChildren().add(yearItem);
            }
            dummyRoot.getChildren().add(typeItem);
        }

        TreeView.setRoot(dummyRoot);
        TreeView.setShowRoot(false);
        TreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void bindTreeView() {
        TreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            mPulseAnimation.stop();
            heartIcon.setVisible(false);
            statsContainer.setVisible(true);
            mBarChart.getData().clear();
            mLineChart.getData().clear();
            resetStats(false);

            ObservableList<TreeItem<String>> selectedItems = TreeView.getSelectionModel().getSelectedItems();
            TreeView.getSelectionModel().getSelectedItems();
            String previousTimespan = ((CustomTreeItem)selectedItems.get(0)).getTimeSpan();
            for (TreeItem<String> selectedItem : selectedItems) {
                CustomTreeItem item = (CustomTreeItem) selectedItem;

                if (!previousTimespan.equals(item.getTimeSpan())) break;

                String value = item.getValue();
                String type2 = "HKQuantityTypeIdentifier";
                String year;
                String month = "";
                Object data = mMap;
                System.out.println(previousTimespan + ": " + value);
                switch (previousTimespan) {
                    case "type":
                        type2 += value.replace(" ", "");
                        data = mMap.get(type2);
                        break;
                    case "year":
                        type2 += item.getParent().getValue().replace(" ", "");
                        data = mMap.get(type2).get(value);
                        break;
                    case "month":
                        type2 += item.getParent().getParent().getValue().replace(" ", "");
                        year = item.getParent().getValue();
                        month = monthNameToNumber(value);
                        data = mMap.get(type2).get(year).get(month);
                        break;
                    case "day":
                        type2 += item.getParent().getParent().getParent().getValue().replace(" ", "");
                        year = item.getParent().getParent().getValue();
                        month = item.getParent().getValue().toLowerCase();

                        month = monthNameToNumber(month);
                        int day = 0;
                        ObservableList<TreeItem<String>> siblings = item.getParent().getChildren();
                        for (int i = 0; i < siblings.size(); i++) {
                            if (siblings.get(i) == item) {
                                day = i;
                                break;
                            }
                        }
                        data = mMap.get(type2).get(year).get(month).get(day).getEntries();
                        break;
                }
                if (type2.equals("HKQuantityTypeIdentifierBodyMass")) {
                    graphLineChart(previousTimespan, data, month, selectedItems.size() > 1);
                } else {
                    graphBarChart(previousTimespan, data, month, selectedItems.size() > 1);
                }
            }
//            if (TreeView.getSelectionModel().getSelectedItems().size() == 1) {
//                //String value = newValue.getValue().toLowerCase();
//                String value = newValue.getValue();
//                String timeSpan = ((CustomTreeItem) newValue).getTimeSpan();
//                String type2 = "HKQuantityTypeIdentifier";
//                String year;
//                String month = "";
//                Object data = mMap;
//                System.out.println(timeSpan+": "+value);
//                switch (timeSpan) {
//                    case "type":
//                        type2 += value.replace(" ", "");
//                        data = mMap.get(type2);
//                        //graphBarChart(timeSpan, mMap.get(type2), "");
//                        break;
//                    case "year":
//                        type2 += newValue.getParent().getValue().replace(" ", "");
//                        data = mMap.get(type2).get(value);
//                        //(timeSpan, mMap.get(type2).get(value), "");
//                        break;
//                    case "month":
//                        type2 += newValue.getParent().getParent().getValue().replace(" ", "");
//                        year = newValue.getParent().getValue();
//                        month = monthNameToNumber(value);
//                        data = mMap.get(type2).get(year).get(month);
//                        //graphBarChart(timeSpan, mMap.get(type2).get(year).get(month), month);
//                        break;
//                    case "day":
//                        type2 += newValue.getParent().getParent().getParent().getValue().replace(" ", "");
//                        year = newValue.getParent().getParent().getValue();
//                        month = newValue.getParent().getValue().toLowerCase();
//
//                        month = monthNameToNumber(month);
//                        int day = 0;
//                        ObservableList<TreeItem<String>> siblings = newValue.getParent().getChildren();
//                        for (int i = 0; i < siblings.size(); i++) {
//                            if (siblings.get(i) == newValue) {
//                                day = i;
//                                break;
//                            }
//                        }
//                        data = mMap.get(type2).get(year).get(month).get(day).getEntries();
//                        //graphBarChart(timeSpan, mMap.get(type2).get(year).get(month).get(day).getEntries(), "");
//                        break;
//                }
//                if (type2.equals("HKQuantityTypeIdentifierBodyMass")) {
//                    graphLineChart(timeSpan, data, month, false);
//                } else {
//                    graphBarChart(timeSpan, data, month, false);
//                }
//            } else { // if multiple items were selected
//                System.out.println("multiple");
//                /*
//                String year = TreeView.getSelectionModel().getSelectedItems().get(0).getParent().getValue();
//                int steps = 0;
//                int days = 0;
//                for (int val  = 0; val < TreeView.getSelectionModel().getSelectedItems().size(); val++) {
//                    String value = TreeView.getSelectionModel().getSelectedItems().get(val).getValue().toLowerCase();
//                    XYChart.Series<String, Double> series = new XYChart.Series<>();
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
//                    steps += mStatSteps;
//                    days += mStatDays;
//                }
//                mStatSteps = steps;
//                mStatDays = days;
//                mStatMean = mStatSteps / mStatDays;
//                */
//
//                ObservableList<TreeItem<String>> selectedItems = TreeView.getSelectionModel().getSelectedItems();
//                String previousTimespan = ((CustomTreeItem)selectedItems.get(0)).getTimeSpan();
//                for (int val  = 0; val < selectedItems.size(); val++) {
//                    CustomTreeItem item = (CustomTreeItem) selectedItems.get(val);
//
//                    if (!previousTimespan.equals(item.getTimeSpan())) break;
//
//                    String value = item.getValue();
//                    String type2 = "HKQuantityTypeIdentifier";
//                    String year;
//                    String month = "";
//                    Object data = mMap;
//                    System.out.println(previousTimespan+": "+value);
//                    switch (previousTimespan) {
//                        case "type":
//                            type2 += value.replace(" ", "");
//                            data = mMap.get(type2);
//                            //graphBarChart(timeSpan, mMap.get(type2), "");
//                            break;
//                        case "year":
//                            type2 += item.getParent().getValue().replace(" ", "");
//                            data = mMap.get(type2).get(value);
//                            //(timeSpan, mMap.get(type2).get(value), "");
//                            break;
//                        case "month":
//                            type2 += item.getParent().getParent().getValue().replace(" ", "");
//                            year = item.getParent().getValue();
//                            month = monthNameToNumber(value);
//                            data = mMap.get(type2).get(year).get(month);
//                            //graphBarChart(timeSpan, mMap.get(type2).get(year).get(month), month);
//                            break;
//                        case "day":
//                            type2 += item.getParent().getParent().getParent().getValue().replace(" ", "");
//                            year = item.getParent().getParent().getValue();
//                            month = item.getParent().getValue().toLowerCase();
//
//                            month = monthNameToNumber(month);
//                            int day = 0;
//                            ObservableList<TreeItem<String>> siblings = item.getParent().getChildren();
//                            for (int i = 0; i < siblings.size(); i++) {
//                                if (siblings.get(i) == item) {
//                                    day = i;
//                                    break;
//                                }
//                            }
//                            data = mMap.get(type2).get(year).get(month).get(day).getEntries();
//                            //graphBarChart(timeSpan, mMap.get(type2).get(year).get(month).get(day).getEntries(), "");
//                            break;
//                    }
//                    if (type2.equals("HKQuantityTypeIdentifierBodyMass")) {
//                        graphLineChart(previousTimespan, data, month, true);
//                    } else {
//                        graphBarChart(previousTimespan, data, month, true);
//                    }
//                }
//            }
        });
    }

    private void graphBarChart(String timeSpan, Object data, String month, boolean multiple) {
        mLineChart.setVisible(false);
        mBarChart.setVisible(true);
        //if (!multiple) resetStats();
        resetStats(multiple);
        int level = 0;
        switch (timeSpan) {
            case "type":
                loopType((TreeMap<String, TreeMap<String, ArrayList<Record>>>) data);
                level = 2;
                break;
            case "year":
                loopYear((TreeMap<String, ArrayList<Record>>) data);
                level = 2;
                break;
            case "month":
                loopMonth((ArrayList<Record>) data, month);
                level = 1;
                break;
            case "day":
                loopDay((ArrayList<HashMap<Date, Double>>) data);
                level = 0;
                break;
        }
        
        mBarChart.getData().add(mBarChartSeries);
        if (!timeSpan.equals("day")) {
            mMedian = getMedian(mMedianList); //TODO: Have decimals for distance walked (So it shows like 2.3 and not just 2)
            mStatMean = mStatSteps / mStatDays;
        }
        setStats(level);
    }

    private void graphLineChart(String state, Object data, String month, boolean multiple) {
        mBarChart.setVisible(false);
        mLineChart.setVisible(true);
        resetStats(multiple);

        switch (state) {
            case "type":
                loopType((TreeMap<String, TreeMap<String, ArrayList<Record>>>) data);
                break;
            case "year":
                loopYear((TreeMap<String, ArrayList<Record>>) data);
                break;
            case "month":
                loopMonth((ArrayList<Record>) data, month);
                break;
            case "day":
                loopDay((ArrayList<HashMap<Date, Double>>) data);
                break;
        }
        /*
        ObservableList<XYChart.Data<Integer, Integer>> data = FXCollections.observableArrayList(
                new XYChart.Data<>(1, 23),
                new XYChart.Data<>(2, 14),
                new XYChart.Data<>(20, 15),
                new XYChart.Data<>(4, 24),
                new XYChart.Data<>(5, 34),
                new XYChart.Data<>(6, 36),
                new XYChart.Data<>(7, 22),
                new XYChart.Data<>(8, 45),
                new XYChart.Data<>(9, 43),
                new XYChart.Data<>(10, 17),
                new XYChart.Data<>(11, 29),
                new XYChart.Data<>(12, 25),
                new XYChart.Data<>(11, 40)
        );

        XYChart.Series<Integer, Integer> series = new XYChart.Series<>(data);
        */


        mLineChart.getData().add(mLineChartSeries);
        //TODO: Set BodyMass stats
        setBodyMassStats();
    }

    private void loopDay(ArrayList<HashMap<Date, Double>> day) { // Loops through ArrayList<Record>.get(index).getEntries == ArrayList<HashMap<Date, Double>>
        for (HashMap<Date, Double> map : day) {
            Date key = (Date) map.keySet().toArray()[0];
            double val = map.get(key);
            mBarChartSeries.getData().add(new XYChart.Data<>(key.toString(), val));
            mStatSteps += val;
        }
    }
    
    private void loopMonth(ArrayList<Record> month, String monthKey) { // Loops through ArrayList<Record>
        int monthValue = 0;
        mUnit = month.get(0).getUnit();
        if (mUnit.equals("kg") && mStatWeightStart == 0) {
            mStatWeightStart = month.get(0).getValue();
            mStatWeightCurrent = month.get(month.size()-1).getValue(); //todo doesn't work for current
        }
        for (Record record : month) {
            double value = record.getValue();
            XYChart.Data<String, Double> point = new XYChart.Data<>();
            if (mUnit.equals("kg") ) {
                XYChart.Data<Integer, Integer> point2 = new XYChart.Data<>(mStatWeightCounter, (int) value);
                mStatWeightCounter++;
                mLineChartSeries.getData().add(point2);
            } else {
                point = new XYChart.Data<>(formatDateString(record.getDate(), true), value);
                mBarChartSeries.getData().add(point);
            }
            monthValue += value;
            if (mUnit.equals("kg")) {
                //TODO: Combine with mBestDayValue etc.
                if (value > mStatWeightHighest) {
                    mStatWeightHighest = value;
                } else if (value < mStatWeightLowest) {
                    mStatWeightLowest = value;
                }
            } else {
                mMedianList.add(value);
                mStatSteps += value;
                mStatDays++;
                if (!mUnit.equals("kg")) {
                    if (value > mBestDayValue) {
                        mBestDayValue = (int) value;
                        mBestDayDate = record.getDate();
                        mBestDayPoint = point;
                    } else if (value < mWorstDayValue) {
                        mWorstDayValue = (int) value;
                        mWorstDayDate = record.getDate();
                        mWorstDayPoint = point;
                    }
                }
            }
        }
        if (monthValue > mBestMonthValue) {
            mBestMonthValue = monthValue;
            mBestMonthName = MONTHS[Integer.valueOf(monthKey)-1];
        } else if (monthValue < mWorstMonthValue && monthValue > 0) {
            mWorstMonthValue = monthValue;
            mWorstMonthName = MONTHS[Integer.valueOf(monthKey)-1];
        }
    }

    private void loopYear(TreeMap<String, ArrayList<Record>> year) { // Loops through TreeMap<String, ArrayList<Record>>
        for (String monthKey : year.keySet()) {
            ArrayList<Record> month = year.get(monthKey);
            if (month.size() > 0)
            loopMonth(month, monthKey);
        }
    }
    
    private void loopType(TreeMap<String, TreeMap<String, ArrayList<Record>>> type) { // Loops through TreeMap<String, TreeMap<String, ArrayList<Record>>>
        for (String yearKey : type.keySet()) {
            TreeMap<String, ArrayList<Record>> year = type.get(yearKey);
            loopYear(year);
        }
    }
    
    private void resetStats(boolean multiple) {
        //TODO: fix for multiple
        if (!multiple) {
            mStatSteps = 0;
            mStatDays = 0;
            mBestDayValue = 0;
            mWorstDayValue = Integer.MAX_VALUE;
            mBestMonthValue = 0;
            mWorstMonthValue = Integer.MAX_VALUE;

            mMedianList = new ArrayList<>();

            mStatWeightLowest = Integer.MAX_VALUE;
            mStatWeightHighest = 0;
            mStatWeightCounter = 0;
        }
        mBarChartSeries = new XYChart.Series<>();
        mLineChartSeries = new XYChart.Series<>();
    }

    private double getMedian(ArrayList<Double> data) {
        data.sort(Double::compare);
        int len = data.size();
        if (len % 2 == 0) {
            return (data.get(len/2) + data.get(len/2-1)) / 2;
        } else {
            return (data.get(len/2)).intValue();
        }
    }

    private void setStats(int level) {
        statsSteps.setText(mUnit+": " + punctuateNumber(mStatSteps));
        if (level > 0) {
            statsDays.setText("Days: " + punctuateNumber(mStatDays));
            statsMean.setText("Mean: " + punctuateNumber(mStatMean));
            statsMedian.setText("Median: " + punctuateNumber(mMedian));
            statsBestDay.setText(String.format("Best Day: %s - %s", punctuateNumber(mBestDayValue), formatDateString(mBestDayDate, true)));
            statsBestDay.setOnMouseClicked(event -> mBestDayPoint.getNode().setStyle("-fx-background-color: blue"));
            statsWorstDay.setText(String.format("Worst Day: %s - %s", punctuateNumber(mWorstDayValue), formatDateString(mWorstDayDate, true)));
            statsWorstDay.setOnMouseClicked(event -> mWorstDayPoint.getNode().setStyle("-fx-background-color: blue"));
            if (level > 1) {
                statsBestMonth.setText(String.format("Best Month: %s - %s", punctuateNumber(mBestMonthValue), mBestMonthName));
                statsWorstMonth.setText(String.format("Worst Month: %s - %s", punctuateNumber(mWorstMonthValue), mWorstMonthName));
            }
        }
    }

    private void setBodyMassStats() {
        statsSteps.setText("Start: " + mStatWeightStart);
        statsDays.setText("Current: " + mStatWeightCurrent);
        statsMean.setText("Lowest: " + mStatWeightLowest);
        statsMedian.setText("Highest: " + mStatWeightHighest);
        /*
        statsBestDay.setText(String.format("Best Day: %s - %s", punctuateNumber(mBestDayValue), formatDateString(mBestDayDate, true)));
        statsBestDay.setOnMouseClicked(event -> mBestDayPoint.getNode().setStyle("-fx-background-color: blue"));
        statsWorstDay.setText(String.format("Worst Day: %s - %s", punctuateNumber(mWorstDayValue), formatDateString(mWorstDayDate, true)));
        statsWorstDay.setOnMouseClicked(event -> mWorstDayPoint.getNode().setStyle("-fx-background-color: blue"));
        statsBestMonth.setText(String.format("Best Month: %s - %s", punctuateNumber(mBestMonthValue), mBestMonthName));
        statsWorstMonth.setText(String.format("Worst Month: %s - %s", punctuateNumber(mWorstMonthValue), mWorstMonthName));
        */
    }

    private String formatDateString(Date date, boolean month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        int m = calendar.get(Calendar.MONTH);
        String[] nums = String.valueOf(d).split("");
        String last = nums[nums.length-1];
        String s;
        switch (last) {
            case "1":
                s = "st";
                break;
            case "2":
                s = "nd";
                break;
            case "3":
                s = "rd";
                break;
            default:
                s = "th";
                break;
        }
        if (month) return d + s + " " + MONTHS[m];
        else return d+s;
    }

    private String punctuateNumber(Number number) { //TODO: Doesn't work properly with decimals
        if (number.doubleValue() > 999) {
            int wholeNumber = number.intValue();
            String[] decimalsArray = String.valueOf(number.toString()).split("\\.");
            boolean hasDecimals = false;
            String decimals = "";
            if (decimalsArray.length > 1) {
                hasDecimals = true;
                decimals = decimalsArray[1];
            }
            String[] nums = String.valueOf(wholeNumber).split("");
            String result = "";
            int counter = 0;
            for (int i = nums.length - 1; i > -1; i--) {
                if (counter == 3) {
                    result = "," + result;
                    counter = 0;
                }
                result = nums[i] + result;
                counter++;
            }
            if (hasDecimals) {
                result += "." + decimals;
            }
            return result;
        }
        return new DecimalFormat("#.00").format(number);
    }

    private String monthNameToNumber(String name) {
        name = name.toLowerCase();
        int index = 0;
        for (int i = 0; i < MONTHS.length; i++) {
            if (MONTHS[i].equals(name)) {
                index = i+1;
                break;
            }
        }
        return index < 10 ? "0"+index : ""+index;
    }
}