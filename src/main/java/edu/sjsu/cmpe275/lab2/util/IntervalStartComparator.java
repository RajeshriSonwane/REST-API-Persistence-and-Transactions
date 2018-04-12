package edu.sjsu.cmpe275.lab2.util;

import org.joda.time.Interval;

import java.util.Comparator;

public class IntervalStartComparator implements Comparator<Interval> {
    @Override
    public int compare(Interval x, Interval y) {
        return x.getStart().compareTo(y.getStart());
    }
}
