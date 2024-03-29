package edu.sjsu.cmpe275.lab2.service;

import java.util.Comparator;
import org.joda.time.Interval;

public class IntervalStartComparator implements Comparator<Interval>{
    @Override
    public int compare(Interval x, Interval y) {
        return x.getStart().compareTo(y.getStart());
    }
}
