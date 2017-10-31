package sample;

import javafx.scene.image.Image;

public class Category {
    private String mName;
    private String mIcon;
    private String mChart;

    public Category(String name, String chart) {
        mName = name;
        mChart = chart;
        switch (name) {
            case "Body Mass ":
                mIcon = "/images/scale_icon_circle.png";
                break;
            case "Distance Walking Running ":
                mIcon = "/images/distance_icon_circle.png";
                break;
            case "Height ":
                mIcon = "/images/height_icon_circle.png";
                break;
            case "Step Count ":
                mIcon = "/images/footprint_icon_circle.png";
                break;
            case "Flights Climbed ":
                mIcon = "/images/stair_icon_circle.png";
                break;
            default:
                mIcon = null;
                break;
        }
    }

    public String getName() {
        return mName;
    }

    public String getIcon() {
        return mIcon;
    }

    public String getChart() {
        return mChart;
    }
}
