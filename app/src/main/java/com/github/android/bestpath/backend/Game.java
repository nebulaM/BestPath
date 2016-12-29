package com.github.android.bestpath.backend;


import android.util.SparseIntArray;

import com.github.android.bestpath.MainActivity;

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
    //total number of node
    private int mNodeNum;
    //num of node in one row/column
    private int mGameLevel=0;

    private enum GameMode{EASY,NORMAL,HARD}
    //0 to 2, easy to hard
    private GameMode mGameMode;

    //adjacentArray has all cost of nodes
    private List<ArrayList<Integer>> adjacentArray;
    //list of all nodes
    private List<Node> nodeList;
    //list of all edges
    private List<Edge> edgeList;
    //char 'S' means no diagonal edges
    private final char edgeLevel;
    //max edgeCost is 3, we will add 1 to exclusive 0 and inclusive 3 in rand method
    private static final int edgeCostMax=3;
    //player on this map
    private Player mPlayer;
    //energy assigned to player in game
    private int mPlayerEnergy;
    //ID of first node
    private final int startNodeID=0;
    //ID of last node(destination)
    private int endNodeID;
    //random number generator
    private Random RNG=new Random();

    //hard mode: nodeID of the node needs to be visited in quadrant 1
    private int q1NodeIndex;
    //hard mode: nodeID of the node needs to be visited in quadrant 3
    private int q3NodeIndex;
    //candidate nodes in quadrant 1/3
    private List<Node> quadrant1NodeList;
    private List<Node> quadrant3NodeList;
    //quadrant boundaries, equal when number of node in a row is even
    private int quadrantPosBound;
    private int quadrantNegBound;
    //list contains all nodes of a potential(probably have more than one path) shortest path
    private List<Integer> mShortestList;
    //potential shortest path in hard mode
    private List<Integer> mShortestListCandidate1;
    private List<Integer> mShortestListCandidate2;

    //edge probability in easy mode, due to the way of implementation, 40 actually = ~80% probability of having an edge between two nodes
    private int edgeProbability=30;
    //which nodeList to put the path in
    private enum PathList{ShortestList, ShortestListCandidate1, ShortestListCandidate2, NoList}
    //game state
    public enum GameState{PLAYER_WIN,PLAYER_LOSE,GAME_NOT_END}

    //means no edge between two nodes, important that this value is less than Integer.MAX_VALUE for a compare in shortest path
    public static final int noEdge=Integer.MAX_VALUE-1;
    //return to this value if we successfully set the player here
    public static final int setPlayer=10;

    public static final int NOT_SHOW_GAME_STAGE=-2;
    private int mStageCleared;

    //can only increment stage clear once when check gameOver
    private boolean mGameRecordLocked =false;

    private boolean mGameRecordWriteFlag=false;
    //location 0-5 contains clear times in normal mode, from lv1 to lv6
    //location 6-11 contains clear times in hard mode, from lv1 to lv6
    private List<Integer> mGameRecord;


    /**
     *
     * @param edgeLevel options are S for Simple(no diagonal), M for Medium(has diagonal)
     */

    public Game(char edgeLevel){

        if(edgeLevel=='S' || edgeLevel=='M') {
            this.edgeLevel = edgeLevel;
        } else {
            throw new IllegalArgumentException("choose edgeLevel from one of the following letters: S, M");
        }

        nodeList=Collections.synchronizedList(new ArrayList()) ;
        edgeList=Collections.synchronizedList(new ArrayList()) ;
        adjacentArray= Collections.synchronizedList(new ArrayList<ArrayList<Integer>>());
        quadrant1NodeList=new ArrayList<>();
        quadrant3NodeList=new ArrayList<>();
        mShortestList=new ArrayList<>();
        mShortestListCandidate1=new ArrayList<>();
        mShortestListCandidate2=new ArrayList<>();
        mGameRecord=new ArrayList<>();

        mPlayer = new Player();
    }

    /**
     *
     * @param gameLevel number of node in a row/column
     */
    public void init(int gameLevel,int gameMode, List gameRecord) {
        setGameMode(gameMode);
        unlockGameRecord();
        //first time start the game or change to a new level
        if (mGameLevel == 0 || mGameLevel != gameLevel) {
            if (gameLevel > 1) {
                mGameLevel = gameLevel;
            } else
                throw new IllegalArgumentException("mGameLevel must greater than 1");
            mNodeNum = gameLevel * gameLevel;
            endNodeID = mNodeNum - 1;
            nodeList.clear();
            edgeList.clear();
            adjacentArray.clear();
            quadrantPosBound = gameLevel / 2 + gameLevel % 2;
            quadrantNegBound = gameLevel / 2 - gameLevel % 2;
            //put all nodes in the nodeList
            for (int i = 0; i < mNodeNum; ++i) {
                nodeList.add(new Node(i, mGameLevel));
            }
        }

        if(gameRecord!=null){
            mGameRecord.addAll(gameRecord);
            //Log.d(TAG,"@init: mGameRecord is "+mGameRecord);
        }
        setStageCleared();

        createPath(nodeList, edgeList, adjacentArray);

        for (Node n : nodeList) {
            n.setNeedToBeHere(false);
            n.setVisited(false);
        }

        switch (mGameMode) {
            case HARD:
                quadrant1NodeList.clear();
                quadrant3NodeList.clear();
                mShortestListCandidate1.clear();
                mShortestListCandidate2.clear();
                for (Node n : nodeList) {
                    if (n.getXCord() >= quadrantPosBound && n.getYCord() <= quadrantNegBound) {
                        quadrant1NodeList.add(n);
                    } else if (n.getXCord() <= quadrantNegBound && n.getYCord() >= quadrantPosBound) {
                        quadrant3NodeList.add(n);
                    }
                }
                //Log.d(TAG, "quadrant1NodeList.size()" + quadrant1NodeList.size());
                //Log.d(TAG, "quadrant3NodeList.size()" + quadrant3NodeList.size());
                q1NodeIndex = quadrant1NodeList.get(RNG.nextInt(quadrant1NodeList.size())).setNeedToBeHere(true);
                q3NodeIndex = quadrant3NodeList.get(RNG.nextInt(quadrant3NodeList.size())).setNeedToBeHere(true);
                nodeList.get(endNodeID).setNeedToBeHere(true);

                int energy1 = shortestPath(startNodeID, q1NodeIndex, adjacentArray, true, true, PathList.ShortestListCandidate1);
                energy1 += shortestPath(q1NodeIndex, q3NodeIndex, adjacentArray, true, false, PathList.ShortestListCandidate1);
                energy1 += shortestPath(q3NodeIndex, endNodeID, adjacentArray, true, false, PathList.ShortestListCandidate1);

                int energy2 = shortestPath(startNodeID, q3NodeIndex, adjacentArray, true, true, PathList.ShortestListCandidate2);
                energy2 += shortestPath(q3NodeIndex, q1NodeIndex, adjacentArray, true, false, PathList.ShortestListCandidate2);
                energy2 += shortestPath(q1NodeIndex, endNodeID, adjacentArray, true, false, PathList.ShortestListCandidate2);
                //Log.d(TAG, "energy1 " + energy1);
                // Log.d(TAG, "energy2 " + energy2);
                mShortestList.clear();
                if (energy1 < energy2) {
                    mShortestList.addAll(mShortestListCandidate1);
                    mPlayerEnergy = energy1;
                } else {
                    mShortestList.addAll(mShortestListCandidate2);
                    mPlayerEnergy = energy2;
                }
                //Log.d(TAG, "shortestList " + mShortestList);
                //Log.d(TAG, "mPlayerEnergy " + mPlayerEnergy);
                mShortestList.add(startNodeID);
                break;
            default:
                nodeList.get(endNodeID).setNeedToBeHere(true);
                mPlayerEnergy = shortestPath(startNodeID, endNodeID, this.adjacentArray, true, true, PathList.ShortestList);
                break;

        }
        mPlayer.setCurrentPosition(startNodeID);
        mPlayer.setFinalPosition(endNodeID);
        mPlayer.setEnergy(mPlayerEnergy);

    }

    public void resetPlayer(){
        for(Node n: nodeList){
            n.setVisited(false);
        }
        mPlayer.setCurrentPosition(0);
        mPlayer.setEnergy(mPlayerEnergy);
        setStageCleared();
    }

    public int getGameMode(){
        switch(mGameMode){
            case EASY:
                return 0;
            case NORMAL:
                return 1;
            case HARD:
                return 2;
            default:
                throw new IllegalArgumentException("Unknown game mode");
        }
    }
    private void setGameMode(int gameMode){
        switch (gameMode) {
            case 0:
                mGameMode = GameMode.EASY;
                break;
            case 1:
                mGameMode=GameMode.NORMAL;
                break;
            case 2:
                mGameMode=GameMode.HARD;
                break;
            default:
                throw new IllegalArgumentException("Game Mode must between 0-2");
        }
    }

    private void unlockGameRecord(){
        mGameRecordLocked =false;
    }
    private void lockGameRecord(){
        mGameRecordLocked =true;
    }

    public boolean getNodeNeedVisit(int nodeIndex){
        return nodeList.get(nodeIndex).getNeedToBeHere();
    }
    /**
     * edge between node
     * in total there are 3*mGameLevel different small adjacent matrices
     * the small adjacent matrices in "createPath" have a size of m[mGameLevel][mGameLevel], they will be mapped to a this.adjacentArray in the end
     */
    private void createPath(List<Node> nodeList, List<Edge> edgeList, List<ArrayList<Integer>> adjacentArray){

        randomConnectNodes(nodeList,edgeList,adjacentArray);
        //keep track on all adjacent nodes of this node
        for(Node n : nodeList){
            n.clearAdjacentNodeID();
            for(int i=0;i<adjacentArray.size();++i){
                if(adjacentArray.get(n.getNodeID()).get(i)!=noEdge){
                    n.addAdjacentNodeID(i);
                }
            }
        }
    }

    private void randomConnectNodes(List<Node> nodeList, List<Edge> edgeList, List<ArrayList<Integer>> m){
        int upperBound= mNodeNum;
        int yPositionScale= mGameLevel;
        Node startNode, endNode;
        int dx,dy;

        //initialize
        edgeList.clear();
        m.clear();
        for(int startIndex=0;startIndex<upperBound;++startIndex) {
            m.add(new ArrayList<Integer>());
            for(int endIndex=0;endIndex < upperBound; ++endIndex){
                m.get(startIndex).add(noEdge);
            }
        }
        //start to put connection information in adjacentMatrix
        /*switch (mGameMode) {
            case EASY:
                for (int startIndex = 0; startIndex < upperBound; ++startIndex) {
                    startNode = nodeList.get(startIndex);
                    for (int endIndex = 0; endIndex < upperBound; ++endIndex) {
                        // a node does not connect to itself
                        if (startIndex != endIndex) {
                            endNode = nodeList.get(endIndex);
                            dx = endNode.getXCord() - startNode.getXCord();
                            dy = endNode.getYCord() - startNode.getYCord();
                            //only if the distance between two nodes <= 1 in all of x, y, z direction, will we consider connect the nodes.
                            if (Math.abs(dx) <= 1 && Math.abs(dy) <= 1) {
                                int prob = RNG.nextInt(100);
                                //Graph is not bi-direct, so m[i][j] = m[j][i]
                                int otherWay=m.get(endIndex).get(startIndex);
                                if (otherWay==noEdge) {
                                    if (dy == 0 || dx == 0) {
                                        //Case 1: endNodeID and startNode are in a line that is parallel to one of the Cartesian axis, nothing special
                                        if (prob > 50) {
                                            m.get(startIndex).set(endIndex, edgeCostMax);
                                        } else {
                                            m.get(startIndex).set(endIndex, edgeCostMax - 1);
                                        }
                                    } else if (edgeLevel != 'S') {
                                        //Case 2: endNodeID and startNode forms an diagonal of a square on the x-y plane
                                        //Only if the other diagonal of the square is not connected, will we try to connect THIS diagonal
                                        if (m.get(startIndex + dx).get(startIndex + (dy * yPositionScale)) == noEdge) {
                                            if (endIndex < startIndex) {
                                                //endIndex<startIndex, dx0<0: means edge "/", high probability w/ low cost
                                                if (dx > 0) {
                                                    if (prob <= 60) {
                                                        if (prob <= 40) {
                                                            m.get(startIndex).set(endIndex, 1);
                                                        } else {
                                                            m.get(startIndex).set(endIndex, 2);
                                                        }
                                                    }
                                                } else {//edge "\", low prob w/ high cost
                                                    if (prob <= 20) {
                                                        if (prob <= 10) {
                                                            m.get(startIndex).set(endIndex, 2);
                                                        } else {
                                                            m.get(startIndex).set(endIndex, 3);
                                                        }
                                                    }
                                                }
                                            } else {//endIndex>startIndex
                                                if (dx < 0) {
                                                    if (prob <= 55) {
                                                        if (prob <= 40) {
                                                            m.get(startIndex).set(endIndex, 1);
                                                        } else {
                                                            m.get(startIndex).set(endIndex, 2);
                                                        }
                                                    }
                                                } else {
                                                    if (prob <= 35) {
                                                        if (prob <= 10) {
                                                            m.get(startIndex).set(endIndex, 1);
                                                        } else if (prob <= 20) {
                                                            m.get(startIndex).set(endIndex, 2);
                                                        } else {
                                                            m.get(startIndex).set(endIndex, 3);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    int thisWay=m.get(startIndex).get(endIndex);
                                    if(thisWay!=noEdge) {
                                        m.get(endIndex).set(startIndex, thisWay);
                                    }
                                } else {
                                    m.get(startIndex).set(endIndex, otherWay);
                                }
                            }
                        }
                    }
                }
                break;
            default:*/
        switch (mGameMode){
            case HARD:
                edgeProbability=30;
                break;
            case NORMAL:
                edgeProbability=35;
                break;
            case EASY:
                edgeProbability=25;
                break;
            default:
                edgeProbability=30;
                break;
        }
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
                        int otherWay=m.get(endIndex).get(startIndex);
                        if (otherWay==noEdge) {
                            if (dy == 0 || dx == 0) {
                                //Case 1: endNode and startNode are in a line that is parallel to one of the Cartesian axis, nothing special
                                if(startIndex==endNodeID) {
                                    m.get(startIndex).set(endIndex,1+RNG.nextInt(edgeCostMax));
                                }
                                else if(!nodeHasEdge(startIndex,m)){
                                    m.get(startIndex).set(endIndex,1+RNG.nextInt(edgeCostMax));
                                } else {
                                    if (RNG.nextInt(100) <= edgeProbability) {//m[startIndex][endIndex]=true, so does m[endIndex][startIndex]
                                        m.get(startIndex).set(endIndex,1+RNG.nextInt(edgeCostMax));
                                    }
                                }
                            } else if(edgeLevel!='S'){
                                //Case 2: endNode and startNode forms an diagonal of a square on the x-y plane
                                //Only if the other diagonal of the square is not connected, will we try to connect THIS diagonal
                                if(m.get(startIndex+dx).get(startIndex+(dy*yPositionScale))==noEdge){
                                    if(startIndex==endNodeID) {
                                        m.get(startIndex).set(endIndex,1+RNG.nextInt(edgeCostMax));
                                    }
                                    else if (RNG.nextInt(100) <= edgeProbability) {

                                        m.get(startIndex).set(endIndex,1 + RNG.nextInt(edgeCostMax));
                                    }
                                }
                            }
                            int thisWay=m.get(startIndex).get(endIndex);
                            if(thisWay!=noEdge) {
                                m.get(endIndex).set(startIndex, thisWay);
                            }
                        } else {
                            m.get(startIndex).set(endIndex,otherWay);
                        }
                    }
                }
            }
        }
        //break;}

        //Not bi-direction, so endIndex is always bigger than startIndex
        //startIndex always < endIndex
        for(int startIndex=0;startIndex<upperBound;++startIndex) {
            for (int endIndex=startIndex; endIndex<upperBound;++endIndex)
                if(m.get(startIndex).get(endIndex)!=noEdge){
                    Edge thisEdge=new Edge( startIndex, endIndex,m.get(startIndex).get(endIndex) );
                    edgeList.add(thisEdge);
                }
        }
    }
    private boolean nodeHasEdge(int startIndex, List<ArrayList<Integer>> m){
        for(int endIndex=0; endIndex<mNodeNum; ++endIndex){
            if(m.get(startIndex).get(endIndex)!=noEdge){
                return true;
            }
        }
        return false;
    }

    public int getNodeNum(){
        return mNodeNum;
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

    public int setPlayerPosition(int nodeIndex){
        int cost=adjacentArray.get(nodeIndex).get(mPlayer.getCurrentPosition());
        if(cost==noEdge){//no edge between currentPosition and target node
            return noEdge;
        } else if( mPlayer.getEnergy()>=cost && mPlayer.getCurrentPosition()!=nodeIndex){
            mPlayer.costEnergy(cost);
            mPlayer.setCurrentPosition(nodeIndex);
            nodeList.get(nodeIndex).setVisited(true);
            return setPlayer;
        } else{
            mPlayer.setEnergy(0);
            return setPlayer;
        }
    }
    /**
     *
     * @return state of game
     */
    public GameState gameOver(){
        switch(mGameMode){
            case HARD:
                //not check energy every step
                if(mPlayer.getCurrentPosition()==mPlayer.getFinalPosition()&&nodeList.get(q1NodeIndex).getVisited()&&nodeList.get(q3NodeIndex).getVisited()){
                    if(!mGameRecordLocked) {//update record
                        int normalized_game_level = mGameLevel - MainActivity.SP_KEY_GAME_LEVEL_DEFAULT;
                        mStageCleared++;
                        mGameRecord.set(normalized_game_level + mGameRecord.size() / 2, mStageCleared);
                        mGameRecordWriteFlag = true;
                        lockGameRecord();
                    }
                    return GameState.PLAYER_WIN;
                }else{
                    if(mPlayer.getEnergy()>0){
                        return GameState.GAME_NOT_END;
                    }else{
                        return GameState.PLAYER_LOSE;
                    }
                }
            case EASY:
                //not check energy every step
                if(mPlayer.getCurrentPosition()==mPlayer.getFinalPosition()){
                    return GameState.PLAYER_WIN;
                }else{
                    if(mPlayer.getEnergy()>0){
                        return GameState.GAME_NOT_END;
                    }else{
                        return GameState.PLAYER_LOSE;
                    }
                }

            case NORMAL:
                //not check energy every step
                if(mPlayer.getCurrentPosition()==mPlayer.getFinalPosition()){
                    if(!mGameRecordLocked) {//update record
                        int normalized_game_level = mGameLevel - MainActivity.SP_KEY_GAME_LEVEL_DEFAULT;
                        mStageCleared++;
                        mGameRecord.set(normalized_game_level, mStageCleared);
                        mGameRecordWriteFlag = true;
                        lockGameRecord();
                    }
                    return GameState.PLAYER_WIN;
                }else{
                    if(mPlayer.getEnergy()>0){
                        return GameState.GAME_NOT_END;
                    }else{
                        return GameState.PLAYER_LOSE;
                    }
                }
                /*
                //check energy every step
                //player win
                if(mPlayer.getCurrentPosition()==mPlayer.getFinalPosition()){
                    if(!mGameRecordLocked) {//update record
                        int normalized_game_level=mGameLevel- MainActivity.SP_KEY_GAME_LEVEL_DEFAULT;
                        mStageCleared++;
                        mGameRecord.set(normalized_game_level,mStageCleared);
                        mGameRecordWriteFlag=true;
                        lockGameRecord();
                    }
                    return GameState.PLAYER_WIN;
                } else{
                    //minimum energy required to reach endNodeID
                    int minEnergy = this.shortestPath(mPlayer.getCurrentPosition(), endNodeID, adjacentArray, false, false,  PathList.NoList);
                    //-1 means player loss, 0 means game not end
                    return minEnergy > mPlayer.getEnergy() ? GameState.PLAYER_LOSE : GameState.GAME_NOT_END;
                }*/
            default:
                throw new IllegalArgumentException(TAG+" @gameOver unknown GameState");
        }
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
     * PreCondition:startNode and endNodeID >=0
     *              startNode!=endNodeID
     * PostCondition:return the cost of best path
     *               save one possible shortestPath in mShortestPath, from endNodeID to startNode.
     */
    private int shortestPath(int startNode, int endNode, List<ArrayList<Integer>> adjacentArray, boolean save, boolean cleanListBeforeSave, PathList pathCode){
        //A list of node ID that shows shortest path from start to end
        List<Integer> shortestPath=new ArrayList<>();
        //map node to cost
        //Map<this,costToStartNode>
        Map<Node,Integer> nodeCost=new HashMap<>();
        //track previous node of this node
        //Map<this,previous>
        SparseIntArray nodePrev=new SparseIntArray();
        //track all unvisited nodes by nodeID
        Set<Integer> unvisitedNodes=new HashSet<>();
        //initialize path and cost for each node, if no path to startNode
        for(int i = 0; i< mNodeNum; ++i){
            int cost=(adjacentArray.get(startNode).get(i)==noEdge)?noEdge:adjacentArray.get(startNode).get(i);
            nodeCost.put(nodeList.get(i),cost);
            if(cost!=noEdge){
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
            //Log.d(TAG,"unvisited "+ unvisitedNodes);
            List<Integer> neighborIDList=nodeList.get(currentNode).getAdjacentNodeID();
            //Log.d(TAG, "neighbor of "+currentNode +" are "+neighborIDList);
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
                        //Log.d(TAG, "update cost for neighbor "+i+ " w/ new cost "+newCost);
                    }
                }
            }
        }
        int thisNode=endNode;
        while(thisNode!=startNode){
            shortestPath.add(thisNode);
            thisNode=nodePrev.get(thisNode);
        }
        //save shortest path
        if(save) {
            if(mGameMode!=GameMode.HARD) {
                shortestPath.add(startNode);
                setPath(shortestPath,pathCode,true);

            }else{
                setPath(shortestPath,pathCode,cleanListBeforeSave);

            }
        }
        return nodeCost.get(nodeList.get(endNode));
    }


    private void setPath(List pathList,PathList pathCode, boolean cleanListBeforeSave){
        switch (pathCode){
            case ShortestList:
                if(cleanListBeforeSave){
                    mShortestList.clear();
                }
                mShortestList.addAll(pathList);
                break;
            case ShortestListCandidate1:
                if(cleanListBeforeSave){
                    mShortestListCandidate1.clear();
                }
                mShortestListCandidate1.addAll(pathList);
                //Log.d(TAG,"shortestListCandidate1 "+ mShortestListCandidate1);
                break;
            case ShortestListCandidate2:
                if(cleanListBeforeSave) {
                    mShortestListCandidate2.clear();
                }
                mShortestListCandidate2.addAll(pathList);
                //Log.d(TAG,"shortestListCandidate2 "+ mShortestListCandidate2);
                break;
            default:
                break;
        }
    }


    public int getGameLevel(){
        return mGameLevel;
    }


    private void setStageCleared(){
        int normalized_game_level=mGameLevel- MainActivity.SP_KEY_GAME_LEVEL_DEFAULT;
        switch (mGameMode){
            case HARD:
                mStageCleared=mGameRecord.get(normalized_game_level+mGameRecord.size()/2);
                break;
            case NORMAL:
                mStageCleared=mGameRecord.get(normalized_game_level);
                break;
            default:
                mStageCleared=NOT_SHOW_GAME_STAGE;
                break;
        }
    }


    public int getStageCleared(){
        return mStageCleared;
    }

    /**
     *
     * @param checkIfNeedWrite if true, the method will check if mGameRecordWriteFlag is dirty
     *                         dirty means we need write back to shared preference
     * @return either the updated game record list or null
     */
    public List getGameRecord(boolean checkIfNeedWrite) {
        if(!checkIfNeedWrite){
            return mGameRecord;
        }
        else if(mGameRecordWriteFlag) {
            return mGameRecord;
        }else{
            return null;
        }
    }

}
