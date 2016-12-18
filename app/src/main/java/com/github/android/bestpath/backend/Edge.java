package com.github.android.bestpath.backend;

class Edge {
    private final int startNodeIndex;
    private final int endNodeIndex;
    private final int edgeCost;

    Edge(int startNodeIndex, int endNodeIndex, int edgeCost){
        this.startNodeIndex=startNodeIndex;
        this.endNodeIndex=endNodeIndex;
        this.edgeCost=edgeCost;

    }

    int getStartNodeIndex(){
        return startNodeIndex;
    }

    int getEndNodeIndex(){
        return endNodeIndex;
    }

    int getEdgeCost(){
        return edgeCost;
    }
}
