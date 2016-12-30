package com.github.android.bestpath.backend;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores information on whether a node is used or not, and node coordinates
 */
 class Node {
    private int nodeID;
    private int xCord;
    private int yCord;

    private boolean needToBeHere=false;

    private boolean visited=false;
    //Node id of adjacent nodes that have connection to this node
    private List<Integer> adjacentNodeID=new ArrayList<>();

    /**
     *
     * @param nodeID starts from 0
     * @param mapSize
     *
     * Z=0
     * Z--------> X
     * | 0  1  2
     * | 3  4  5
     * | 6  7  8
     * Y
     */
    Node(int nodeID,int mapSize){
        if(mapSize<2) {
            throw new IllegalArgumentException();
        }
        if(nodeID>=(mapSize*mapSize)){
            throw new IllegalArgumentException();
        }

        this.nodeID=nodeID;
        this.xCord = nodeID % mapSize;
        int temp=nodeID, sqMapSize=mapSize*mapSize;

        this.yCord = temp / mapSize;
        while (temp>=sqMapSize){
            this.yCord-=mapSize;
            temp-=sqMapSize;
        }

    }

    int getXCord(){
        return xCord;
    }

    int getYCord(){
        return yCord;
    }

    int getNodeID(){
        return nodeID;
    }

    void clearAdjacentNodeID(){
        adjacentNodeID.clear();
    }
    List<Integer> getAdjacentNodeID(){
        return adjacentNodeID;
    }
    void addAdjacentNodeID(int nodeID){
        adjacentNodeID.add(nodeID);
    }

    int setNeedToBeHere(boolean needToBeHere){
        this.needToBeHere=needToBeHere;
        return nodeID;
    }
    boolean getNeedToBeHere(){
        return needToBeHere;
    }

    void setVisited(boolean visit){
        visited=visit;
    }
    boolean getVisited(){
        return visited;
    }

}
