package com.mygdx.game;

public class Move {
	
	public int axis;
	public int layer;
	public boolean inverse;
	
	public Move(int axis, int layer, boolean inverse) {
		this.axis = axis;
		this.layer = layer;
		this.inverse = inverse;
	}
	
	public Move(Move move, boolean inverse) {
		this(move.axis, move.layer, inverse);
	}
	
	public boolean isEvent() {
		if (axis == -1 && layer == -1) {
			Animation.DURATION_MAX = Animation.DURATION_MAX_DEFAULT;
			ShiftCube.started = true;
			return true;
		}
		return false;
	}
	
}
