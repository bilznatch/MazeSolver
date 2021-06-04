package com.lol.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Maze {
    int[][] maze;
    boolean finalpath = true;
    boolean showDeletions = false;
    Maze(){

    }
    public int[] getNeighbors(int x, int y){
        int[] neighbors;
        //up,right,down,left clockwise
        neighbors = new int[]{maze[x][y+1],maze[x+1][y],maze[x][y-1],maze[x-1][y]};
        return neighbors;
    }
    public void fillFromEnds(){
        int count = 0;
        for(int i = 0; i < maze.length;i++){
            for(int j = 0; j < maze[0].length;j++){
                if(maze[i][j] == 0){
                    if(maze[i+1][j]==1 || maze[i+1][j] == 3){
                        count++;
                    }
                    if(maze[i-1][j]==1 || maze[i-1][j] == 3){
                        count++;
                    }
                    if(maze[i][j+1]==1 || maze[i][j+1] == 3){
                        count++;
                    }
                    if(maze[i][j-1]==1 || maze[i][j-1] == 3){
                        count++;
                    }
                    if(count>=3){
                        maze[i][j]=3;
                        finalpath = true;
                        count=0;
                    }else{
                        count = 0;
                    }
                }
            }
        }
        if(!finalpath){
            setFinalPath();
        }
        finalpath = false;
    }
    public void drawMaze(SpriteBatch batch, TextureRegion tileTex){
        for(int i = 0; i < maze.length;i++){
            for(int j = 0; j < maze[0].length; j++){
                if(maze[i][j] == 1){
                    batch.setColor(Color.DARK_GRAY);
                }else if(maze[i][j] == 0){
                    batch.setColor(Color.CORAL);
                }else if(maze[i][j] == -1){
                    batch.setColor(Color.CORAL);
                }else if(maze[i][j] == 2){
                    batch.setColor(Color.GREEN);
                }else if(maze[i][j] == 3){
                    if(showDeletions){
                        batch.setColor(Color.GOLDENROD);
                    }else{
                        batch.setColor(Color.DARK_GRAY);
                    }
                }else if(maze[i][j]==4){
                    batch.setColor(Color.MAGENTA);
                }else if(maze[i][j]==5){
                    batch.setColor(Color.PURPLE);
                }
                batch.draw(tileTex,200+i*32,200+j*32,32,32);
            }
        }
    }
    public void setRunner(int x, int y){
        resetMaze();
        if(maze[x][y] == 0) maze[x][y] = -1;
    }
    public void resetMaze(){
        for(int i = 0; i < maze.length;i++){
            for(int j = 0; j < maze[0].length;j++){
                if(maze[i][j]!=1 && maze[i][j]!=2)maze[i][j]=0;
            }
        }
    }
    public void setFinalPath(){
        for(int i = 0; i < maze.length;i++){
            for(int j = 0; j < maze[0].length;j++){
                if(maze[i][j]==0)maze[i][j]=4;
            }
        }
    }
}
