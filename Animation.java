package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;

public class Animation {
	
	public Cube cube;
	public Vector3 vector;
	public int duration;
	public int blend;
	public static final int DURATION_MAX_DEFAULT = 10;
	public static int DURATION_MAX = DURATION_MAX_DEFAULT;
	public Color color;

	public Animation(Cube cube, Vector3 vector) {
		this.cube = cube;
		this.vector = vector;
		duration = DURATION_MAX;
		color = cube.color;
	}
	
	
	
	public void render() {
		if (duration == DURATION_MAX) {
			for (int k = 2; k >= 0; k--) //y
				for (int i = 2; i >= 0; i--) //z
					for (int j = 0; j < 3; j++) //x
		        	{
						if (cube == ShiftCube.positions[i][j][k]) {
							if (cube.n == ShiftCube.solve[i][j][k] && color.equals(ColorPicker.current.wrong)) { //make correct
								blend = -1;
							} else if (cube.n != ShiftCube.solve[i][j][k] && color.equals(ColorPicker.current.correct)) { //make wrong
								blend = 1;
							}
						}
		        	}
		}
		for (ModelInstance instance : cube.instances) {
			instance.transform.translate(vector.cpy().scl(5f/DURATION_MAX));
		}
		duration--;
		
		if (blend != 0) {
			color = (blend == 1 ? ColorPicker.current.correct : ColorPicker.current.wrong).cpy().lerp((blend == 1 ? ColorPicker.current.wrong : ColorPicker.current.correct), 1 - (float)duration/DURATION_MAX);
			if (duration == 0) {
				color = (blend == 1 ? ColorPicker.current.wrong : ColorPicker.current.correct);
				blend = 0;
			}
			cube.color = color;
			for (ModelInstance instance : cube.instances) {
				instance.materials.get(0).set(ColorAttribute.createDiffuse(color));
			}
		}
	}

}
