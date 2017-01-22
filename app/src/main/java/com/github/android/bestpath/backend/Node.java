/*
 * Copyright (C) 2017 by nebulaM <nebulam12@gmail.com>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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
