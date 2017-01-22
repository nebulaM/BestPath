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

class Player {
    private int currentPosition;
    private int finalPosition;
    private int energy;

    Player(){}

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
