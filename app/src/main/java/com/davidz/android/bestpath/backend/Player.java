package com.nebulaM.android.bestpath.backend;

/**
 * Created by nebulaM on 9/6/2016.
 */
public class Player {
    private int currentPosition;
    private final int finalPosition;
    private int energy;

    public Player(int startPosition, int endPosition, int energy){
        if(startPosition>=0) {
            this.currentPosition = startPosition;
        }
        else
            throw new IllegalArgumentException("startPosition must >=0");
        if(endPosition>startPosition) {
            this.finalPosition = endPosition;
        }
        else
            throw new IllegalArgumentException("endPosition must > startPosition");
        this.energy=energy;
    }

    public void costEnergy(int edgeCost){
        energy-=edgeCost;

    }

    public int getEnergy(){
        return energy;
    }

    public void setCurrentPosition(int nodeIndex){
        if(nodeIndex>=0)
            currentPosition=nodeIndex;
        else
            throw new IllegalArgumentException("nodeIndex must >0");
    }
    public int getCurrentPosition(){
        return currentPosition;
    }

    public int getFinalPosition(){
        return finalPosition;
    }

    public void setEnergy(int energy){
        this.energy=energy;
    }

}
