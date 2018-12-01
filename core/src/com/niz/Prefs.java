package com.niz;


import com.badlogic.gdx.utils.Array;

public class Prefs {

	public int control_button_height = 150;
	public int jump_button_width = 150;
	public int inventory_button_height = 50;
	public int inventory_button_width = 50;
	//public int move_slider_width_multiplier = .5f;
	public int move_slider_width = 250;
	private transient GameInstance game;
	public float action_label_size = 70;
	public boolean cut_off_jump_button = true, cut_off_move_buttons = true;
	public String previously_launched_game = "";;
	public int toon_levels = 3;
    public int toon_type = 0;
    public Prefs(){

	}

    public void set(GameInstance game){
		this.game = game;
	}
	public void back(){
		//Gdx.app.log("prefs", "SOMETHING!!!!!!!" + cut_off_jump_button);
		//game.invScreen.setCutOffButtons(cut_off_jump_button, cut_off_move_buttons);
		//game.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Main.TILE_VIEW_MIN, Main.TILE_VIEW_MAX);
	}
}
