package com.github.android.bestpath.backend;

class Player {
    private int currentPosition;
    private int finalPosition;
    private int energy;

    Player(){
    }



     void setCurrentPosition(int position){
        if(position>=0)
            currentPosition=position;
        else
            throw new IllegalArgumentException("nodeIndex must >0");
    }

     int getCurrentPosition(){
        return currentPosition;
    }

     void setFinalPosition(int position){
        finalPosition=position;
    }

     int getFinalPosition(){
        return finalPosition;
    }

     void costEnergy(int edgeCost){
        energy-=edgeCost;

    }

     void setEnergy(int energy){
        this.energy=energy;
    }

     int getEnergy(){
        return energy;
    }
}
