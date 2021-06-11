package com.lol.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

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
    MazeGenerator(int width, int height){
        RBGenerate(width, height);
    }
    public void RBGenerate(int width, int height){
        generateNewMaze(width,height);
    }
    public void generateNewMaze(int width, int height){
        maze = new MazeCell[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                maze[i][j] = new MazeCell();
            }
        }
        if(MathUtils.randomBoolean()){
            startX = 0;
            startY = MathUtils.random(0,height-1);
        }else{
            startX = MathUtils.random(0,width-1);
            startY = 0;
        }
        tunnelToNeighbor(startX,startY);
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                while(maze[i][j].walls[0]&&maze[i][j].walls[1]&&maze[i][j].walls[2]&&maze[i][j].walls[3]){
                    shuffleArray(dir);
                    if(dir[0]=='N'){
                        if(j+1 <= maze[0].length-1){
                            maze[i][j].walls[0] = false;
                            maze[i][j+1].walls[1] = false;
                        }
                    }
                    if(dir[0]=='S'){
                        if(j-1 >= 0){
                            maze[i][j].walls[1] = false;
                            maze[i][j-1].walls[0] = false;
                        }
                    }
                    if(dir[0]=='E'){
                        if(i+1 <= maze.length-1){
                            maze[i][j].walls[2] = false;
                            maze[i+1][j].walls[3] = false;
                        }
                    }
                    if(dir[0]=='W'){
                        if(i-1 >= 0){
                            maze[i][j].walls[3] = false;
                            maze[i-1][j].walls[2] = false;
                        }
                    }
                }
            }
        }
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                maze[i][j].visited = false;
            }
        }
        setRandomGoal();
    }
    public void tunnelToNeighbor(int x, int y){
        maze[x][y].visited = true;
        shuffleArray(dir);
        for(char c: dir){
            if(c=='N'){
                if(y+1 <= maze[x].length-1){
                    if(!maze[x][y+1].visited){
                        maze[x][y].walls[0] = false;
                        maze[x][y+1].walls[1] = false;
                        tunnelToNeighbor(x,y+1);
                    }
                }
            }else if(c=='S'){
                if(y-1 >= 0){
                    if(!maze[x][y-1].visited){
                        maze[x][y].walls[1] = false;
                        maze[x][y-1].walls[0] = false;
                        tunnelToNeighbor(x,y-1);
                    }
                }
            }else if(c=='E'){
                if(x+1 <= maze.length-1){
                    if(!maze[x+1][y].visited){
                        maze[x][y].walls[2] = false;
                        maze[x+1][y].walls[3] = false;
                        tunnelToNeighbor(x+1,y);
                    }
                }
            }else if(c=='W'){
                if(x-1 >= 0){
                    if(!maze[x-1][y].visited){
                        maze[x][y].walls[3] = false;
                        maze[x-1][y].walls[2] = false;
                        tunnelToNeighbor(x-1,y);
                    }
                }

            }
        }
    }
    public void shuffleArray(char[] ar)
    {
        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i > 0; i--) {
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
        float resolution = 32f * (30f/maze.length);
        float wallthickness = resolution * 0.2f;
        float walllength = resolution + wallthickness;
        float faroffset = resolution-wallthickness/2;
        float nearoffset = -wallthickness/2;
        batch.setColor(Color.CORAL);
        batch.draw(tex,100,100,resolution*maze.length,resolution*maze[0].length);
        for(int i = 0;i<maze.length;i++){
            for(int j=0;j<maze[0].length;j++){
                if(maze[i][j].visited && !maze[i][j].deleted && highlightPath){
                    batch.setColor(Color.MAROON);
                    batch.draw(tex,100+i*resolution,100+j*resolution,resolution,resolution);
                }
                if(maze[i][j].isGoal){
                    batch.setColor(Color.LIME);
                    batch.draw(tex,100+i*resolution,100+j*resolution,resolution,resolution);
                }
            }
        }
        for(int i = 0; i < maze.length;i++){
            for(int j = 0; j < maze[0].length;j++){
                batch.setColor(Color.DARK_GRAY);
                if(showDeletions && maze[i][j].deleted && !highlightDeletions){
                   batch.draw(tex,100+i*resolution+nearoffset,100+j*resolution+nearoffset,walllength,walllength);
                }else{
                    if(maze[i][j].walls[0]){
                        batch.draw(tex,100+i*resolution+nearoffset,100+j*resolution+faroffset,walllength,wallthickness);
                    }
                    if(maze[i][j].walls[1]){
                        batch.draw(tex,100+i*resolution+nearoffset,100+j*resolution+nearoffset,walllength,wallthickness);
                    }
                    if(maze[i][j].walls[2]){
                        batch.draw(tex,100+i*resolution+faroffset,100+j*resolution+nearoffset,wallthickness,walllength);
                    }
                    if(maze[i][j].walls[3]){
                        batch.draw(tex,100+i*resolution+nearoffset,100+j*resolution+nearoffset,wallthickness,walllength);
                    }
                    if(highlightDeletions && showDeletions && maze[i][j].deleted){
                        batch.setColor(highlighterColor);
                        batch.draw(tex,100+i*resolution,100+j*resolution,resolution,resolution);
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
}
