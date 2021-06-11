package com.lol.game;

public class MazeCell {
    int x, y;
    boolean[] walls;
    boolean visited;
    boolean deleted;
    boolean isGoal;
    MazeCell(){
        walls = new boolean[]{true, true, true, true}; //N, S, E, W
        visited = false;
        deleted = false;
        isGoal = false;
    }
}
