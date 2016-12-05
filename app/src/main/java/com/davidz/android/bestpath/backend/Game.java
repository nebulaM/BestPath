package com.nebulaM.android.bestpath.backend;


import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Create a new 2D game
 *
 */
public class Game {
    private final String TAG="Game";
    private final int routeSize;
    private final int nodeNum;
    private int edgeProbability;
    private List<ArrayList<Integer>> adjacentArray;
    private List<Node> nodeList;
    private List<Edge> edgeList;
    private final char edgeLevel;

    private static final int edgeCostMax=3;//max edgeCost is 3, we will add 1 to exclusive 0 and inclusive 3 in rand method

    private Player mPlayer;

    private int mPlayerEnergy;
    private List<Integer> mShortestList=new ArrayList<>();

    /**
     *
     * @param routeSize number of node in a single dimension
     *
     *
     * @param edgeProbability
     *
     * @param edgeLevel options are S for Simple, M for Medium or H for Hard
     */

    public Game(int routeSize, int edgeProbability, char edgeLevel){
        if(routeSize>1) {
            this.routeSize = routeSize;
        }
        else
            throw new IllegalArgumentException("routeSize must greater than 1");

        this.edgeProbability=edgeProbability;
        this.nodeList=Collections.synchronizedList(new ArrayList()) ;
        this.edgeList=Collections.synchronizedList(new ArrayList()) ;

        this.nodeNum=routeSize*routeSize;

        this.adjacentArray= Collections.synchronizedList(new ArrayList<ArrayList<Integer>>());

        //put all nodes in the nodeList
        for (int i=0;i<nodeNum;++i){
            nodeList.add(new Node(i,this.routeSize));
        }

        if(edgeLevel=='S' || edgeLevel=='M') {
            this.edgeLevel = edgeLevel;
        } else {
            throw new IllegalArgumentException("choose edgeLevel from one of the following letters: S, M");
        }
        this.createPath(this.nodeList,this.edgeList, this.edgeProbability, this.adjacentArray);

        mPlayerEnergy=shortestPath(0,nodeNum-1,this.adjacentArray);

        mPlayer=new Player(0,nodeNum-1,mPlayerEnergy);

    }
    /**
     * edge between node
     * in total there are 3*routeSize different small adjacent matrices
     * the small adjacent matrices in "createPath" have a size of m[routeSize][routeSize], they will be mapped to a this.adjacentArray in the end
     */
    private void createPath(List<Node> nodeList, List<Edge> edgeList, int edgeProbability, List<ArrayList<Integer>> adjacentArray){

        randomConnectNodes(nodeList,edgeList,edgeProbability, adjacentArray);
        //keep track on all adjacent nodes of this node
        for(Node n : nodeList){
            n.clearAdjacentNodeID();
            for(int i=0;i<adjacentArray.size();++i){
                if(adjacentArray.get(n.getNodeID()).get(i)!=0){
                    n.addAdjacentNodeID(i);
                }
            }
        }
    }
    /**
     * @param nodeList
     * @param probability
     * @param m
     */

    private void randomConnectNodes(List<Node> nodeList, List<Edge> edgeList, int probability, List<ArrayList<Integer>> m){
        int upperBound=nodeNum;
        int yPositionScale=routeSize;
        Random randEdge = new Random();
        Random randCost = new Random();
        Node startNode, endNode;
        int dx,dy;

        //initialize
        edgeList.clear();
        m.clear();
        for(int startIndex=0;startIndex<upperBound;++startIndex) {
            m.add(new ArrayList<Integer>());
            for(int endIndex=0;endIndex < upperBound; ++endIndex){
                m.get(startIndex).add(0);
            }
        }
        //start to put connection information in adjacentMatrix
        for(int startIndex=0;startIndex<upperBound;++startIndex) {
            startNode = nodeList.get(startIndex);
                for (int endIndex = 0; endIndex < upperBound; ++endIndex) {
                    // a node does not connect to itself
                    if (startIndex != endIndex) {
                        endNode = nodeList.get(endIndex);
                            dx = endNode.getXCord() - startNode.getXCord();
                            dy = endNode.getYCord() - startNode.getYCord();

                            //only if the distance between two nodes <= 1 in all of x, y, z direction, will we consider connect the nodes.
                            if (Math.abs(dx) <= 1 && Math.abs(dy) <= 1 ) {
                                //Graph is not bi-direct, so m[i][j] = m[j][i]
                                if (m.get(endIndex).get(startIndex)==0) {
                                    if((dx!=0 && dy==0 ) || (dx==0 && dy!=0 ) || (dx==0 )) {
                                        //Case 1: endNode and startNode are in a line that is parallel to one of the Cartesian axis, nothing special
                                        if(!nodeHasEdge(startIndex,m)){
                                            m.get(startIndex).set(endIndex,1+randCost.nextInt(edgeCostMax));
                                        }
                                        else {
                                            if (randEdge.nextInt(100) <= probability) {//m[startIndex][endIndex]=true, so does m[endIndex][startIndex]
                                                m.get(startIndex).set(endIndex,1+randCost.nextInt(edgeCostMax));
                                            }
                                        }
                                    }
                                    else if(edgeLevel!='S'){
                                            //Case 2: endNode and startNode forms an diagonal of a square on the x-y plane
                                            //Only if the other diagonal of the square is not connected, will we try to connect THIS diagonal
                                            if(m.get(startIndex+dx).get(startIndex+(dy*yPositionScale))==0){
                                                if (randEdge.nextInt(100) <= probability) {
                                                    m.get(startIndex).set(endIndex,1 + randCost.nextInt(edgeCostMax));
                                                }
                                            }

                                    }

                                    m.get(endIndex).set(startIndex,m.get(startIndex).get(endIndex));
                                }
                                else {
                                    m.get(endIndex).set(startIndex,m.get(startIndex).get(endIndex));
                                }
                            }
                    }
                }
        }
        //Not bi-direction, so endIndex is always bigger than startIndex
        //startIndex always < endIndex
        for(int startIndex=0;startIndex<upperBound;++startIndex) {
            for (int endIndex=startIndex; endIndex<upperBound;++endIndex)
                if(m.get(startIndex).get(endIndex)!=0){
                    Edge thisEdge=new Edge( startIndex, endIndex,m.get(startIndex).get(endIndex) );
                    edgeList.add(thisEdge);
                }
        }


    }
    private boolean nodeHasEdge(int startIndex, List<ArrayList<Integer>> m){
        for(int endIndex=0; endIndex<nodeNum; ++endIndex){
            if(m.get(startIndex).get(endIndex)!=0){
                return true;
            }
        }
        return false;
    }

    public int getNodeNum(){
        return nodeNum;
    }

    public int getEdgeNum(){
        if(!edgeList.isEmpty())
            return edgeList.size();
        else
            return 0;
    }

    public int getEdgeCost(int index){
        return edgeList.get(index).getEdgeCost();
    }

    public int getEdgeStartNode(int edgeIndex){

        return edgeList.get(edgeIndex).getStartNodeIndex();

    }

    public int getEdgeEndNode(int edgeIndex){

        return edgeList.get(edgeIndex).getEndNodeIndex();

    }


    public void setPlayerPosition(int nodeIndex){
        int cost=adjacentArray.get(nodeIndex).get(mPlayer.getCurrentPosition());
        if(cost<=0){//no edge between currentPosition and target node
            return;
        } else if( mPlayer.getEnergy()>=cost && mPlayer.getCurrentPosition()!=nodeIndex){
            mPlayer.costEnergy(cost);
            mPlayer.setCurrentPosition(nodeIndex);

        } else{
            mPlayer.setEnergy(0);
        }
    }
    /**
     *
     * @return
     */
    public int gameOver(int nodeIndex){
        //player win
        if(mPlayer.getCurrentPosition()==mPlayer.getFinalPosition()){
            return 1;
        } else{
            List<Integer> adjacentNodeID=nodeList.get(nodeIndex).getAdjacentNodeID();
            for(int i=0;i<adjacentNodeID.size();++i){
                //at least player can move to one node next to this node
                if(adjacentArray.get(nodeIndex).get(adjacentNodeID.get(i))<=mPlayer.getEnergy()){
                    //not end
                    return 0;
                }
            }
            //player lose
            return -1;
        }

    }

    public void resetGame(){
        createPath(nodeList, edgeList, edgeProbability, adjacentArray);
        mPlayerEnergy=shortestPath(0,nodeNum-1,adjacentArray);
        mPlayer.setCurrentPosition(0);
        mPlayer.setEnergy(mPlayerEnergy);
    }

    public void resetPlayer(){
        mPlayer.setCurrentPosition(0);
        mPlayer.setEnergy(mPlayerEnergy);
    }

    public int getPlayerPosition() {
        return mPlayer.getCurrentPosition();
    }

    public int getPlayerEnergy(){
        return mPlayer.getEnergy();
    }

    public int getNodeXCord(int nodeIndex){
        return nodeList.get(nodeIndex).getXCord();
    }

    public int getNodeYCord(int nodeIndex){
        return nodeList.get(nodeIndex).getYCord();
    }

    public int getMaxEnergy(){
        return mPlayerEnergy;
    }

    public List<Integer> getShortestList(){
        return mShortestList;
    }
    /**
     *Dijkstra's Algorithm
     * PreCondition:startNode and endNode >=0
     *              startNode!=endNode
     * PostCondition:return the cost of best path
     *               save one possible shortestPath in mShortestPath, from endNode to startNode.
     */
    private int shortestPath(int startNode, int endNode, List<ArrayList<Integer>> adjacentArray){
        //A list of node ID that shows shortest path from start to end
        List<Integer> shortestPath=new ArrayList<>();
        //map node to cost
        //Map<this,costToStartNode>
        Map<Node,Integer> nodeCost=new HashMap<>();
        //track previous node of this node
        //Map<this,previous>
        Map<Integer,Integer> nodePrev=new HashMap<>();
        //track all unvisited nodes by nodeID
        Set<Integer> unvisitedNodes=new HashSet<>();
        for(int i=0;i<nodeNum;++i){
            int cost=(adjacentArray.get(startNode).get(i)==0)?Integer.MAX_VALUE-1:adjacentArray.get(startNode).get(i);
            nodeCost.put(nodeList.get(i),cost);
            if(cost!=Integer.MAX_VALUE-1){
                nodePrev.put(i,startNode);
            }else{
                nodePrev.put(i,Integer.MAX_VALUE);
            }
            unvisitedNodes.add(i);
        }
        nodePrev.put(startNode,startNode);
        unvisitedNodes.remove(startNode);
        int currentNode=startNode;
        while(!unvisitedNodes.isEmpty()){
            int minCost=Integer.MAX_VALUE;
            for(Integer nodeID : unvisitedNodes){
                int cost=nodeCost.get(nodeList.get(nodeID));
                if (cost < minCost) {
                    minCost = cost;
                    currentNode= nodeID;
                }
            }
            unvisitedNodes.remove(currentNode);
            Log.d(TAG,"unvisited "+ unvisitedNodes);
            List<Integer> neighborIDList=nodeList.get(currentNode).getAdjacentNodeID();
            Log.d(TAG, "neighbor of "+currentNode +" are "+neighborIDList);
            for(Integer i : neighborIDList) {
                if (unvisitedNodes.contains(i)) {
                    int newCost=adjacentArray.get(currentNode).get(i);
                    int checkNode = currentNode;
                    while (checkNode != startNode){
                        //order does not matter, not a bi-direction array
                        newCost += adjacentArray.get(nodePrev.get(checkNode)).get(checkNode);
                        checkNode = nodePrev.get(checkNode);
                    }
                   if (newCost < nodeCost.get(nodeList.get(i))) {
                        nodeCost.put(nodeList.get(i), newCost);
                        nodePrev.put(i, currentNode);
                       Log.d(TAG, "update cost for neighbor "+i+ " w/ new cost "+newCost);
                    }
                }
            }
        }
        int thisNode=endNode;
        while(thisNode!=startNode){
            shortestPath.add(thisNode);
            thisNode=nodePrev.get(thisNode);
        }
        shortestPath.add(startNode);
        if(!mShortestList.isEmpty()) {
            mShortestList.clear();
        }
        mShortestList=shortestPath;
        System.out.println(""+mShortestList);
        System.out.println(""+nodeCost.get(nodeList.get(endNode)));
        return nodeCost.get(nodeList.get(endNode));
    }
}
