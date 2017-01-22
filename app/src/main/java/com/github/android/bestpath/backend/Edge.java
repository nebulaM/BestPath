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
