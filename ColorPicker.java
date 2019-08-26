package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class ColorPicker {
	
	public static ColorVariant current;
	Color c = new Color(88/255f, 161/255f, 1f, 1f);
	
	public ColorPicker() {
		current = ColorVariant.DEFAULT;
	}
	
	public enum ColorVariant {
		DEFAULT(Color.ROYAL, new Color(206/255f, 42/255f, 42/255f, 1f), new Color(.15f, .15f, .15f, 1f)),
		FOREST(Color.FOREST, Color.FIREBRICK, Color.BLACK),
		DARK(Color.GRAY, new Color(.15f, .15f, .15f, 1f), Color.BLACK),
		ORANGE(new Color(1f, 150/255f, 0f, 1f), new Color(175/255f, 41/255f, 0f, 1f), Color.DARK_GRAY),
		BROWN(Color.TAN, Color.BROWN, Color.BLACK),
		LIGHT(Color.SKY, Color.CORAL, Color.LIGHT_GRAY),
		SLATE(Color.SLATE, Color.DARK_GRAY, new Color(.1f, .1f, .1f, 1f)),
		SEA(Color.TEAL, Color.SALMON, Color.NAVY),
		NEON(Color.CHARTREUSE, new Color(.1f, .1f, .1f, 1f), Color.BLACK),
		PURPLE(new Color(187/255f, 48/255f, 215/255f, 1f), new Color(127/255f, 20/255f, 70/255f, 1f), Color.PINK),
		;
		
		public final Color correct;
		public final Color wrong;
		public final Color core;
		
		private ColorVariant(Color correct, Color wrong, Color core) {
			this.correct = correct;
			this.wrong = wrong;
			this.core = core;
		}
	}
	
	public void draw() {
		ShiftCube.sr.begin(ShapeType.Filled);
		ShiftCube.sr.setColor(Color.WHITE);
		ShiftCube.sr.rect(1050, 100, 150, 40);
		ShiftCube.sr.rect(1050+2, 100+2, 150-4, 40-4, Color.ROYAL, c, c, Color.ROYAL);
		ShiftCube.sr.triangle(1040, 100, 1040, 140, 1020, 120);
		ShiftCube.sr.triangle(1210, 100, 1210, 140, 1230, 120);
		ShiftCube.sr.setColor(Color.ROYAL);
		ShiftCube.sr.triangle(1040-2, 100+3, 1040-2, 140-3, 1020+1.5f, 120);
		ShiftCube.sr.triangle(1210+2, 100+3, 1210+2, 140-3, 1230-1.5f, 120);
		ShiftCube.sr.end();
		
		Gdx.gl20.glEnable(GL20.GL_BLEND);
		if (current.ordinal() > 0) {
        	if (Gdx.input.getX() > 1020 && Gdx.input.getX() < 1040 && 750-Gdx.input.getY() > 100 && 750-Gdx.input.getY() < 140) {
        		ShiftCube.sr.begin(ShapeType.Filled);
        		ShiftCube.sr.setColor(1f, 1f, 1f, .35f);
        		ShiftCube.sr.triangle(1040, 100, 1040, 140, 1020, 120);
	            if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
	            	ShiftCube.sr.setColor(1f, 1f, 1f, .35f);
	            	ShiftCube.sr.triangle(1040, 100, 1040, 140, 1020, 120);
	                if (ShiftCube.left()) {
	                	ColorVariant old = current;
	                	current = ColorVariant.values()[current.ordinal() - 1];
	                	color(current, old);
	                }
	        	}
	            ShiftCube.sr.end();
        	}
        } else {
        	ShiftCube.sr.begin(ShapeType.Filled);
        	ShiftCube.sr.setColor(1f, 0f, 0f, .5f);
        	ShiftCube.sr.triangle(1040, 100, 1040, 140, 1020, 120);
        	ShiftCube.sr.end();
        }
		if (current.ordinal() < ColorVariant.values().length - 1) {
        	if (Gdx.input.getX() > 1210 && Gdx.input.getX() < 1230 && 750-Gdx.input.getY() > 100 && 750-Gdx.input.getY() < 140) {
        		ShiftCube.sr.begin(ShapeType.Filled);
        		ShiftCube.sr.setColor(1f, 1f, 1f, .35f);
        		ShiftCube.sr.triangle(1210, 100, 1210, 140, 1230, 120);
	            if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
	            	ShiftCube.sr.setColor(1f, 1f, 1f, .35f);
	            	ShiftCube.sr.triangle(1210, 100, 1210, 140, 1230, 120);
	                if (ShiftCube.left()) {
	                	ColorVariant old = current;
	                	current = ColorVariant.values()[current.ordinal() + 1];
	                	color(current, old);
	                }
	        	}
	            ShiftCube.sr.end();
        	}
        } else {
        	ShiftCube.sr.begin(ShapeType.Filled);
        	ShiftCube.sr.setColor(1f, 0f, 0f, .5f);
        	ShiftCube.sr.triangle(1210, 100, 1210, 140, 1230, 120);
        	ShiftCube.sr.end();
        }
        Gdx.gl20.glDisable(GL20.GL_BLEND);
		
		ShiftCube.batch.begin();
		String s = current.toString();
		String n = "" + s.charAt(0);
		for (int i = 1; i < s.length(); i++) {
			n += Character.toLowerCase(s.charAt(i));
		}
		ShiftCube.font.draw(ShiftCube.batch, n, 1060, 135);
		ShiftCube.font.draw(ShiftCube.batch, "Color Theme:", 1015, 180);
		ShiftCube.batch.end();
	}
	
	public void color(ColorVariant colorNew, ColorVariant colorOld) {
		for (Cube[][] positions : ShiftCube.positions) {
			for (Cube[] cubes : positions) {
				for (Cube cube : cubes) {
					if (cube.color.equals(colorOld.correct)) {
						for (ModelInstance instance : cube.instances) {
							instance.materials.get(0).set(ColorAttribute.createDiffuse(colorNew.correct));
						}
						cube.color = colorNew.correct;
					} else if (cube.color.equals(colorOld.wrong)) {
						for (ModelInstance instance : cube.instances) {
							instance.materials.get(0).set(ColorAttribute.createDiffuse(colorNew.wrong));
						}
						cube.color = colorNew.wrong;
					}
				}
			}
		}
		for (ModelInstance instance : ShiftCube.coreInstances) {
			instance.materials.get(0).set(ColorAttribute.createDiffuse(colorNew.core));
		}
	}

}
