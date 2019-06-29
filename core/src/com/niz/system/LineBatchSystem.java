package com.niz.system;

import java.util.Iterator;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.RenderSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.LineBatchNiz;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Bresenham2;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.niz.Data;
import com.niz.GameInstance;
import com.niz.Main;
import com.niz.action.ActionList;
import com.niz.actions.path.AFollowPath;
import com.niz.astar.PathNode;
import com.niz.component.BlockLine;
import com.niz.component.BlockOutline;
import com.niz.component.Body;
import com.niz.component.DragBlock;
import com.niz.component.DragOption;
import com.niz.component.Health;
import com.niz.component.Light;
import com.niz.component.LineBody;
import com.niz.component.PickUp;
import com.niz.component.Player;
import com.niz.component.Position;
import com.niz.component.RoomDefinition;
import com.niz.component.SpriteAnimation;
import com.niz.component.SpriteStatic;

public class LineBatchSystem extends RenderSystem {
private static final String TAG = "linebatch";
private static final Color PLACE_LINE_COLOR = new Color(1f, 1f, 1f, 1f);
private static ComponentMapper<DragOption> dragoM = ComponentMapper.getFor(DragOption.class);

public LineBatchNiz batch;
private CameraSystem camSys;
private Family family;
ComponentMapper<LineBody> lineM = ComponentMapper.getFor(LineBody.class);
Bresenham2 ray = new Bresenham2();
Vector2 v = new Vector2(), v2 = new Vector2();

public ComponentMapper<Position> posM = ComponentMapper.getFor(Position.class);
public ComponentMapper<SpriteAnimation> spriteM = ComponentMapper.getFor(SpriteAnimation.class);
public ComponentMapper<Health> healthM = ComponentMapper.getFor(Health.class);
public ComponentMapper<SpriteStatic> spriteStaticM = ComponentMapper.getFor(SpriteStatic.class);
protected ComponentMapper<Body> bodyM = ComponentMapper.getFor(Body.class);
protected ComponentMapper<BlockLine> blockLineM = ComponentMapper.getFor(BlockLine.class);

private LightUpdateSystem lights;
private ShaderProgram shader;
private ImmutableArray<Entity> lineBodyEntities;
private ImmutableArray<Entity> pickUpEntities;
private ImmutableArray<Entity> blockOutlineEntities;
private ImmutableArray<Entity> blockLineEntities;

private static float 
af = .1f,
bf = .2f,
cf = .3f,
df = .4f,
ef = .5f,
ff = .6f,
gf = .7f,
hf = .8f,
iif = .9f;
float phase;
Color[] fadeColor = {
		/*new Color(1f, 1f, 1f, 0f),
		new Color(1f, 1f, 1f, af),
		new Color(1f, 1f, 1f, bf),
		new Color(1f, 1f, 1f, cf),
		new Color(1f, 1f, 1f, df),
		new Color(1f, 1f, 1f, ef),
		new Color(1f, 1f, 1f, ff),
		new Color(1f, 1f, 1f, gf),
		new Color(1f, 1f, 1f, hf),
		new Color(1f, 1f, 1f, iif),
		new Color(1f, 1f, 1f, 1f),
		new Color(1f, 1f, 1f, hf),
		new Color(1f, 1f, 1f, gf),
		new Color(1f, 1f, 1f, ff),
		new Color(1f, 1f, 1f, ef),
		new Color(1f, 1f, 1f, df),
		new Color(1f, 1f, 1f, cf),
		new Color(1f, 1f, 1f, bf),
		new Color(1f, 1f, 1f, af),
		new Color(1f, 1f, 1f, 0f),
		new Color(1f, 1f, 1f, 0f),
		new Color(1f, 1f, 1f, 0f),
		new Color(1f, 1f, 1f, 0f),
		new Color(1f, 1f, 1f, 0f),
		new Color(1f, 1f, 1f, 0f),
		new Color(1f, 1f, 1f, 0f),
		new Color(1f, 1f, 1f, 0f),
		new Color(1f, 1f, 1f, 0f),
		new Color(1f, 1f, 1f, 0f),
		new Color(1f, 1f, 1f, 0f),
		new Color(1f, 1f, 1f, 0f),
		new Color(1f, 1f, 1f, 0f),*/
		Data.colors[Data.DARK_GREY_INDEX],
		Data.colors[Data.MEDIUM_GREY_INDEX],
		Data.colors[Data.LIGHT_GREY_INDEX],
		Data.colors[Data.MEDIUM_GREY_INDEX],
		Data.colors[Data.DARK_GREY_INDEX],
		new Color(1f, 1f, 1f, 0f),
		new Color(1f, 1f, 1f, 0f),
		new Color(1f, 1f, 1f, 0f),
		
		
};
private ImmutableArray<Entity> allBodyEntities;
private ImmutableArray<Entity> dragOptionEntities;
private ImmutableArray<Entity> dragBlockEntities;
private ImmutableArray<Entity> roomDefEntities;
private ImmutableArray<Entity> playerEntities;
	private ImmutableArray<Entity> healthEntities;

/*
 * new Color(0f, 0f, 0f, 1f),
		new Color(af, af, af, 1f),
		new Color(bf, bf, bf, 1f),
		new Color(cf, cf, cf, 1f),
		new Color(df, df, df, 1f),
		new Color(ef, ef, ef, 1f),
		new Color(ff, ff, ff, 1f),
		new Color(gf, gf, gf, 1f),
		new Color(hf, hf, hf, 1f),
		new Color(iif, iif, iif, 1f)
 */

public LineBatchSystem(LightUpdateSystem lights) {
	this.lights = lights;
}

@Override
public void addedToEngine(Engine engine) {
	ShaderSystem shaderSys = engine.getSystem(ShaderSystem.class);
	shader = shaderSys.shader;
	batch = new LineBatchNiz(1000, null);
	batch.setColor(Color.GREEN);
	super.addedToEngine(engine);
	camSys = engine.getSystem(CameraSystem.class);
	family = Family.all(Body.class, Position.class, PickUp.class).get();
	healthEntities = engine.getEntitiesFor(Family.all(Health.class, Body.class, Position.class).exclude(DragOption.class).get());
	pickUpEntities = engine.getEntitiesFor(family);
	blockOutlineEntities = engine.getEntitiesFor(Family.all(Position.class, BlockOutline.class).get());
	//spriteShader = shaderSys.spriteShader;
	//backShader = shaderSys.backShader;

	lineBodyEntities = engine.getEntitiesFor(Family.all(LineBody.class, Position.class).get());
	
	blockLineEntities = engine.getEntitiesFor(Family.all(BlockLine.class, Position.class).get());
	
	allBodyEntities = engine.getEntitiesFor(Family.all(Position.class, Body.class).get());
	
	dragOptionEntities = engine.getEntitiesFor(Family.all(Position.class, Body.class, DragOption.class).get());

	dragBlockEntities = engine.getEntitiesFor(Family.all(DragBlock.class, Position.class).get());

	roomDefEntities = engine.getEntitiesFor(Family.all(RoomDefinition.class).get());
	
	playerEntities = engine.getEntitiesFor(Family.all(Player.class).get());
}

@Override
public void removedFromEngine(Engine engine) {
	
	super.removedFromEngine(engine);
}
private Vector2 tmpV = new Vector2(), tmpV2 = new Vector2(), tmpV3 = new Vector2();


public void drawLast(float deltaTime) {
	//if (true) return;
	//batch.setColor(Color.CYAN);
	batch.setProjectionMatrix(camSys.camera.combined);
	//batch.clearCache();
	//batch.setShader(null);
	batch.begin();
	phase += deltaTime;
	//Gdx.app.log(TAG, "phase"+phase);
//	normal.bind(1);
//	diffuse.bind(0);
	//Gdx.gl.glEnable(GL20.GL_BLEND);
	Gdx.gl.glLineWidth(1f);
	
	for (int i = 0; i < pickUpEntities.size(); i++){
		Entity e = pickUpEntities.get(i);
		Body body = bodyM.get(e);
		Position pos = posM.get(e);
		float x =  (pos.pos.x * Main.PPM), y = (pos.pos.y * Main.PPM);
		int w = (int) (body.width*Main.PPM);
		int h = (int) (body.height * Main.PPM);
		//batch.drawLine(region, (int)x-(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
		//batch.drawLine(region, (int)x-(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f);
		//batch.drawLine(region, (int)x-(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x-(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
		//batch.drawLine(region, (int)x+(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
		int s = (int) (.36f * Main.PPM), sw = s;
		//w = Math.max(1, w);
		//h = Math.max(1, h);
		w += 1;
		h += 1;
		s = Math.min((int)w, s);
		sw = Math.min(h, sw);
		sw /= 3f;
		s /= 3f;
		//int sh = sw;
		//s = Math.max(2, s);
		//sw = Math.max(2, sw);
		//w = Main.PPM * 16;
		float space = .25f;
		long id = e.getId();
		float speed = 4f;
		//batch.end();
		batch.setColor(fadeColor[(int)((phase * speed +id + 0)% fadeColor.length)]);
		//Gdx.app.log(TAG, "sjfdl;k " + ((int)((phase * speed +id + 0)% fadeColor.length)));
		//batch.setColor(Color.CYAN);
		//batch.begin();
		//x-w, y+h, x-w+s, y+htl
		batch.drawLine((int)x- w +Main.PX/2f , (int)y+ h +Main.PX/2f, (int)x- w +Main.PX/2f + sw, (int)y+ h +Main.PX/2f);
		//x-w, y+h, x-w, y+h-s tl
		batch.drawLine((int)x- w +Main.PX/2f, (int)y+ h +Main.PX/2f, (int)x- w +Main.PX/2f, (int)y+ h +Main.PX/2f-s);
		
		batch.setColor(fadeColor[(int)((phase * speed +id + 2)% fadeColor.length)]);
		//x+w-s, y+h, x+w, y+h tr
		batch.drawLine( (int)x+ w +Main.PX/2f - sw, (int)y+ h +Main.PX/2f, (int)x+ w +Main.PX/2f, (int)y+ h +Main.PX/2f);
		//x+w, y+h, x+w, y+h-s tr
		batch.drawLine( (int)x+ w +Main.PX/2f, (int)y+ h +Main.PX/2f, (int)x+ w +Main.PX/2f, (int)y+ h +Main.PX/2f-s);
		
		batch.setColor(fadeColor[(int)((phase * speed +id + 4)% fadeColor.length)]);
		//x+w-s, y-h, x+w, y-h br
		batch.drawLine( (int)x+ w +Main.PX/2f - sw, (int)y- h +Main.PX/2f, (int)x+ w +Main.PX/2f, (int)y- h +Main.PX/2f);
		//x+w, y-h+s, x+w, y-h br
		batch.drawLine( (int)x+ w +Main.PX/2f, (int)y- h +Main.PX/2f+s, (int)x+ w +Main.PX/2f, (int)y- h +Main.PX/2f);
		
		batch.setColor(fadeColor[(int)((phase * speed +id + 6)% fadeColor.length)]);
		//x-w, y-h, x-w+s, y-h bl
		batch.drawLine( (int)x- w +Main.PX/2f , (int)y- h +Main.PX/2f, (int)x- w +Main.PX/2f + sw, (int)y- h +Main.PX/2f);
		//x-w, y-h+s, x-w, y-h bl
		batch.drawLine( (int)x- w +Main.PX/2f, (int)y- h +Main.PX/2f+s, (int)x- w +Main.PX/2f, (int)y- h +Main.PX/2f);

		/*if (batch.isFull()){
			batch.end();
			batch.beginDraw();
			lights.setUniforms(Light.CHARACTER_SPRITES_LAYER_LEFT, shader);
			batch.render();
			batch.end();
			batch.clearCache();
			batch.begin();
		}*/
		
	}
	batch.end();
	if (GameInstance.paused){
        Gdx.gl.glLineWidth(7f);
        batch.begin();
        batch.setColor(Data.colors[Data.DARK_GREYISH_BROWN_INDEX]);
        for (Entity e : healthEntities){
            Health health = healthM.get(e);
            Vector2 pos = posM.get(e).pos;
            Body body = bodyM.get(e);
            float alpha = 1f;//health.health / (float)(health.maxHealth);
            float w = 1f;
            //Gdx.app.log(TAG, "draw" + pos.x + " " + alpha);
            batch.drawLine(
                    (int)((pos.x-w/2) * Main.PPM)
                    , (int)((pos.y - body.height) * Main.PPM - 1)
                    , (int)((pos.x -w/2 + w * alpha) * Main.PPM)
                    , (int)((pos.y - body.height) * Main.PPM - 1)
            );
        }
        batch.end();
        Gdx.gl.glLineWidth(3f);
        batch.begin();

        batch.setColor(Data.colors[Data.RED_INDEX]);
        for (Entity e : healthEntities){
            Health health = healthM.get(e);
            Vector2 pos = posM.get(e).pos;
            Body body = bodyM.get(e);
            float alpha = health.health / (float)(health.maxHealth);
            float w = 1f;
            //Gdx.app.log(TAG, "draw" + pos.x + " " + alpha);
            batch.drawLine(
                    (int)((pos.x-w/2) * Main.PPM)
                    , (int)((pos.y - body.height) * Main.PPM - 1)
                    , (int)((pos.x -w/2 + w * alpha) * Main.PPM)
                    , (int)((pos.y - body.height) * Main.PPM - 1)
            );
        }
        batch.end();
    }
	Gdx.gl.glLineWidth(3f);
	batch.begin();
	batch.setColor(Color.WHITE);
	for (int i = 0; i < dragOptionEntities.size(); i++){
		Entity e = dragOptionEntities.get(i);
		DragOption drag = dragoM.get(e);
		if (!drag.selected)continue;
		//Body body = bodyM.get(e);
		Position pos = posM.get(e);
		//Gdx.app.log(TAG,  "render block outline "+pos.pos);
		float x =  (pos.pos.x * Main.PPM), y = (pos.pos.y * Main.PPM);
		float w = 1.5f * Main.PPM;
		float h = 1.5f * Main.PPM;
		
		batch.drawLine( (int)x-(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
		batch.drawLine( (int)x-(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f);
		batch.drawLine( (int)x-(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x-(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
		batch.drawLine( (int)x+(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
		
		//check(lights);
		x += drag.spacing * Main.PPM;
		batch.drawLine( (int)x-(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
		batch.drawLine( (int)x-(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f);
		batch.drawLine( (int)x-(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x-(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
		batch.drawLine( (int)x+(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
		
		//check(lights);
		x += -drag.spacing * 2 * Main.PPM;
		batch.drawLine( (int)x-(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
		batch.drawLine( (int)x-(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f);
		batch.drawLine( (int)x-(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x-(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
		batch.drawLine( (int)x+(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);

		//check(lights);
		
	}
	
	
	for (int i = 0; i < playerEntities.size(); i++){
		Entity e = playerEntities.get(i);
		//Body body = bodyM.get(e);
		Position pos = posM.get(e);
		//Gdx.app.log(TAG,  "render block outline "+pos.pos);
		float w = .5f * Main.PPM;
		float h = .5f * Main.PPM;
		AFollowPath act = e.getComponent(ActionList.class).getAction(AFollowPath.class);
		if (act == null) break;
		Iterator<Connection<PathNode>> it = act.path.path.iterator();
		while (it.hasNext()){
			Connection<PathNode> con = it.next();
			PathNode node = con.getFromNode();
			float x =  (node.x * Main.PPM), y = (node.y * Main.PPM);
			batch.drawLine( (int)x-(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
			batch.drawLine( (int)x-(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f);
			batch.drawLine( (int)x-(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x-(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
			batch.drawLine( (int)x+(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);

		
//		batch.drawLine(region, (int)x-(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
//		batch.drawLine(region, (int)x-(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f);
//		batch.drawLine(region, (int)x-(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x-(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
//		batch.drawLine(region, (int)x+(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
//		

			/*batch.end();
			batch.clearCache();
			batch.begin();*/
		}
		
	}
	
	
	
	//batch.end();
	//batch.setProjectionMatrix(camSys.camera.combined);

	batch.end();
}


/*private void check(LightRenderSystem lights2) {
	if (batch.isFull()){
		batch.end();
		batch.beginDraw();
		lights.setUniforms(Light.CHARACTER_SPRITES_LAYER_LEFT, shader);
		batch.render();
		batch.end();
		batch.clearCache();
		batch.begin();
	}
}*/

@Override
public void update(float deltaTime) {
	//if (true) return;

	batch.setProjectionMatrix(camSys.camera.combined);
	//batch.clearCache();
	batch.begin();
	//phase += deltaTime;
	//normal.bind(1);
	//diffuse.bind(0);
	//Gdx.gl.glEnable(GL20.GL_BLEND);
	Gdx.gl.glLineWidth(1f);
	
	if (false)
	for (int i = 0; i < allBodyEntities.size(); i++){
		Entity e = allBodyEntities.get(i);
		Body body = bodyM.get(e);
		Position pos = posM.get(e);
		float x =  (pos.pos.x * Main.PPM), y = (pos.pos.y * Main.PPM);
		float w = (body.width*Main.PPM);
		float h = (body.height * Main.PPM);
		//batch.drawLine(region, (int)x-(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
		//batch.drawLine(region, (int)x-(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f);
		//batch.drawLine(region, (int)x-(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x-(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
		//batch.drawLine(region, (int)x+(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
		int s = (int) (Math.min(body.width,  body.height) * Main.PPM);
		
		float speed = 9f;
		batch.setColor(Color.CYAN);
		//x-w, y+h, x-w+s, y+htl
		batch.drawLine( (int)x-(int)w+Main.PX/2f , (int)y+(int)h+Main.PX/2f, (int)x-(int)w+Main.PX/2f + s, (int)y+(int)h+Main.PX/2f);
		//x-w, y+h, x-w, y+h-s tl
		batch.drawLine( (int)x-(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x-(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f-s);
		
		//batch.setColor(fadeColor[(int)((phase * speed + 2)% fadeColor.length)]);
		//x+w-s, y+h, x+w, y+h tr
		batch.drawLine( (int)x+(int)w+Main.PX/2f - s, (int)y+(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f);
		//x+w, y+h, x+w, y+h-s tr
		batch.drawLine( (int)x+(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f-s);
		
		//batch.setColor(fadeColor[(int)((phase * speed + 4)% fadeColor.length)]);
		//x+w-s, y-h, x+w, y-h br
		batch.drawLine( (int)x+(int)w+Main.PX/2f - s, (int)y-(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
		//x+w, y-h+s, x+w, y-h br
		batch.drawLine( (int)x+(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f+s, (int)x+(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
		
		//batch.setColor(fadeColor[(int)((phase * speed + 6)% fadeColor.length)]);
		//x-w, y-h, x-w+s, y-h bl
		batch.drawLine( (int)x-(int)w+Main.PX/2f , (int)y-(int)h+Main.PX/2f, (int)x-(int)w+Main.PX/2f + s, (int)y-(int)h+Main.PX/2f);
		//x-w, y-h+s, x-w, y-h bl
		batch.drawLine( (int)x-(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f+s, (int)x-(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);

		/*if (batch.isFull()){
			batch.end();
			batch.beginDraw();
			lights.setUniforms(Light.CHARACTER_SPRITES_LAYER_LEFT, shader);
			batch.render();
			batch.end();
			batch.clearCache();
			batch.begin();
		}*/
		
	}
	
	batch.setColor(Color.GRAY);
	//x-w, y-h, x-w+s, y-h bl
	//batch.drawLine(region, 0, 0, 0, OverworldSystem.SCROLLING_MAP_HEIGHT * Main.PPM);
	//batch.drawLine(region, OverworldSystem.SCROLLING_MAP_WIDTH * Main.PPM, 0, OverworldSystem.SCROLLING_MAP_WIDTH * Main.PPM, OverworldSystem.SCROLLING_MAP_HEIGHT * Main.PPM);
	//batch.drawLine(region, 0, OverworldSystem.SCROLLING_MAP_HEIGHT * Main.PPM, OverworldSystem.SCROLLING_MAP_WIDTH * Main.PPM, OverworldSystem.SCROLLING_MAP_HEIGHT * Main.PPM);
	//batch.drawLine(region, 0, 0, OverworldSystem.SCROLLING_MAP_WIDTH * Main.PPM, 0);
	
	//batch.drawLine(region, 0, 0, 1000, 1000);

	/*if (batch.isFull()){
		batch.end();
		batch.beginDraw();
		lights.setUniforms(Light.CHARACTER_SPRITES_LAYER_LEFT, shader);
		batch.render();
		batch.end();
		batch.clearCache();
		batch.begin();
	}*/
	
	
	for (int i = 0; i < lineBodyEntities.size(); i++){
		Entity e = lineBodyEntities.get(i);
		LineBody line = lineM.get(e);
		Vector2 p = posM.get(e).pos;
		tmpV3.set(p).scl(Main.PPM);
		tmpV.set(p);
		//tmpV.add(-1, 0);
		tmpV.add(line.offsetA);
		tmpV2.set(p);
		//tmpV2.add(1, 0);
		tmpV2.add(line.offsetB);
		
		
		tmpV.scl(Main.PPM);
		tmpV2.scl(Main.PPM);
		//Gdx.app.log(TAG,  "render line "+line.offsetA+line.offsetB);
		batch.setColor(Color.CYAN);
		//batch.drawLine(region, (int)tmpV.x+Main.PX/2f, (int)tmpV.y+Main.PX/2f, (int)tmpV3.x+Main.PX/2f, (int)tmpV3.y+Main.PX/2f);
		//batch.drawLine(region, (int)tmpV2.x+Main.PX/2f, (int)tmpV2.y+Main.PX/2f, (int)tmpV3.x+Main.PX/2f, (int)tmpV3.y+Main.PX/2f);

		
		v.set(p).add(line.offsetA);
		v2.set(p).add(line.offsetB);
		Array<GridPoint2> returnArray = ray.line((int)v.x,  (int)v.y, (int)v2.x, (int)v2.y);
		batch.setColor(Color.BLUE);

		for (int k = 0; k < returnArray.size; k++){
			GridPoint2 pt = returnArray.get(k);
			
			int x = pt.x * Main.PPM + Main.PPM/2;
			int y = pt.y * Main.PPM + Main.PPM/2;
			int w = 8;
			int h = 8;
			//batch.drawLine(region, (int)x-(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
			//batch.drawLine(region, (int)x-(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f);
			//batch.drawLine(region, (int)x-(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x-(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
			//batch.drawLine(region, (int)x+(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
			
			
		}
		
		
		
		/*if (batch.isFull()){
			batch.end();
			batch.beginDraw();
			lights.setUniforms(Light.CHARACTER_SPRITES_LAYER_LEFT, shader);
			batch.render();
			batch.end();
			batch.clearCache();
			batch.begin();
		}*/
	}
	
	for (int i = 0; i < blockLineEntities.size(); i++){
		Entity e = blockLineEntities.get(i);
		BlockLine line = blockLineM.get(e);
		Vector2 p = posM.get(e).pos;
		tmpV3.set(p).scl(Main.PPM);
		tmpV.set(line.end);
		//tmpV.add(-1, 0);
		//tmpV.add(line.offset);
		tmpV2.set(p);
		//tmpV2.add(1, 0);
		//tmpV2.add(line.offsetB);
		
		
		tmpV.scl(Main.PPM);
		tmpV2.scl(Main.PPM);
		//Gdx.app.log(TAG,  "render line "+line.offsetA+line.offsetB);
		batch.setColor(PLACE_LINE_COLOR);
		batch.drawLine( (int)tmpV.x+Main.PX/2f, (int)tmpV.y+Main.PX/2f, (int)tmpV3.x+Main.PX/2f, (int)tmpV3.y+Main.PX/2f);
		batch.drawLine( (int)tmpV2.x+Main.PX/2f, (int)tmpV2.y+Main.PX/2f, (int)tmpV3.x+Main.PX/2f, (int)tmpV3.y+Main.PX/2f);

		
		/*v.set(p).add(line.offsetA);
		v2.set(p).add(line.offsetB);
		Array<GridPoint2> returnArray = ray.line((int)v.x,  (int)v.y, (int)v2.x, (int)v2.y);
		batch.setColor(Color.BLUE);

		for (int k = 0; k < returnArray.size; k++){
			GridPoint2 pt = returnArray.get(k);
			
			int x = pt.x * Main.PPM + Main.PPM/2;
			int y = pt.y * Main.PPM + Main.PPM/2;
			int w = 8;
			int h = 8;
			//batch.drawLine(region, (int)x-(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
			//batch.drawLine(region, (int)x-(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f);
			//batch.drawLine(region, (int)x-(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x-(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
			//batch.drawLine(region, (int)x+(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
			
			
		}*/
		
		
		
		/*if (batch.isFull()){
			batch.end();
			batch.beginDraw();
			lights.setUniforms(Light.CHARACTER_SPRITES_LAYER_LEFT, shader);
			batch.render();
			batch.end();
			batch.clearCache();
			batch.begin();
		}*/
	}
	
	
	batch.setColor(Color.WHITE);
	Gdx.gl.glLineWidth(1f);
	
	Gdx.gl.glLineWidth(2f);
	
	batch.setColor(PLACE_LINE_COLOR);

	for (int i = 0; i < blockOutlineEntities.size(); i++){
		Entity e = blockOutlineEntities.get(i);
		//Body body = bodyM.get(e);
		Position pos = posM.get(e);
		//Gdx.app.log(TAG,  "render block outline "+pos.pos);
		float x =  (pos.pos.x * Main.PPM), y = (pos.pos.y * Main.PPM);
		float w = .5f * Main.PPM;
		float h = .5f * Main.PPM;
		
		batch.drawLine( (int)x-(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
		batch.drawLine( (int)x-(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f);
		batch.drawLine( (int)x-(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x-(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
		batch.drawLine( (int)x+(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
		

		/*if (batch.isFull()){
			batch.end();
			batch.beginDraw();
			lights.setUniforms(Light.CHARACTER_SPRITES_LAYER_LEFT, shader);
			batch.render();
			batch.end();
			batch.clearCache();
			batch.begin();
		}*/
		
	}
	
	
	
	batch.setColor(Data.colors[Data.CYAN_INDEX]);
	for (int i = 0; i < dragBlockEntities.size(); i++){
		Entity e = dragBlockEntities.get(i);
		//Body body = bodyM.get(e);
		Position pos = posM.get(e);
		//Gdx.app.log(TAG,  "render block outline "+pos.pos);
		float x =  ((int)pos.pos.x * Main.PPM + Main.PPM/2), y = ((int)pos.pos.y * Main.PPM + Main.PPM/2);
		float w = .125f * Main.PPM;
		float h = .125f * Main.PPM;
		
		batch.drawLine( (int)x-(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
		batch.drawLine( (int)x-(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f);
		batch.drawLine( (int)x-(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x-(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
		batch.drawLine( (int)x+(int)w+Main.PX/2f, (int)y+(int)h+Main.PX/2f, (int)x+(int)w+Main.PX/2f, (int)y-(int)h+Main.PX/2f);
		
		/*if (batch.isFull()){
			batch.end();
			batch.beginDraw();
			lights.setUniforms(Light.CHARACTER_SPRITES_LAYER_LEFT, shader);
			batch.render();
			batch.end();
			batch.clearCache();
			batch.begin();
		}*/
		
	}
	
	batch.setColor(Data.colors[Data.BRIGHT_GREEN_INDEX]);
	for (int i = 0; i < roomDefEntities.size(); i++){
		Entity e = roomDefEntities.get(i);
		RoomDefinition room = e.getComponent(RoomDefinition.class);
		float x =  (room.min.x * Main.PPM), y = (room.min.y * Main.PPM);
		float x2 =  (room.max.x * Main.PPM), y2 = (room.max.y * Main.PPM);

		batch.drawLine( (int)x+Main.PX/2f, (int)y+Main.PX/2f, (int)x+Main.PX/2f, (int)y2+Main.PX/2f);
		batch.drawLine( (int)x+Main.PX/2f, (int)y+Main.PX/2f, (int)x2+Main.PX/2f, (int)y+Main.PX/2f);
		batch.drawLine( (int)x2+Main.PX/2f, (int)y2+Main.PX/2f, (int)x2+Main.PX/2f, (int)y+Main.PX/2f);
		batch.drawLine( (int)x2+Main.PX/2f, (int)y2+Main.PX/2f, (int)x+Main.PX/2f, (int)y2+Main.PX/2f);

//		if (batch.isFull()){
//			batch.end();
//			batch.beginDraw();
//			lights.setUniforms(Light.CHARACTER_SPRITES_LAYER_LEFT, shader);
//			batch.render();
//			batch.end();
//			batch.clearCache();
//			batch.begin();
//		}
		
	}
	
	batch.setColor(Data.colors[Data.BLUE_INDEX]);
	for (int i = 0; i < roomDefEntities.size(); i++){
		Entity e = roomDefEntities.get(i);
		RoomDefinition room = e.getComponent(RoomDefinition.class);
		float x =  (room.min9.x * Main.PPM), y = (room.min9.y * Main.PPM);
		float x2 =  (room.max9.x * Main.PPM), y2 = (room.max9.y * Main.PPM);

		batch.drawLine( (int)x+Main.PX/2f, (int)y+Main.PX/2f, (int)x+Main.PX/2f, (int)y2+Main.PX/2f);
		batch.drawLine( (int)x+Main.PX/2f, (int)y+Main.PX/2f, (int)x2+Main.PX/2f, (int)y+Main.PX/2f);
		batch.drawLine( (int)x2+Main.PX/2f, (int)y2+Main.PX/2f, (int)x2+Main.PX/2f, (int)y+Main.PX/2f);
		batch.drawLine( (int)x2+Main.PX/2f, (int)y2+Main.PX/2f, (int)x+Main.PX/2f, (int)y2+Main.PX/2f);
//
//		if (batch.isFull()){
//			batch.end();
//			batch.beginDraw();
//			lights.setUniforms(Light.CHARACTER_SPRITES_LAYER_LEFT, shader);
//			batch.render();
//			batch.end();
//			batch.clearCache();
//			batch.begin();
//		}
		
	}
	
	batch.end();
	

	
	//batch.beginDraw();
	//lights.setUniforms(Light.CHARACTER_SPRITES_LAYER_LEFT, shader);
	//batch.render();
	//batch.end();
	
	
	super.update(deltaTime);
}


}
