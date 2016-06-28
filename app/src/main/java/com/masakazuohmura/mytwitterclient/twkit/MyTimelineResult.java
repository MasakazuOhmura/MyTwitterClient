package com.masakazuohmura.mytwitterclient.twkit;

import java.util.List;

/**
 * Created by MasakazuOhmura on 2016/06/27.
 */

public class MyTimelineResult<T> {

    public final MyTimelineCursor timelineCursor;
    public final List<T> items;

    /**
     * Constructs a TimelineResult storing item and cursor data.
     * @param timelineCursor cursor representing position and containsLastItem data
     * @param items timeline items
     */
    public MyTimelineResult(MyTimelineCursor timelineCursor, List<T> items) {
        this.timelineCursor = timelineCursor;
        this.items = items;
    }
}