package brickgame;

import java.io.Serializable;

/**
 * The BlockSerializable class represents a serializable block in the game.
 * It implements the Serializable interface, which enables its instances to be written to streams as objects.
 * The original source code can be found in the BlockSerializable.java file.
 */
public class BlockSerializable implements Serializable {
    /**
     * The row position of the block in the game grid.
     */
    public final int row;

    /**
     * The column position of the block in the game grid.
     */
    public final int column;

    /**
     * The type of the block, which can be used to determine its behavior or appearance in the game.
     */
    public final int type;

    /**
     * Constructs a new BlockSerializable with the specified row, column, and type.
     *
     * @param row    the row position of the block in the game grid
     * @param column the column position of the block in the game grid
     * @param type   the type of the block
     */
    public BlockSerializable(int row , int column , int type) {
        this.row = row;
        this.column = column;
        this.type = type;
    }
}