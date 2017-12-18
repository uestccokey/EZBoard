package cn.ezandroid.goboard;

import java.io.Serializable;

public interface IBoardModel extends Serializable {

    public void forcePut(int x, int y, GoPoint point);

    public void forceRemove(int x, int y);

    public void performPut(int x, int y, GoPoint point) throws GoException;

    public void performRemove(int x, int y) throws GoException;

    public int getBoardSize();

    public IGridModel getGridModel();

    public GoPoint getPoint(int x, int y);

    public void reset(int boardSize);
}
