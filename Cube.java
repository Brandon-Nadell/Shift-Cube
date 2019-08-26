package com.mygdx.game;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class Cube {
	
	public ArrayList<ModelInstance> instances;
	public int n;
	public Color color;
	float sqrt2 = (float)Math.sqrt(2);
	public static final float LIFT = .1f;
	
	public Cube(ModelBuilder modelBuilder, int i, int j, int k, int n) {
		instances = new ArrayList<ModelInstance>();
		float size = 4.5f;
		float size2 = size - .5f;
		Material mat = new Material(TextureAttribute.createDiffuse(new TextureRegion(ShiftCube.numbers, 320*(n-1), 0, 320, 320)));
		instances.add(new ModelInstance(ShiftCube.assets.get("cube.g3db", Model.class), (i-1)*5, (k-1)*5, (j-1)*5));
		instances.get(0).transform.scl(size/4.5f);
		instances.add(new ModelInstance(modelBuilder.createBox(
				size2, LIFT, size2, mat, 
				Usage.Position | Usage.Normal | Usage.TextureCoordinates), (i-1)*5, (k-1)*5 + 0 + size/2f, (j-1)*5));
		instances.add(new ModelInstance(modelBuilder.createBox(
				size2, LIFT, size2, mat, 
				Usage.Position | Usage.Normal | Usage.TextureCoordinates), (i-1)*5, (k-1)*5 - size/2f, (j-1)*5));
		instances.add(new ModelInstance(modelBuilder.createBox(
				LIFT, size2, size2, mat, 
				Usage.Position | Usage.Normal | Usage.TextureCoordinates), (i-1)*5 + size/2f, (k-1)*5, (j-1)*5));
		instances.add(new ModelInstance(modelBuilder.createBox(
				LIFT, size2, size2, mat, 
				Usage.Position | Usage.Normal | Usage.TextureCoordinates), (i-1)*5 - size/2f, (k-1)*5, (j-1)*5));
		instances.add(new ModelInstance(modelBuilder.createBox(
				size2, size2, LIFT, mat, 
				Usage.Position | Usage.Normal | Usage.TextureCoordinates), (i-1)*5, (k-1)*5, (j-1)*5 + size/2f));
		instances.add(new ModelInstance(modelBuilder.createBox(
				size2, size2, LIFT, mat, 
				Usage.Position | Usage.Normal | Usage.TextureCoordinates), (i-1)*5, (k-1)*5, (j-1)*5 - size/2f));
		for (int a = 1; a < instances.size(); a++)
			instances.get(a).materials.get(0).set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
		instances.get(0).materials.get(0).set(ColorAttribute.createDiffuse(ColorPicker.current.correct));
		ShiftCube.instances.addAll(instances);
		this.n = n;
		color = ColorPicker.current.correct;
	}

}
