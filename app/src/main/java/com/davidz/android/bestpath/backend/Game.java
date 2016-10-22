package com.nebulaM.android.bestpath.backend;


import java.util.ArrayList;
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
    private final int routeSize;
    private final int nodeNum;
    private int edgeProbability;
    private int[][] adjacentArray;
    private List<Node> nodeList;
    private List<Edge> edgeList;
    private final char edgeLevel;

    private static final int edgeCostMax=3;//max edgeCost is 3, we will add 1 to exclusive 0 and inclusive 3 in rand method

    private static final boolean debug=false;//this is better:"true".equals(System.getProperty("debug"));

    private Player mPlayer;

    private int mPlayerEnergy;

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
        this.nodeList=new ArrayList() ;
        this.edgeList=new ArrayList() ;

        this.nodeNum=routeSize*routeSize;

        this.adjacentArray=new int[nodeNum][nodeNum];

        //put all nodes in the nodeList
        for (int i=0;i<nodeNum;++i){
            nodeList.add(new Node(i,this.routeSize));
        }

        if(edgeLevel=='S' || edgeLevel=='M') {
            this.edgeLevel = edgeLevel;
        }
        else
            throw new IllegalArgumentException("choose edgeLevel from one of the following letters: S, M");
        createPath();
        //TODO:list of bestPath
        mPlayerEnergy=shortestPath(0,nodeNum-1);

        mPlayer=new Player(0,nodeNum-1,mPlayerEnergy);

    }

    /**
     * edge between node
     * in total there are 3*routeSize different small adjacent matrices
     * the small adjacent matrices in "createPath" have a size of m[routeSize][routeSize], they will be mapped to a this.adjacentArray in the end
     */
    private void createPath(){

        randomConnectNodes(nodeList,edgeProbability, adjacentArray);
        if(debug)
            printAdjacentMatrix("randomConnectNodes");
       
    }
    


    /**
     * @param nodeList
     * @param probability
     * @param m
     */

    private void randomConnectNodes(List<Node> nodeList, int probability, int[][] m){
        int upperBound=nodeNum;
        int yPositionScale=routeSize;
        Random randEdge = new Random();
        Random randCost = new Random();
        Node startNode, endNode;
        int dx,dy;

        //initialize
        edgeList.clear();
        for(int startIndex=0;startIndex<upperBound;++startIndex) {
            for(int endIndex=0;endIndex < upperBound; ++endIndex){
                m[startIndex][endIndex]=0;
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
                                if (m[endIndex][startIndex]==0) {
                                    if((dx!=0 && dy==0 ) || (dx==0 && dy!=0 ) || (dx==0 && dy==0 )) {
                                        //Case 1: endNode and startNode are in a line that is parallel to one of the Cartesian axis, nothing special
                                        if(!nodeHasEdge(startIndex,m)){
                                            m[startIndex][endIndex]=(1+randCost.nextInt(edgeCostMax));
                                        }
                                        else {
                                            if (randEdge.nextInt(100) <= probability) {//m[startIndex][endIndex]=true, so does m[endIndex][startIndex]
                                                m[startIndex][endIndex]=(1+randCost.nextInt(edgeCostMax));
                                            }
                                        }
                                    }
                                    else if(edgeLevel!='S'){
                                        if(dx!=0 && dy!=0 ){//Case 2: endNode and startNode forms an diagonal of a square on the x-y plane
                                            //Only if the other diagonal of the square is not connected, will we try to connect THIS diagonal
                                            if(m[startIndex+dx][startIndex+(dy*yPositionScale)]==0){
                                                if (randEdge.nextInt(100) <= probability) {
                                                    m[startIndex][endIndex] = (1 + randCost.nextInt(edgeCostMax));
                                                }
                                            }
                                        }
                                    }

                                    m[endIndex][startIndex]=m[startIndex][endIndex];
                                }
                                else {
                                    m[startIndex][endIndex] = m[endIndex][startIndex];
                                }
                            }
                    }
                }
        }


        //Not bi-direction, so endIndex is always bigger than startIndex
        //startIndex always < endIndex
        for(int startIndex=0;startIndex<upperBound;++startIndex) {
            for (int endIndex=startIndex; endIndex<upperBound;++endIndex)
                if(m[startIndex][endIndex]!=0){
                    Edge thisEdge=new Edge( startIndex, endIndex,m[startIndex][endIndex] );
                    edgeList.add(thisEdge);
                }
        }


    }

    private boolean nodeHasEdge(int startIndex, int[][] m){
        for(int endIndex=0; endIndex<nodeNum; ++endIndex){
            if(m[startIndex][endIndex]!=0){
                return true;
            }
        }
        return false;
    }


    /**
     * print out the usage and coordinate of node
     */
    public void printNodeList(String info){
        System.out.println("Tracing node list @"+info+":");
        System.out.println("Node ID\tx\ty");
        for(int i=0;i<nodeNum;++i) {
            System.out.println(nodeList.get(i).getNodeID()+"\t\t"+nodeList.get(i).getXCord()+"\t"+nodeList.get(i).getYCord()+"\t");
        }
    }
    /**
     * print out adjacent matrix for all nodes
     */
    private void printAdjacentMatrix(String info){
        System.out.println("Tracing AdjacentMatrix @"+info+":");
        System.out.println();
        System.out.print("\t");
        for(int i=0;i<nodeNum;++i){
            System.out.print((i+1)+"\t");
        }
        for(int i=0;i<nodeNum;++i){
            System.out.print("\n"+(i+1)+"\t");
            for(int j=0;j<nodeNum;++j){

                    System.out.print(adjacentArray[i][j]+"\t");

            }
        }
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


    public int getEdgeStartXCord(int edgeIndex){

        return nodeList.get(edgeList.get(edgeIndex).getStartNodeIndex()).getXCord();

    }
    public int getEdgeStartYCord(int edgeIndex) {
        return nodeList.get(edgeList.get(edgeIndex).getStartNodeIndex()).getYCord();
    }
    public int getEdgeEndXCord(int edgeIndex) {
        return nodeList.get(edgeList.get(edgeIndex).getEndNodeIndex()).getXCord();
    }
    public int getEdgeEndYCord(int edgeIndex) {
       return nodeList.get(edgeList.get(edgeIndex).getEndNodeIndex()).getYCord();
    }

    public boolean setPlayerPosition(int nodeIndex){
        if(adjacentArray[nodeIndex][mPlayer.getCurrentPosition()]>0){
            mPlayer.costEnergy(adjacentArray[nodeIndex][mPlayer.getCurrentPosition()]);
            mPlayer.setCurrentPosition(nodeIndex);
            return true;
        }
        else{
            return false;
        }
    }

    /**
     *
     * @return
     */
    public int gameOver(){
        //player lost
        if(mPlayer.getEnergy()<=0){
            return -1;
        }
        //player win
        else if(mPlayer.getCurrentPosition()==mPlayer.getFinalPosition()){
            return 1;
        }
        //not end
        else{
            return 0;
        }
    }

    public void resetGame(){
        createPath();
        mPlayerEnergy=shortestPath(0,nodeNum-1);
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
    /**
     *Dijkstra's Algorithm
     *In this method, we DEFINE -1 as infinity
     * Precondition:startNode and endNode >=0
     *              startNode!=endNode
     */
    private int shortestPath(int startNode, int endNode){
        //A list of node ID that shows shortest path from start to end
        List<Integer> shortestPath=new ArrayList<>();
        //map nodeID to cost
        //Map<this,costToStartNode>
        Map<Integer,Integer> nodeCost=new HashMap<>();
        //track previous node of this node
        //Map<this,previous>
        Map<Integer,Integer> nodePrev=new HashMap<>();
        //track all unvisited nodes by nodeID
        Set<Integer> unvisitedNodes=new HashSet<>();
        for(int i=0;i<nodeNum;++i){
            nodeCost.put(i,-1);
            nodePrev.put(i,-1);
            unvisitedNodes.add(i);
        }
        nodeCost.put(startNode,0);
        nodePrev.put(startNode,startNode);
        unvisitedNodes.remove(startNode);
        int currentNode=startNode;
        while(!unvisitedNodes.isEmpty()){
            if (nodeCost.get(currentNode) != -1) {
                unvisitedNodes.remove(currentNode);
                for(int i=0;i<nodeNum;++i) {
                    if (unvisitedNodes.contains(i)) {
                        int thisEdgeCost = adjacentArray[currentNode][i];
                        //thisEdgeCost==0 means no edge
                        if (thisEdgeCost != 0) {
                            int newCost=thisEdgeCost;
                            int checkNode = currentNode;
                            do {
                                //order does not matter, not a bi-direction array
                                newCost += adjacentArray[nodePrev.get(checkNode)][checkNode];
                                checkNode = nodePrev.get(checkNode);
                            }while (checkNode != startNode);
                            if (nodeCost.get(i) == -1 || newCost < nodeCost.get(i)) {
                                nodeCost.put(i, newCost);
                                nodePrev.put(i, currentNode);
                            }
                        }
                    }
                }
            }
            currentNode++;
            if(currentNode>=nodeNum){
                currentNode=0;
            }
        }
        return nodeCost.get(endNode);
    }
}
