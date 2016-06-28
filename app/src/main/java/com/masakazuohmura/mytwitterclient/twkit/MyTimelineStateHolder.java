package com.masakazuohmura.mytwitterclient.twkit;

/**
 * Created by MasakazuOhmura on 2016/06/27.
 */


import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Component which holds a TimelineAdapter's data about whether a request is in flight and the
 * scroll position MyTimelineCursors.
 */
public class MyTimelineStateHolder {
    // cursor for Timeline 'next' calls
    MyTimelineCursor nextCursor;
    // cursor for Timeline 'previous' calls
    MyTimelineCursor previousCursor;
    // true while a request is in flight, false otherwise
    public final AtomicBoolean requestInFlight = new AtomicBoolean(false);

    public MyTimelineStateHolder() {
        // intentionally blank
    }

    /* for testing */
    public MyTimelineStateHolder(MyTimelineCursor nextCursor, MyTimelineCursor previousCursor) {
        this.nextCursor = nextCursor;
        this.previousCursor = previousCursor;
    }

    /**
     * Nulls the nextCursor and previousCursor
     */
    public void resetCursors() {
        nextCursor = null;
        previousCursor = null;
    }

    /**
     * Returns the position to use for the subsequent Timeline.next call.
     */
    public Long positionForNext() {
        return nextCursor == null ? null : nextCursor.maxPosition;
    }

    /**
     * Returns the position to use for the subsequent Timeline.previous call.
     */
    public Long positionForPrevious() {
        return previousCursor == null ? null : previousCursor.minPosition;
    }

    /**
     * Updates the nextCursor
     */
    public void setNextCursor(MyTimelineCursor timelineCursor) {
        nextCursor = timelineCursor;
        setCursorsIfNull(timelineCursor);
    }

    /**
     * Updates the previousCursor.
     */
    public void setPreviousCursor(MyTimelineCursor timelineCursor) {
        previousCursor = timelineCursor;
        setCursorsIfNull(timelineCursor);
    }

    /**
     * If a nextCursor or previousCursor is null, sets it to timelineCursor. Should be called by
     * setNextCursor and setPreviousCursor to handle the very first timeline load which sets
     * both cursors.
     */
    public void setCursorsIfNull(MyTimelineCursor timelineCursor) {
        if (nextCursor == null) {
            nextCursor = timelineCursor;
        }
        if (previousCursor == null) {
            previousCursor = timelineCursor;
        }
    }

    /**
     * Returns true if a timeline request is not in flight, false otherwise. If true, a caller
     * must later call finishTimelineRequest to remove the requestInFlight lock.
     */
    public boolean startTimelineRequest() {
        return requestInFlight.compareAndSet(false, true);
    }

    /**
     * Unconditionally sets requestInFlight to false.
     */
    public void finishTimelineRequest() {
        requestInFlight.set(false);
    }
}
