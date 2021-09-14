package com.lol.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MazeGenerator {
    int startX = 0, startY = 0;
    char[] dir = new char[]{'N','S','E','W'};
    boolean showDeletions = false;
    boolean highlightPath = false;
    boolean highlightDeletions = false;
    Color highlighterColor = new Color(1,1,0,0.35f);
    MazeCell[][] maze;
    float resolution;

    MazeGenerator(int width, int height){
        generateNewMaze(width, height);
    }

    public void generateNewMaze(int width, int height){
        maze = new MazeCell[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                maze[i][j] = new MazeCell();
            }
        }
        getRandomStartingPosition();
        maze[startX][startY].visited = true;
        tunnelToNeighbor(startX,startY);
        resetVisited();
        setRandomGoal();
        resolution = 1050f/maze.length; //divides the maze into even parts out of 1050 so it will always fit on screen.
    }

    public void getRandomStartingPosition(){
        //choose a random point in the grid to propagate the maze from.
        startY = MathUtils.random(0,maze.length-1);
        startX = MathUtils.random(0,maze[0].length-1);
    }

    /*
     tunnelToNeighbor: Check the tile in a random direction, if it is unexplored,
     open a wall between the current tile and that tile, then check the neighbors of that tile.
     When a dead end is met, and all neighbors have been explored, backtrack through the stack checking any remaining
     unexplored neighbors.
     */
    public void tunnelToNeighbor(int x, int y){
        shuffleArray(dir);
        while(!neighborsAllVisited(x,y)){
            for(int i = 0; i < dir.length;i++) {
                if (dir[i] == 'N') {
                    if (y + 1 <= maze[x].length - 1) {
                        if (!maze[x][y + 1].visited) {
                            maze[x][y + 1].visited = true;
                            maze[x][y].walls[0] = false;
                            maze[x][y + 1].walls[1] = false;
                            tunnelToNeighbor(x, y + 1);
                        }
                    }
                } else if (dir[i] == 'S') {
                    if (y - 1 >= 0) {
                        if (!maze[x][y - 1].visited) {
                            maze[x][y - 1].visited = true;
                            maze[x][y].walls[1] = false;
                            maze[x][y - 1].walls[0] = false;
                            tunnelToNeighbor(x, y - 1);
                        }
                    }
                } else if (dir[i] == 'E') {
                    if (x + 1 <= maze.length - 1) {
                        if (!maze[x + 1][y].visited) {
                            maze[x + 1][y].visited = true;
                            maze[x][y].walls[2] = false;
                            maze[x + 1][y].walls[3] = false;
                            tunnelToNeighbor(x + 1, y);
                        }
                    }
                } else if (dir[i] == 'W') {
                    if (x - 1 >= 0) {
                        if (!maze[x - 1][y].visited) {
                            maze[x - 1][y].visited = true;
                            maze[x][y].walls[3] = false;
                            maze[x - 1][y].walls[2] = false;
                            tunnelToNeighbor(x - 1, y);
                        }
                    }

                }
            }
        }
    }

    //validates whether all neighbors have been visited.
    public boolean neighborsAllVisited(int x, int y){
        MazeCell[] neighbors = new MazeCell[4];
        if(y+1 <= maze[0].length-1){
            neighbors[0] = maze[x][y+1];
        }
        if(y-1 >= 0){
            neighbors[1] = maze[x][y-1];
        }
        if(x+1 <= maze.length-1){
            neighbors[2] = maze[x+1][y];
        }
        if(x-1 >= 0){
            neighbors[3] = maze[x-1][y];
        }
        for(MazeCell mc: neighbors){
            if(mc != null){
                if(!mc.visited) return false;
            }
        }
        return true;
    }

    public void resetVisited(){
        for(int i = 0; i < maze.length; i++){
            for(int j = 0; j < maze[0].length; j++){
                maze[i][j].visited = false;
            }
        }
    }

    public void shuffleArray(char[] ar){
        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i >= 0; i--) {
            int index = rnd.nextInt(i + 1);
            char a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    public MazeCell[] getNeighbors(int x, int y){
        MazeCell[] neighbors = new MazeCell[4];
        if(y+1 <= maze[0].length-1){
            neighbors[0] = maze[x][y+1];
        }
        if(y-1 >= 0){
            neighbors[1] = maze[x][y-1];
        }
        if(x+1 <= maze.length-1){
            neighbors[2] = maze[x+1][y];
        }
        if(x-1 >= 0){
            neighbors[3] = maze[x-1][y];
        }
        return neighbors;
    }

    public void drawMaze(SpriteBatch batch, TextureRegion tex){
        float wallthickness = resolution * 0.2f;
        float walllength = resolution + wallthickness;
        float faroffset = resolution-wallthickness/2;
        float nearoffset = -wallthickness/2;
        batch.setColor(Color.CORAL);
        batch.draw(tex,10,10,resolution*maze.length,resolution*maze[0].length);
        for(int i = 0;i<maze.length;i++){
            for(int j=0;j<maze[0].length;j++){
                if(maze[i][j].visited && !maze[i][j].deleted && highlightPath){
                    batch.setColor(Color.MAROON);
                    batch.draw(tex,10+i*resolution,10+j*resolution,resolution,resolution);
                }
                if(maze[i][j].isGoal){
                    batch.setColor(Color.LIME);
                    batch.draw(tex,10+i*resolution,10+j*resolution,resolution,resolution);
                }
            }
        }
        for(int i = 0; i < maze.length;i++){
            for(int j = 0; j < maze[0].length;j++){
                batch.setColor(Color.DARK_GRAY);
                if(showDeletions && maze[i][j].deleted && !highlightDeletions){
                   batch.draw(tex,10+i*resolution+nearoffset,10+j*resolution+nearoffset,walllength,walllength);
                }else{
                    if(maze[i][j].walls[0]){
                        batch.draw(tex,10+i*resolution+nearoffset,10+j*resolution+faroffset,walllength,wallthickness);
                    }
                    if(maze[i][j].walls[1]){
                        batch.draw(tex,10+i*resolution+nearoffset,10+j*resolution+nearoffset,walllength,wallthickness);
                    }
                    if(maze[i][j].walls[2]){
                        batch.draw(tex,10+i*resolution+faroffset,10+j*resolution+nearoffset,wallthickness,walllength);
                    }
                    if(maze[i][j].walls[3]){
                        batch.draw(tex,10+i*resolution+nearoffset,10+j*resolution+nearoffset,wallthickness,walllength);
                    }
                    if(highlightDeletions && showDeletions && maze[i][j].deleted){
                        batch.setColor(highlighterColor);
                        batch.draw(tex,10+i*resolution,10+j*resolution,resolution,resolution);
                    }

                }
            }
        }
    }

    public void setRandomGoal(){
        maze[MathUtils.random(maze.length-1)][MathUtils.random(maze[0].length-1)].isGoal = true;
    }

    public void resetMaze(){
        for(MazeCell[] mca: maze){
            for(MazeCell mc: mca){
                mc.visited = false;
                mc.deleted = false;
                mc.isGoal = false;
            }
        }
    }

    public MazeCell getCell(Vector2 pos){
        pos.set(pos.x-10,pos.y-10);
        pos.set(pos.x/resolution,pos.y/resolution);
        int x = (int) MathUtils.clamp(pos.x,0,maze.length-1);
        int y = (int) MathUtils.clamp(pos.y,0,maze[0].length-1);
        return maze[x][y];
    }
}
