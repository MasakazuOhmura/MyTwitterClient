package com.masakazuohmura.mytwitterclient.twkit;

import com.twitter.sdk.android.core.models.Identifiable;

import java.util.List;

/**
 * Created by MasakazuOhmura on 2016/06/27.
 */

public class MyTimelineCursor {
    public final Long minPosition;
    public final Long maxPosition;

    /**
     * Constructs a MyTimelineCursor storing position and containsLastItem data.
     * @param minPosition the minimum position of items received or Null
     * @param maxPosition the maximum position of items received or Null
     */
    public MyTimelineCursor(Long minPosition, Long maxPosition) {
        this.minPosition = minPosition;
        this.maxPosition = maxPosition;
    }

    /**
     * Constructs a MyTimelineCursor by reading the maxPosition from the start item and the
     * minPosition from the last item.
     * @param items items from the maxPosition item to the minPosition item
     */
    MyTimelineCursor(List<? extends Identifiable> items) {
        this.minPosition = items.size() > 0 ? items.get(items.size() - 1).getId() : null;
        this.maxPosition = items.size() > 0 ? items.get(0).getId() : null;
    }
}
