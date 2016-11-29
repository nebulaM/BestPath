package com.nebulaM.android.bestpath.backend;

/**
 * Stores information on whether a node is used or not, and node coordinates
 */
public class Node {
    private int nodeID;
    private int xCord;
    private int yCord;

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
    public Node(int nodeID,int mapSize){
        if(mapSize<2) {
            throw new IllegalArgumentException("minimum mapSize is 2");
        }
        if(nodeID>=(mapSize*mapSize)){
            throw new IllegalArgumentException("maximum nodeID is "+mapSize*mapSize);
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

    public int getXCord(){
        return xCord;
    }

    public int getYCord(){
        return yCord;
    }

    public int getNodeID(){
        return nodeID;
    }

}
