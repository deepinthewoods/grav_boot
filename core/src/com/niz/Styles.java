package com.niz;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBoxNiz;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBoxNiz.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.InventoryDrawable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gdx.extension.util.ColorUtil.HSVColor;

public class Styles {
	
	private static final String TAG = "Styles";
	public static NinePatchDrawable[] tableBack;
	public static NinePatchDrawable tableBackB;
	public static NinePatchDrawable[] methodBack;
	private static float h;
	private static float w;
	private static float border;
	public static BitmapFont inventoryFont;
	
	
	public static  void makeSkin(Skin skin, TextureAtlas atlas) {
		;
		
		//if (blank == null) throw new GdxRuntimeException("");
        //BitmapFont font = new BitmapFont(Gdx.files.internal("andale.fnt"), atlas.findRegion("fonts"));//, Gdx.files.internal("data/font/fonts.png"));
		AtlasRegion fontRegion = atlas.findRegion("fonts");
		BitmapFont font = new BitmapFont(Gdx.files.internal("kenpixel_square-16.fnt"), fontRegion);
		fontRegion.getTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		//font.getData().scale(2);
		font.getData().setScale(1);
		inventoryFont = font;
		
        //Gdx.app.log(TAG,  "bleh"+Gdx.files.internal("data/ini/Play/assets.ini").exists());;
        //font.getData().setScale(2);
		
        TextureRegion reg = atlas.findRegion("button");
        NinePatchDrawable patch = new NinePatchDrawable(new NinePatch(reg));
        patch.getPatch().setColor(Color.DARK_GRAY);
        border = Gdx.graphics.getHeight()/50f;
        


        TextureRegion blockSel = atlas.findRegion("buttonselected");
        NinePatch blockSel9 = new NinePatch(blockSel);
        blockSel9.setMiddleWidth(2);
        blockSel9.setMiddleHeight(2);

        
        NinePatchDrawable sliderBack = new NinePatchDrawable(new NinePatch(atlas.findRegion("slider")));
        NinePatchDrawable knob = new NinePatchDrawable(new NinePatch(reg));
        NinePatchDrawable cursor = new NinePatchDrawable(new NinePatch(reg));
        NinePatchDrawable selection = new NinePatchDrawable(new NinePatch(reg));
        NinePatchDrawable scroll = new NinePatchDrawable(new NinePatch(reg));
        NinePatchDrawable sliderKnob = new NinePatchDrawable(new NinePatch(atlas.findRegion("sliderknob")));
        NinePatchDrawable checked = new NinePatchDrawable(new NinePatch(reg));

        NinePatchDrawable back = new NinePatchDrawable(new NinePatch(reg));
        NinePatchDrawable up = new NinePatchDrawable(new NinePatch(reg));
        NinePatchDrawable down = new NinePatchDrawable(new NinePatch(reg));
        NinePatchDrawable upB = new NinePatchDrawable(new NinePatch(reg));
        NinePatchDrawable downB = new NinePatchDrawable(new NinePatch(reg));
        NinePatchDrawable midB = new NinePatchDrawable(new NinePatch(reg));
        NinePatchDrawable tfBack = new NinePatchDrawable(new NinePatch(reg));
        
        tableBack = new NinePatchDrawable[50];
        for (int i = 0; i < tableBack.length; i++){
        	tableBack[i] = new NinePatchDrawable(new NinePatch(reg));
        }
        
        methodBack = new NinePatchDrawable[35];
        for (int i = 0; i < methodBack.length; i++){
        	methodBack[i] = new NinePatchDrawable(new NinePatch(reg));
        }
        
        Color color = new Color(Color.WHITE);
        TextButton.TextButtonStyle tbStyle = new TextButton.TextButtonStyle(up, down, up, font);
        TextButton.TextButtonStyle tbStyleBoolean = new TextButton.TextButtonStyle(upB, midB, downB, font);
        TextButton.TextButtonStyle tbStyleNav = new TextButton.TextButtonStyle(upB, midB, downB, font);
        TextButton.TextButtonStyle tbStyleMain = new TextButton.TextButtonStyle(upB, midB, upB, font);
        TextButton.TextButtonStyle tbStyleMainSel = new TextButton.TextButtonStyle(upB, midB, down, font);

        //TextButton.TextButtonStyle tbStyleToggle = new TextButton.TextButtonStyle(up, down, checked, font);
        
        h = Gdx.graphics.getHeight()/18f;
        w = Gdx.graphics.getWidth()/2f;
        
        tbStyleBoolean.checkedFontColor = Color.WHITE;
        tbStyleBoolean.fontColor = Color.LIGHT_GRAY;
        
        tbStyleNav.checkedFontColor = Color.WHITE;
        tbStyleNav.fontColor = Color.WHITE;
        
        tbStyle.checkedFontColor = Color.WHITE;
        tbStyle.fontColor = Color.LIGHT_GRAY;
        
		
        //BitmapFont invFont = new BitmapFont(Gdx.files.internal("andale.fnt"), Gdx.files.internal("andale.png"), false);
        //ImageTextButton.ImageTextButtonStyle invButStyle = new ImageTextButton.ImageTextButtonStyle(backInv, backInv, backInv, invFont);
      
        NinePatchDrawable selInv = new NinePatchDrawable(atlas.createPatch("outlinedselected")){
        	

			
        	
        };
        NinePatch outPatch = atlas.createPatch("outlined");
        if (outPatch == null) throw new GdxRuntimeException("jsdlk"+atlas.getRegions());
        
        NinePatchDrawable backInv = new NinePatchDrawable(outPatch);
        
        
        Button.ButtonStyle invButStyle = new Button.ButtonStyle(backInv, backInv, selInv);//selInv);
        Color invCol = new Color(1f, 1f, 1f, .951f);
        backInv.setMinHeight(h*2);
        backInv.setMinWidth(h*2);
        setBorder(backInv, border);
        //backInv.getPatch().setColor(invCol);
        
        selInv.setMinHeight(h*2);
        selInv.setMinWidth(h*2);
        setBorder(selInv, border);
        invCol = new Color(1f, 1f, 1f, 1f);
        //selInv.getPatch().setColor(invCol);
        
        
        NinePatchDrawable invTableBack = new NinePatchDrawable(new NinePatch(reg));
        NinePatchDrawable invTableOther = new NinePatchDrawable(new NinePatch(reg));
        
        LabelStyle invLabelStyle = new Label.LabelStyle(font, Color.WHITE);
        invLabelStyle.background = invTableBack;
        Color invCol2 = new Color(1f, 1f, 1f, .1f);
        invTableOther.setMinHeight(h*2);
        invTableOther.setMinWidth(h*2);
        setBorder(invTableOther, border);
        invTableOther.getPatch().setColor(invCol2);
        
        invTableBack.setMinHeight(h*2);
        invTableBack.setMinWidth(h*2);
        setBorder(invTableBack, border);
        invCol2 = new Color(1f, 1f, 1f, .27f);
        invTableBack.getPatch().setColor(invCol2);
        
        LabelStyle invAmountStyle = new Label.LabelStyle(font, Color.WHITE);
        
        
        NinePatchDrawable upm = new NinePatchDrawable(new NinePatch(reg));
        NinePatchDrawable downm = new NinePatchDrawable(new NinePatch(reg));
        NinePatchDrawable backm = new NinePatchDrawable(new NinePatch(reg));
        Color mCol = new Color(1f, 1f, 1f, .0f);
        upm.setMinHeight(h);
        upm.setMinWidth(h);
        setBorder(upm, border);
        upm.getPatch().setColor(mCol);
        
        downm.setMinHeight(h);
        downm.setMinWidth(h);
        setBorder(downm, border);
        //downm.getPatch().setColor(mCol);
        
        backm.setMinHeight(h);
        backm.setMinWidth(h);
        setBorder(backm, border);
        backm.getPatch().setColor(mCol);
        
		BitmapFont fontm  = new BitmapFont(Gdx.files.internal("lunaboy-16.fnt"), fontRegion);
        TextButton.TextButtonStyle methodButStyle = new TextButton.TextButtonStyle(upm, downm, backm, fontm);
        methodButStyle.fontColor = Color.BLACK;

        
        ////////////////////
        
        NinePatchDrawable upm2 = new NinePatchDrawable(new NinePatch(reg));
        NinePatchDrawable downm2 = new NinePatchDrawable(new NinePatch(reg));
        NinePatchDrawable backm2 = new NinePatchDrawable(new NinePatch(reg));
        NinePatchDrawable checkedm2 = new NinePatchDrawable(new NinePatch(reg));
        Color mCol2 = new Color(1f, 1f, 1f, .0f);
        upm2.setMinHeight(h);
        upm2.setMinWidth(h);
        setBorder(upm2, border);
        upm2.getPatch().setColor(mCol2);
        
        checkedm2.setMinHeight(h);
        checkedm2.setMinWidth(h);
        setBorder(checkedm2, border);
        checkedm2.getPatch().setColor(mCol2);
        
        downm2.setMinHeight(h);
        downm2.setMinWidth(h);
        setBorder(downm2, border);
        //downm.getPatch().setColor(mCol);
        
        backm2.setMinHeight(h);
        backm2.setMinWidth(h);
        setBorder(backm2, border);
        backm2.getPatch().setColor(mCol2);
        
        Button.ButtonStyle momentaryButtonStyle = new ButtonStyle(upm2, downm2, checkedm2);
        //momentaryButtonStyle.fontColor = Color.BLACK;
        
        ////////////////
        
        Button.ButtonStyle butStyle = new Button.ButtonStyle(up, down, back);
        

        Button.ButtonStyle butStyleToggle = new Button.ButtonStyle(up, down, checked);
        
        Touchpad.TouchpadStyle tpStyle = new Touchpad.TouchpadStyle(back, knob);

        TextField.TextFieldStyle tfStyle = new TextField.TextFieldStyle(font, color, cursor, selection, tfBack);
        
        Label.LabelStyle lStyle = new Label.LabelStyle(font, color);
        lStyle.background = back;
        lStyle.background.setMinHeight(h);
        sliderBack.setMinHeight(h);
        sliderBack.setMinWidth(w);
        sliderBack.getPatch().setColor(new Color(1f, 1f, 1f, .25f));
        setBorder(sliderBack, border);
        //sliderBack.setLeftWidth(w);

        back.setMinHeight(h);
        back.setMinWidth(w);
        setBorder(back, border);
        
        Color textFieldColor = new Color(.477f, .477f, .477f, 1f);
        tfBack.setMinHeight(h);
        tfBack.setMinWidth(w);
        setBorder(tfBack, border);
        tfBack.getPatch().setColor(textFieldColor);
        
        Color tableColor = new Color(.8477f, .8477f, .8477f, 1f);
        HSVColor col = new HSVColor(0);
        col.h = 0f;
        col.s = .29713f;
        col.v = .4692f;
        for (int i = 0; i < tableBack.length; i++){
        	col.h += 1f/tableBack.length;
        	col.toRGB(tableColor);
        	//tableColor.a = .645f;
        	tableBack[i].setMinHeight(h);
        	tableBack[i].setMinWidth(h);
        	setBorder(tableBack[i], 5);
        	tableBack[i].getPatch().setColor(tableColor);
        }
        
        col.h = 0f;
        col.s = .429713f;
        col.v = .9974692f;
        for (int i = 0; i < methodBack.length; i++){
        	col.h += 1f/methodBack.length;
        	col.toRGB(tableColor);
        	//tableColor.a = .645f;
        	methodBack[i].setMinHeight(h);
        	methodBack[i].setMinWidth(h);
        	setBorder(methodBack[i], 5);
        	methodBack[i].getPatch().setColor(tableColor);
        }
        
        
        
        
        Color tbCol = new Color(0f, 0f, 0f, .315f);
        up.setMinHeight(h);
        up.setMinWidth(h);
        setBorder(up, border);
        up.getPatch().setColor(tbCol);
        
        Color tfbooleanCol = new Color(Color.DARK_GRAY);
        upB.setMinHeight(h);
        upB.setMinWidth(h);
        setBorder(upB, border);
        upB.getPatch().setColor(tfbooleanCol);
        
        Color tfbooleanColDown = new Color(Color.DARK_GRAY);
        downB.setMinHeight(h);
        downB.setMinWidth(h);
        setBorder(downB, border);
        downB.getPatch().setColor(tfbooleanColDown);
        
        Color tfbooleanColDownm = new Color(Color.DARK_GRAY);
        midB.setMinHeight(h);
        midB.setMinWidth(h);
        setBorder(midB, border);
        midB.getPatch().setColor(tfbooleanColDownm);
        
        down.setMinHeight(h);
        down.setMinWidth(h);
        setBorder(down, border);
        down.getPatch().setColor(Color.LIGHT_GRAY);
        //back2.setMinHeight(h);
        //back2.setMinWidth(h);
        //setBorder(back2, border);
        
        
        //back3.setMinHeight(h);
        //back3.setMinWidth(h);
        //setBorder(back3, border);
        
        checked.setMinHeight(h);
        checked.setMinWidth(h);
        setBorder(checked, border);
        checked.getPatch().setColor(Color.GREEN);
        
        patch.setMinHeight(h);
        patch.setMinWidth(w);
        setBorder(patch, border);
        back.setMinHeight(h);
        back.setMinWidth(w);
        setBorder(back, border);
        back.getPatch().setColor(Color.DARK_GRAY);
        //back.setLeftWidth(5);
       // back.setRightWidth(5);
        
        sliderKnob.setMinHeight(h+8);
        //sliderKnob.setMinWidth(w+8);
        sliderBack.setLeftWidth(4);sliderBack.setRightWidth(4);
        Slider.SliderStyle sliderStyle = new Slider.SliderStyle(sliderBack, sliderKnob);
        
        //SlideColorPicker.SlideColorPickerStyle colorSliderStyle = new SlideColorPicker.SlideColorPickerStyle();
       // colorSliderStyle.knob = sliderKnob;
        //colorSliderStyle.background = sliderBack;
        Label.LabelStyle labeStyle = new Label.LabelStyle(font, color);
        
        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle(back, scroll, knob, scroll, knob);

        //TextFieldStyle textFieldStyle = new TextField.TextFieldStyle(fontm Color.WHITE, cursor, selection, back);
        labeStyle.background = back;
		BitmapFont fontLabel = new BitmapFont(Gdx.files.internal("kenpixel-16.fnt"), fontRegion);
		fontLabel.getData().setScale(1.3f);
		BitmapFont fontTitle  = new BitmapFont(Gdx.files.internal("lunaboy-16.fnt"), fontRegion);
		fontTitle.getData().setScale(2.3f);
		BitmapFont fontSelected  = new BitmapFont(Gdx.files.internal("kenpixel-16.fnt"), fontRegion);
		fontSelected.getData().setScale(1.5f);

		
        Label.LabelStyle itemLabel = new Label.LabelStyle(fontLabel, color);
        Label.LabelStyle itemLabelTitle = new Label.LabelStyle(fontTitle, color);
        Label.LabelStyle itemLabelSelected = new Label.LabelStyle(fontSelected, color);
        itemLabel.fontColor = Color.LIGHT_GRAY;
        itemLabelSelected.fontColor = Color.WHITE;
        //fontTitle.getData().setScale(2f);
        
        CheckBoxNiz.CheckBoxStyle cbStyle = new CheckBoxStyle(down, up, font, color);
        
        Window.WindowStyle windowS = new Window.WindowStyle(font, Color.WHITE, up);
        
        
        skin.add("default", butStyle);
        skin.add("default", tbStyle);
        skin.add("boolean", tbStyleBoolean);
        //skin.add("toggle", tbStyleToggle2);
        skin.add("default", tfStyle);
 
        skin.add("default", cbStyle);
        skin.add("navbar", tbStyleNav);
    
        skin.add("default-horizontal", sliderStyle);
        //skin.add("default-horizontal", colorSliderStyle);
        skin.add("default", labeStyle);
        skin.add("iteminfo", itemLabel);
        skin.add("iteminfoselected", itemLabelSelected);
        skin.add("iteminfotitle", itemLabelTitle);
        //skin.add("default", scrollStyle);
        //skin.add("default", tableStyle);
        skin.add("inventory", invButStyle);
        skin.add("inventory", invLabelStyle);
        skin.add("method", methodButStyle);
        //skin.add("inventory", butStyle);
        skin.add("momentaryButton", momentaryButtonStyle);
        skin.add("invamount", invAmountStyle);
        skin.add("mainmenu", tbStyleMain);
        skin.add("mainmenuselectable", tbStyleMainSel);
        skin.add("default", windowS);
    }
	
	private static void setBorder(BaseDrawable draw, float border){
        draw.setBottomHeight(border); draw.setTopHeight(border);
        draw.setLeftWidth(border); draw.setRightWidth(border);
    }

	public static InventoryDrawable getInvDrawable() {
		InventoryDrawable draw = new InventoryDrawable();
		draw.setMinHeight(h);
		draw.setMinWidth(h);
        setBorder(draw, border);
		
		return draw;
	}
}
