package com.nebulaM.android.bestpath.backend;

/**
 * Created by nebulaM on 9/3/2016.
 */
public class Edge {
    private final int startNodeIndex;
    private final int endNodeIndex;
    private final int edgeCost;

    public Edge(int startNodeIndex, int endNodeIndex, int edgeCost){
        this.startNodeIndex=startNodeIndex;
        this.endNodeIndex=endNodeIndex;
        this.edgeCost=edgeCost;

    }

    public int getStartNodeIndex(){
        return startNodeIndex;
    }

    public int getEndNodeIndex(){
        return endNodeIndex;
    }

    public  int getEdgeCost(){
        return edgeCost;
    }
}
