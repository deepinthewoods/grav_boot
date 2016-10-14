package com.niz.ui.elements;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.niz.component.Race;

public class RaceSelectionButton extends Table {

	public int current;
	private Label text;

	public RaceSelectionButton(int limbIndex, Skin skin){
		super(skin);
		TextButton subButton = new TextButton("<", skin);
		TextButton addButton = new TextButton(">", skin);
		text = new Label("", skin);
		Label limbName = new Label(""+Race.limbNames[limbIndex] + " :", skin);
		this.add(subButton).left();
		this.add(new Table());
		this.add(limbName);
		this.add(text);
		Table tab = new Table();
		tab.add(new Actor()).expandX().fillX();
		this.add(tab).expand().fill();
		this.add(addButton).right();
		
		final RaceSelectionButton butt = this;
		
		subButton.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				butt.decrement();
			}
		});
		
		addButton.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				butt.increment();
			}
		});
		setName();
	}

	protected void decrement() {
		current--;
		if (current < 0) current = Race.TOTAL_RACES-1;
		setName();
	}
	protected void increment(){
		current++;
		if (current >= Race.TOTAL_RACES) current = 0;
		setName();
	}
	
	public void setName(){
		text.setText(Race.raceNames[current]);;
	}
	
	
}
