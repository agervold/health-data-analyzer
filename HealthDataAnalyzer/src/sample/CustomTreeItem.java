package sample;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;

public class CustomTreeItem extends TreeItem<String> {
    private String mTimeSpan;

    public CustomTreeItem(String value, String type) {
        super(value);
        mTimeSpan = type;
    }

    public CustomTreeItem(String value, Node graphic, String timeSpan) {
        super(value, graphic);
        mTimeSpan = timeSpan;
    }

    public String getTimeSpan() {
        return mTimeSpan;
    }
}
