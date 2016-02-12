/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.ui;

/**
 *
 * @author
 */
public class ScaleDescriptor {

    public ScaleDescriptor(double scaleCoeffX, double scaleCoeffY) {
        this.scaleCoeffX = scaleCoeffX;
        this.scaleCoeffY = scaleCoeffY;
    }

    public double getScaleCoeffX() {
        return scaleCoeffX;
    }

    public double getScaleCoeffY() {
        return scaleCoeffY;
    }

    public double getMinValue() {
        return scaleCoeffX <= scaleCoeffY ? scaleCoeffX : scaleCoeffY;
    }

    public double getValueByRatio(DirectionRatio directionRatio) {
        if (directionRatio == DirectionRatio.Y_DIRECTION_RATIO) {
            return getScaleCoeffY();
        }
        if (directionRatio == DirectionRatio.X_DIRECTION_RATIO) {
            return getScaleCoeffX();
        }
        if (directionRatio == DirectionRatio.MIN_DIRECTION_RATIO) {
            return getMinValue();
        } else {
            throw new IllegalArgumentException((new StringBuilder()).append("Unsupported direction ratio: ").append(directionRatio).toString());
        }
    }

    @Override
    public String toString() {
        return String.format("ScaleDescriptor [scaleCoeffX=%s, scaleCoeffY=%s]", new Object[]{
            Double.valueOf(scaleCoeffX), Double.valueOf(scaleCoeffY)
        });
    }
    private final double scaleCoeffX;
    private final double scaleCoeffY;
}
