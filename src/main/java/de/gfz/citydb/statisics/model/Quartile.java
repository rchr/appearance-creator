package de.gfz.citydb.statisics.model;

/**
 * Created by richard on 11.08.15.
 */
public class Quartile {

    private double first, second, third;

    public Quartile(double first, double second, double third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public double getFirst() {
        return first;
    }

    public double getSecond() {
        return second;
    }

    public double getThird() {
        return third;
    }

}
