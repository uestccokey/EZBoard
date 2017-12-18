package cn.ezandroid.goboard;

public class DefaultBoardModel implements IBoardModel {

    private static final long serialVersionUID = 1L;

    private IGridModel mGridModel;

    private int mNextPlayer = GoPoint.PLAYER_BLACK;

    public DefaultBoardModel(int bsize) {
        mGridModel = new DefaultGridModel(bsize);
    }

    @Override
    public void forcePut(int x, int y, GoPoint point) {
        if (!validatePoint(x, y)) {
            return;
        }

        GoPoint p = getPoint(x, y);
        if (p.getPlayer() != GoPoint.PLAYER_EMPTY) {
            return;
        }

        p.setPlayer(point.getPlayer());
        p.setNumber(point.getNumber());
    }

    @Override
    public void forceRemove(int x, int y) {
        if (!validatePoint(x, y)) {
            return;
        }

        GoPoint p = getPoint(x, y);
        if (p.getPlayer() == GoPoint.PLAYER_EMPTY) {
            return;
        }

        p.setPlayer(GoPoint.PLAYER_EMPTY);
        p.setNumber(GoPoint.NONE);
    }

    public void setNextPlayer(int nextPlayer) {
        mNextPlayer = nextPlayer;
    }

    public int getNextPlayer() {
        return mNextPlayer;
    }

    @Override
    public int getBoardSize() {
        return mGridModel.getGridSize();
    }

    public void setGridModel(IGridModel gridModel) {
        mGridModel = gridModel;
    }

    @Override
    public IGridModel getGridModel() {
        return mGridModel;
    }

    @Override
    public GoPoint getPoint(int col, int row) {
        return mGridModel.getObject(col, row);
    }

    @Override
    public void performPut(int x, int y, GoPoint point) throws GoException {
        if (!validatePoint(x, y)) {
            return;
        }

        GoPoint p = getPoint(x, y);
        if (p.getPlayer() != GoPoint.PLAYER_EMPTY) {
            throw new GoException("There is already one chessman:" + x + "x" + y + " " + point
                    .getNumber() + " " + p.getNumber());
        }

        p.setPlayer(point.getPlayer());
        p.setNumber(point.getNumber());
    }

    @Override
    public void performRemove(int x, int y) throws GoException {
        if (!validatePoint(x, y)) {
            throw new GoException("Invalid point");
        }

        GoPoint p = getPoint(x, y);
        if (p.getPlayer() == GoPoint.PLAYER_EMPTY) {
            throw new GoException("No chessman to remove");
        }

        p.setPlayer(GoPoint.PLAYER_EMPTY);
        p.setNumber(GoPoint.NONE);
    }

    private boolean validatePoint(int col, int row) {
        return !((col < 0) || (col >= getBoardSize()) || (row < 0) || (row >= getBoardSize()));
    }

    @Override
    public void reset(int boardSize) {
        mGridModel.reset(boardSize);
        mNextPlayer = GoPoint.PLAYER_BLACK;
    }
}
