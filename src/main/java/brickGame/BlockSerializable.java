package brickGame;

import java.io.Serializable;

public class BlockSerializable implements Serializable {
    public final int row;
    public final int column;
    public final int type;

    public BlockSerializable(int row , int column , int type) {
        this.row = row;
        this.column = column;
        this.type = type;
    }
}
