package com.mygdx.game;

//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;

public class ShiftCube extends ApplicationAdapter {

	public PerspectiveCamera cam;
	public CameraInputController camController;
	public ModelBatch modelBatch;
	public Environment environment;
	public static ArrayList<ModelInstance> instances = new ArrayList<ModelInstance>();
	public static ArrayList<ModelInstance> coreInstances = new ArrayList<ModelInstance>();
	public static Cube[][][] positions = new Cube[3][3][3];
	public static int[][][] solve = new int[3][3][3];
	static Texture numbers;
	static Texture numbers2;
	Texture logo;
	ArrayList<Animation> animations = new ArrayList<Animation>();
	ArrayList<Move> moves = new ArrayList<Move>();
	static boolean started;
	boolean startTimer;
	ArrayList<Move> correctMoves = new ArrayList<Move>();
	Vector3 pos2;
	static SpriteBatch batch;
	static ShapeRenderer sr;
	static BitmapFont font;
	int[][][] orients;

	public Pos[] points = { new Pos(0, 0), new Pos(0, 1), new Pos(0, 2), new Pos(1, 2), new Pos(2, 2), new Pos(2, 1),
			new Pos(2, 0), new Pos(1, 0), };
	public Pos[] pointsReverse = { new Pos(0, 0), new Pos(1, 0), new Pos(2, 0), new Pos(2, 1), new Pos(2, 2),
			new Pos(1, 2), new Pos(0, 2), new Pos(0, 1), };
	public Pos[] vectors = { new Pos(0, 1), new Pos(0, 1), new Pos(1, 0), new Pos(1, 0), new Pos(0, -1), new Pos(0, -1),
			new Pos(-1, 0), new Pos(-1, 0), };
	public Pos[] vectorsReverse = { new Pos(1, 0), new Pos(1, 0), new Pos(0, 1), new Pos(0, 1), new Pos(-1, 0),
			new Pos(-1, 0), new Pos(0, -1), new Pos(0, -1), };

	int moveCooldown = 0;
	boolean canShift;
	float sqrt2 = (float) Math.sqrt(2);
	static boolean left;
	boolean solved;
	int timer;
	int record;
	int moveCount;
	int recordMoves;
	boolean shuffled;
	ColorPicker colorpicker;

	public void reset() {
		timer = 0;
		moveCount = 0;
		solved = false;
		canShift = true;
		moveCooldown = 0;
		moves.clear();
		correctMoves.clear();
		startTimer = false;
		started = false;
	}

	public static AssetManager assets;

	@Override
	public void create() {
		Gdx.gl.glClearColor(.75f, .75f, .75f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		modelBatch = new ModelBatch();
		numbers = new Texture("numbers.png");
		batch = new SpriteBatch();
		sr = new ShapeRenderer();
		assets = new AssetManager();
		assets.load("cube.g3db", Model.class);
		assets.finishLoading();
		font = new BitmapFont(Gdx.files.internal("font.fnt"));
		logo = new Texture("logo.png");
		colorpicker = new ColorPicker();

		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.up.set(0, 1, 0);
		cam.position.set(-25f, 25f, 25f);
		cam.lookAt(0, 0, 0);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();

		camController = new CameraInputController(cam);
		Gdx.input.setInputProcessor(camController);
		camController.forwardKey = -1;
		camController.backwardKey = -1;
		camController.rotateRightKey = -1;
		camController.rotateLeftKey = -1;
		camController.translateButton = -1;
		camController.forwardButton = -1;
		camController.autoUpdate = false;

		ModelBuilder modelBuilder = new ModelBuilder();

		createCore(modelBuilder, 4f);// 4.83333 max
		int n = 1;
		for (int k = 2; k >= 0; k--) // y
			for (int i = 2; i >= 0; i--) // z
				for (int j = 0; j < 3; j++) // x
				{
					create(modelBuilder, i, j, k, n);
					solve[i][j][k] = n;
					n++;
				}
//		setRecord();
		initializeHighlights(modelBuilder);
		initializeOrientations();
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, new Color(.4f, .4f, .4f, 1f)));
		float l = .85f;
		Vector3 v = new Vector3(1f, 1f, -.66f);
		environment.add(new DirectionalLight().set(l, l, l, v));
		environment.add(new DirectionalLight().set(l, l, l, v.scl(-1)));
		// instances.add(new ModelInstance(modelBuilder.createBox(1, 1, 1, new
		// Material(ColorAttribute.createDiffuse(Color.BLACK)), Usage.Normal |
		// Usage.Position), new Vector3(v).scl(-30)));
		// instances.add(new ModelInstance(modelBuilder.createBox(1, 1, 1, new
		// Material(ColorAttribute.createDiffuse(Color.BLACK)), Usage.Normal |
		// Usage.Position), new Vector3(v).scl(30)));

	}

	public void shuffle(int shuffles) {
		Animation.DURATION_MAX = 4;
		for (int i = 0; i < shuffles; i++) {
			moves.add(new Move(Moves.values()[(int) (Math.random() * Moves.values().length)].move,
					Math.random() > .5 ? true : false));
		}
		correctMoves.addAll(moves);
		Collections.reverse(correctMoves);
		for (int i = 0; i < correctMoves.size(); i++) {
			correctMoves.set(i, new Move(correctMoves.get(i), !correctMoves.get(i).inverse));
		}
		moves.add(new Move(-1, -1, false));
		shuffled = true;
	}

	public enum Moves {
		F(Keys.F, new Move(0, 0, false)), B(Keys.B, new Move(0, 2, false)), D(Keys.D, new Move(1, 0, false)), U(Keys.U,
				new Move(1, 2, false)), L(Keys.L, new Move(2, 0, false)), R(Keys.R, new Move(2, 2, false));

		public int key;
		public final Move move;
		public ModelInstance highlight;

		private Moves(int key, Move move) {
			this.key = key;
			this.move = move;
		}

		public void execute(ShiftCube game, boolean inverse) {
			game.moves.add(new Move(move, inverse));
		}
	}

	@Override
	public void render() {
		setOrientation();
		boundZoom();
		camController.update();
		cam.update();

		if (!solved) {
			boolean inverse = Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT);
			for (Moves move : Moves.values()) {
				if (Gdx.input.isKeyJustPressed(move.key)) {
					move.execute(this, inverse);
				}
			}
			if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
				moves.addAll(correctMoves);
			}
		}
		if (!moves.isEmpty() && canShift && !solved) {
			Move move = moves.remove(0);
			if (!move.isEvent()) {
				if (started) {
					moveCount++;
					if (!startTimer) {
						startTimer = true;
					}
				}
				move(move.axis, move.layer, move.inverse);
			}
			canShift = false;
			moveCooldown = Animation.DURATION_MAX;
		}
		if (moveCooldown > 0) {
			moveCooldown--;
		} else {
			canShift = true;
		}
		for (int i = 0; i < animations.size(); i++) {
			animations.get(i).render();
			if (animations.get(i).duration == 0) {
				animations.remove(i);
				i--;
			}
		}

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		modelBatch.begin(cam);
		modelBatch.render(instances, environment);
		if (!solved) {
			// drawHighlight(new ModelBuilder());
		}
		modelBatch.end();

		if (startTimer && !solved) {
			timer++;
		}
		drawSidebar();
	}

	public void drawSidebar() {
		if (!Gdx.input.isButtonPressed(Buttons.LEFT) && left) {
			left = false;
		}

		sr.setColor(Color.BLACK);
		sr.begin(ShapeType.Filled);
		sr.rect(0, 0, 250, 750);
		sr.rect(1000, 0, 250, 750);
		sr.end();
		// left
		batch.begin();
		batch.draw(logo, 0, 510, 250, 250);
		int y = 500;
		font.draw(batch, "Controls:", 15, y);
		font.draw(batch, "Top -> U", 15, y - 50);
		font.draw(batch, "Bottom -> D", 15, y - 90);
		font.draw(batch, "Front -> F", 15, y - 130);
		font.draw(batch, "Back -> B", 15, y - 170);
		font.draw(batch, "Right -> R", 15, y - 210);
		font.draw(batch, "Left -> L", 15, y - 250);
		font.draw(batch, "Rev. -> Shift", 15, y - 290);
		batch.end();

		// right
		sr.begin(ShapeType.Filled);
		sr.setColor(Color.WHITE);
		sr.rect(1020, 680, 210, 40);
		sr.rect(1020 + 2, 680 + 2, 210 - 4, 40 - 4, Color.ROYAL, Color.SKY, Color.SKY, Color.ROYAL);
		sr.end();
		Gdx.gl20.glEnable(GL20.GL_BLEND);
		if ((!started && !shuffled) || solved) {
			if (Gdx.input.getX() > 1020 && Gdx.input.getX() < 1020 + 210 && 750 - Gdx.input.getY() > 680
					&& 750 - Gdx.input.getY() < 680 + 40) {
				sr.begin(ShapeType.Filled);
				sr.setColor(1f, 1f, 1f, .35f);
				sr.rect(1020, 680, 210, 40);
				if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
					sr.setColor(1f, 1f, 1f, .35f);
					sr.rect(1020, 680, 210, 40);
					if (left()) {
						reset();
						shuffle(25);
					}
				}
				sr.end();
			}
		} else {
			sr.begin(ShapeType.Filled);
			sr.setColor(1f, 0f, 0f, .5f);
			sr.rect(1020 + 2, 680 + 2, 210 - 4, 40 - 4);
			sr.end();
		}
		Gdx.gl20.glDisable(GL20.GL_BLEND);
		batch.begin();
		font.draw(batch, "Shuffle", 1060, 715);
		if (solved) {
			font.draw(batch, "Solved!", 1060, 660);
		}
		font.getData().setScale(1f);
		font.draw(batch, "Moves/Time:", 1015, 610);
		font.draw(batch, "   " + String.valueOf(moveCount), 1015, 570);
		font.draw(batch, "   " + ticksToClock(timer), 1015, 530);
		font.draw(batch, "Record:", 1015, 460);
		if (record != -1) {
			font.draw(batch, "   " + String.valueOf(recordMoves), 1015, 420);
			font.draw(batch, "   " + ticksToClock(record), 1015, 380);
		} else {
			font.draw(batch, "   -", 1015, 420);
			font.draw(batch, "   -:--.--", 1015, 380);
		}
		batch.end();
		colorpicker.draw();

		if (Gdx.input.isButtonPressed(Buttons.LEFT) && !left) {
			left = true;
		}
	}

//	public void setNewRecord() {
//		boolean changed = false;
//		boolean first = record == -1;
//		if (timer < record || first) {
//			record = timer;
//			changed = true;
//		}
//		if (moveCount < recordMoves || first) {
//			recordMoves = moveCount;
//			changed = true;
//		}
//		if (changed) {
//			try {
//				java.io.BufferedWriter bw;
//				bw = new java.io.BufferedWriter(new java.io.FileWriter(new java.io.File("records.txt")));
//				bw.write(String.valueOf(record) + "\n" + String.valueOf(recordMoves));
//				bw.close();
//			} catch (IOException e) {
//			}
//		}
//	}
//
//	public void setRecord() {
//		java.io.File file = new java.io.File("records.txt");
//		if (file.exists()) {
//			try {
//				java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file));
//				record = Integer.valueOf(br.readLine());
//				recordMoves = Integer.valueOf(br.readLine());
//				br.close();
//			} catch (IOException e) {
//			} catch (NumberFormatException e) {
//			}
//		} else {
//			try {
//				java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(file));
//				bw.write("-1");
//				bw.close();
//				record = -1;
//				recordMoves = -1;
//			} catch (IOException e) {
//			}
//		}
//	}

	public void setOrientation() {
		Vector3 pos = cam.position;
		if (Math.abs(pos.x) > Math.abs(pos.y) && Math.abs(pos.x) > Math.abs(pos.z)) {
			int j = (int) (((getCameraRotation(cam.up.y, cam.up.z) + 315) % 360) / 90);
			// int[][] k = new int[4][4];
			int k;
			if (pos.x < 0) {
				// initialize(k, new int[] { Keys.U, Keys.R, Keys.D, Keys.L });
				k = 0;
				Moves.F.key = Keys.F;
				Moves.B.key = Keys.B;
			} else {
				// initialize(k, new int[] { Keys.U, Keys.L, Keys.D, Keys.R });
				k = 1;
				Moves.B.key = Keys.F;
				Moves.F.key = Keys.B;
			}
			Moves.U.key = orients[k][j][0];
			Moves.R.key = orients[k][j][1];
			Moves.D.key = orients[k][j][2];
			Moves.L.key = orients[k][j][3];
		} else if (Math.abs(pos.z) > Math.abs(pos.x) && Math.abs(pos.z) > Math.abs(pos.y)) {
			int j = (int) (((getCameraRotation(cam.up.x, cam.up.y) + 315) % 360) / 90);
			// int[][] k = new int[4][4];
			int k;
			if (pos.z < 0) {
				// initialize(k, new int[] { Keys.R, Keys.D, Keys.L, Keys.U });
				k = 2;
				Moves.L.key = Keys.F;
				Moves.R.key = Keys.B;
			} else {
				// initialize(k, new int[] { Keys.L, Keys.D, Keys.R, Keys.U });
				k = 3;
				Moves.R.key = Keys.F;
				Moves.L.key = Keys.B;
			}
			Moves.U.key = orients[k][j][0];
			Moves.F.key = orients[k][j][1];
			Moves.D.key = orients[k][j][2];
			Moves.B.key = orients[k][j][3];
		} else if (Math.abs(pos.y) > Math.abs(pos.x) && Math.abs(pos.y) > Math.abs(pos.z)) {
			int j = (int) (((getCameraRotation(cam.up.x, cam.up.z) + 315) % 360) / 90);
			// int[][] k = new int[4][4];
			int k;
			if (pos.y < 0) {
				// initialize(k, new int[] { Keys.U, Keys.L, Keys.D, Keys.R });
				k = 4;
				Moves.D.key = Keys.F;
				Moves.U.key = Keys.B;
			} else {
				// initialize(k, new int[] { Keys.U, Keys.R, Keys.D, Keys.L });
				k = 5;
				Moves.U.key = Keys.F;
				Moves.D.key = Keys.B;
			}
			Moves.B.key = orients[k][j][0];
			Moves.R.key = orients[k][j][1];
			Moves.F.key = orients[k][j][2];
			Moves.L.key = orients[k][j][3];
		}
	}

	public void initialize(int[][] k, int[] tmp) {
		for (int i = 0; i < 4; i++) {
			k[i] = Arrays.copyOf(tmp, tmp.length);
			int t = tmp[tmp.length - 1];
			for (int a = tmp.length - 1; a > 0; a--) {
				tmp[a] = tmp[a - 1];
			}
			tmp[0] = t;
		}
	}

	public void move(int axis, int a, boolean inverse) {
		if (axis == 0) { // front and back
			Pos[] vectors = (a == 0) == !inverse ? this.vectors : this.vectorsReverse;
			Pos[] points = (a == 0) == !inverse ? this.points : this.pointsReverse;

			for (int i = 0; i < points.length; i++) {
				animations.add(new Animation(positions[a][points[i].a][points[i].b],
						new Vector3(0, vectors[i].b, vectors[i].a)));
			}

			Cube temp = positions[a][0][0];
			for (int i = 1; i < points.length; i++) {
				positions[a][points[i - 1].b][points[i - 1].a] = positions[a][points[i].b][points[i].a];
			}
			positions[a][points[points.length - 1].b][points[points.length - 1].a] = temp;
		} else if (axis == 1) { // up and down
			Pos[] vectors = (a == 0) == !inverse ? this.vectorsReverse : this.vectors;
			Pos[] points = (a == 0) == !inverse ? this.points : this.pointsReverse;

			for (int i = 0; i < points.length; i++) {
				animations.add(new Animation(positions[points[i].a][points[i].b][a],
						new Vector3(vectors[i].b, 0, vectors[i].a)));
			}

			Cube temp = positions[0][0][a];
			for (int i = 1; i < points.length; i++) {
				positions[points[i - 1].b][points[i - 1].a][a] = positions[points[i].b][points[i].a][a];
			}
			positions[points[points.length - 1].b][points[points.length - 1].a][a] = temp;
		} else if (axis == 2) { // right and left
			Pos[] vectors = (a == 0) == !inverse ? this.vectors : this.vectorsReverse;
			Pos[] points = (a == 0) == !inverse ? this.pointsReverse : this.points;

			for (int i = 0; i < points.length; i++) {
				animations.add(new Animation(positions[points[i].a][a][points[i].b],
						new Vector3(vectors[i].b, vectors[i].a, 0)));
			}

			Cube temp = positions[0][a][0];
			for (int i = 1; i < points.length; i++) {
				positions[points[i - 1].b][a][points[i - 1].a] = positions[points[i].b][a][points[i].a];
			}
			positions[points[points.length - 1].b][a][points[points.length - 1].a] = temp;
		}
		if (started && !solved) {
			solved = checkIfSolved();
			if (solved) {
				shuffled = false;
//				setNewRecord();
			}
		}
	}

	public void boundZoom() {
		if (cam.position.len() > 100) {
			cam.position.setLength(100);
		}
		if (cam.position.len() < 15) {
			cam.position.setLength(15);
			cam.lookAt(0, 0, 0);
		}
	}

	public String ticksToClock(int ticks) {
		int seconds = ticks / 60;
		int minutes = seconds / 60;
		seconds %= 60;
		int centiseconds = (int) ((ticks % 60) / 60.0 * 100);
		return minutes + ":" + (seconds < 10 ? "0" : "") + seconds + "." + centiseconds
				+ (centiseconds < 10 ? "0" : "");
	}

	public void create(ModelBuilder modelBuilder, int i, int j, int k, int n) {
		positions[i][j][k] = new Cube(modelBuilder, i, j, k, n);
	}

	public void createCore(ModelBuilder modelBuilder, float radius) {
		float r = radius;
		float r2 = 2f * radius;
		float rHalf = r / 2;
		Material core = new Material(ColorAttribute.createDiffuse(ColorPicker.current.core));
		coreInstances
				.add(new ModelInstance(modelBuilder.createBox(r, r, r, core, Usage.Position | Usage.Normal), 0, 0, 0));
		coreInstances
				.add(new ModelInstance(modelBuilder.createBox(r, r, r, core, Usage.Position | Usage.Normal), -r, 0, 0));
		coreInstances
				.add(new ModelInstance(modelBuilder.createBox(r, r, r, core, Usage.Position | Usage.Normal), r, 0, 0));
		coreInstances
				.add(new ModelInstance(modelBuilder.createBox(r, r, r, core, Usage.Position | Usage.Normal), 0, -r, 0));
		coreInstances
				.add(new ModelInstance(modelBuilder.createBox(r, r, r, core, Usage.Position | Usage.Normal), 0, r, 0));
		coreInstances
				.add(new ModelInstance(modelBuilder.createBox(r, r, r, core, Usage.Position | Usage.Normal), 0, 0, -r));
		coreInstances
				.add(new ModelInstance(modelBuilder.createBox(r, r, r, core, Usage.Position | Usage.Normal), 0, 0, r));

		coreInstances.add(new ModelInstance(
				modelBuilder.createCylinder(r2, r, r2, 50, core, Usage.Position | Usage.Normal), rHalf, 0, rHalf));
		coreInstances.add(new ModelInstance(
				modelBuilder.createCylinder(r2, r, r2, 50, core, Usage.Position | Usage.Normal), -rHalf, 0, -rHalf));
		coreInstances.add(new ModelInstance(
				modelBuilder.createCylinder(r2, r, r2, 50, core, Usage.Position | Usage.Normal), rHalf, 0, -rHalf));
		coreInstances.add(new ModelInstance(
				modelBuilder.createCylinder(r2, r, r2, 50, core, Usage.Position | Usage.Normal), -rHalf, 0, rHalf));

		ModelInstance inst = new ModelInstance(
				modelBuilder.createCylinder(r2, r, r2, 50, core, Usage.Position | Usage.Normal), rHalf, rHalf, 0);
		inst.transform.rotate(1, 0, 0, 90);
		coreInstances.add(inst);
		inst = new ModelInstance(modelBuilder.createCylinder(r2, r, r2, 50, core, Usage.Position | Usage.Normal),
				-rHalf, -rHalf, 0);
		inst.transform.rotate(1, 0, 0, 90);
		coreInstances.add(inst);
		inst = new ModelInstance(modelBuilder.createCylinder(r2, r, r2, 50, core, Usage.Position | Usage.Normal), rHalf,
				-rHalf, 0);
		inst.transform.rotate(1, 0, 0, 90);
		coreInstances.add(inst);
		inst = new ModelInstance(modelBuilder.createCylinder(r2, r, r2, 50, core, Usage.Position | Usage.Normal),
				-rHalf, rHalf, 0);
		inst.transform.rotate(1, 0, 0, 90);
		coreInstances.add(inst);

		inst = new ModelInstance(modelBuilder.createCylinder(r2, r, r2, 50, core, Usage.Position | Usage.Normal), 0,
				rHalf, rHalf);
		inst.transform.rotate(0, 0, 1, 90);
		coreInstances.add(inst);
		inst = new ModelInstance(modelBuilder.createCylinder(r2, r, r2, 50, core, Usage.Position | Usage.Normal), 0,
				-rHalf, -rHalf);
		inst.transform.rotate(0, 0, 1, 90);
		coreInstances.add(inst);
		inst = new ModelInstance(modelBuilder.createCylinder(r2, r, r2, 50, core, Usage.Position | Usage.Normal), 0,
				-rHalf, rHalf);
		inst.transform.rotate(0, 0, 1, 90);
		coreInstances.add(inst);
		inst = new ModelInstance(modelBuilder.createCylinder(r2, r, r2, 50, core, Usage.Position | Usage.Normal), 0,
				rHalf, -rHalf);
		inst.transform.rotate(0, 0, 1, 90);
		coreInstances.add(inst);

		coreInstances.add(
				new ModelInstance(modelBuilder.createSphere(r2, r2, r2, 30, 30, core, Usage.Position | Usage.Normal),
						rHalf, rHalf, rHalf));
		coreInstances.add(
				new ModelInstance(modelBuilder.createSphere(r2, r2, r2, 30, 30, core, Usage.Position | Usage.Normal),
						-rHalf, rHalf, rHalf));
		coreInstances.add(
				new ModelInstance(modelBuilder.createSphere(r2, r2, r2, 30, 30, core, Usage.Position | Usage.Normal),
						rHalf, -rHalf, rHalf));
		coreInstances.add(
				new ModelInstance(modelBuilder.createSphere(r2, r2, r2, 30, 30, core, Usage.Position | Usage.Normal),
						rHalf, rHalf, -rHalf));
		coreInstances.add(
				new ModelInstance(modelBuilder.createSphere(r2, r2, r2, 30, 30, core, Usage.Position | Usage.Normal),
						-rHalf, -rHalf, rHalf));
		coreInstances.add(
				new ModelInstance(modelBuilder.createSphere(r2, r2, r2, 30, 30, core, Usage.Position | Usage.Normal),
						rHalf, -rHalf, -rHalf));
		coreInstances.add(
				new ModelInstance(modelBuilder.createSphere(r2, r2, r2, 30, 30, core, Usage.Position | Usage.Normal),
						-rHalf, rHalf, -rHalf));
		coreInstances.add(
				new ModelInstance(modelBuilder.createSphere(r2, r2, r2, 30, 30, core, Usage.Position | Usage.Normal),
						-rHalf, -rHalf, -rHalf));

		instances.addAll(coreInstances);
	}

	public void initializeHighlights(ModelBuilder modelBuilder) {
		Material mat = new Material(ColorAttribute.createDiffuse(Color.WHITE));
		mat.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, .25f));
		float l = .01f;
		float d = 7.26f;// 7.26
		Moves.values()[0].highlight = new ModelInstance(
				modelBuilder.createBox(l, 14.5f, 14.5f, mat, Usage.Position | Usage.Normal), -d, 0, 0);
		Moves.values()[1].highlight = new ModelInstance(
				modelBuilder.createBox(l, 14.5f, 14.5f, mat, Usage.Position | Usage.Normal), d, 0, 0);
		Moves.values()[2].highlight = new ModelInstance(
				modelBuilder.createBox(14.5f, l, 14.5f, mat, Usage.Position | Usage.Normal), 0, -d, 0);
		Moves.values()[3].highlight = new ModelInstance(
				modelBuilder.createBox(14.5f, l, 14.5f, mat, Usage.Position | Usage.Normal), 0, d, 0);
		Moves.values()[4].highlight = new ModelInstance(
				modelBuilder.createBox(14.5f, 14.5f, l, mat, Usage.Position | Usage.Normal), 0, 0, -d);
		Moves.values()[5].highlight = new ModelInstance(
				modelBuilder.createBox(14.5f, 14.5f, l, mat, Usage.Position | Usage.Normal), 0, 0, d);
	}

	public void initializeOrientations() {
		orients = new int[6][4][4];
		initialize(orients[0], new int[] { Keys.U, Keys.R, Keys.D, Keys.L });
		initialize(orients[1], new int[] { Keys.U, Keys.L, Keys.D, Keys.R });
		initialize(orients[2], new int[] { Keys.R, Keys.D, Keys.L, Keys.U });
		initialize(orients[3], new int[] { Keys.L, Keys.D, Keys.R, Keys.U });
		initialize(orients[4], new int[] { Keys.U, Keys.L, Keys.D, Keys.R });
		initialize(orients[5], new int[] { Keys.U, Keys.R, Keys.D, Keys.L });
	}

	public void drawHighlight(ModelBuilder modelBuilder) {
		if (Math.abs(Gdx.input.getX() - Gdx.graphics.getWidth() / 2) < 5000 / cam.position.len()
				&& Math.abs(Gdx.input.getY() - Gdx.graphics.getHeight() / 2) < 5000 / cam.position.len()) {
			for (Moves move : Moves.values()) {
				if (move.key == Keys.F) {
					modelBatch.render(move.highlight, environment);
				}
			}
		}
	}

	public boolean checkIfSolved() {
		for (int k = 2; k >= 0; k--) // y
			for (int i = 2; i >= 0; i--) // z
				for (int j = 0; j < 3; j++) // x
					if (positions[i][j][k].n != solve[i][j][k]) {
						return false;
					}
		return true;
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
		instances.clear();
		assets.dispose();
	}

	public float getCameraRotation(float a, float b) {
		return -(float) Math.toDegrees(Math.atan2(a, b)) + 180;
	}

	public static boolean left() {
		return Gdx.input.isButtonPressed(Buttons.LEFT) && !left;
	}
}
