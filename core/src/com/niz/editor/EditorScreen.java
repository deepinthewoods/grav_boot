package com.niz.editor;

import com.badlogic.ashley.core.EngineNiz;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ArrayAddButton;
import com.badlogic.gdx.scenes.scene2d.ui.ArrayExpander;
import com.badlogic.gdx.scenes.scene2d.ui.ArraySubButton;
import com.badlogic.gdx.scenes.scene2d.ui.EditorLabel;
import com.badlogic.gdx.scenes.scene2d.ui.EditorStackActor;
import com.badlogic.gdx.scenes.scene2d.ui.Expander;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.MethodButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.reflect.Annotation;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.niz.Styles;
import com.niz.anim.Range;
import com.niz.component.BooleanInput;
import com.niz.component.TestComponent;
import com.niz.observer.Observer;
import com.niz.ui.edgeUI.EdgeUI;
import com.niz.ui.edgeUI.InventoryScreen;
import com.niz.ui.edgeUI.UITable;
import com.niz.ui.elements.BackgroundTouchNone;
import com.niz.ui.elements.EditorTableDisplay;
import com.niz.ui.elements.UIElement;


public class EditorScreen extends EdgeUI implements Observer{
	Array<EditorStackActor> stack = new Array<EditorStackActor>(true, 16);
	EditorTableDisplay disp =  new EditorTableDisplay(), nav = new EditorTableDisplay();
	public EditorTable navTable;// = new Table();
	EditorTable mainTable;// = new EditorTable();
	private Skin skin;
	private ScrollPane navPane;
	private ScrollPane mainPane;
	private EditorStackActor root;
	public static final int DEPTH_LIMIT = 3;
	private static final String TAG = "editor screen";
	private static final float PAD = 2;
	Array<Expander> contractQueue = new Array<Expander>();
	Array<ArrayExpander> arrayContractQueue = new Array<ArrayExpander>();
	Array<ArrayExpander> arrayExpandQueue = new Array<ArrayExpander>();

	ActorPool<Expander> expanderP ;
	private ActorPool<FloatButton> floatP;
	private ActorPool<BooleanButton> booleanP;
	private ActorPool<IntegerButton> integerP;
	private ActorPool<EditorTable> tableP;
	final protected ActorPool<EventListener> listenerP;
	private ActorPool<EditorLabel> editorLabelP;
	private ActorPool<Label> labelP;
	private ActorPool<Actor> actorP;
	private ActorPool<StringButton> stringP;
	private ActorPool<ArrayExpander> arrayExpanderP;
	private ActorPool<StringArrayElementButton> stringArrayElementP;
	private ActorPool<ExpanderEventListener> expanderListenerP;
	private ActorPool<ArrayAddButton> arrayAddButtonP;
	
	private ObjectMap<Object, String> expandedStore = new ObjectMap<Object, String>();
	private ActorPool<ArraySubButton> arraySubButtonP;
	private ActorPool<EditorStackActor> stackActorP;
	private boolean reTable;
	//private boolean firstRun = true;;//REALLY HACKY does retable in the update loop one time since everywhere else I put it futzes up the table
	private ActorPool<FloatSlider> sliderP;
	private ActorPool<EventListener> sliderListenerP;
	private ActorPool<MethodButton> methodP;
	private Stage theStage;
	public InventoryScreen invScreen;
	public boolean on = false;
	private Method backMethod;
	
	public EditorScreen(Skin skin, EngineNiz engine){
		super();
		engine.getSubject("inventoryToggle").add(this);
		
		final EditorScreen ed = this;
		
    	tableP = new ActorPool<EditorTable>(skin, this){
    		@Override
    		protected EditorTable newObject() {
    			EditorTable r = new EditorTable(ed);
    			//r.addListener(listenerP.obtain());
    			//r.defaults().top().left();
    			return r;
    		}
    	};
    	super.colspanTop = true;
		expandX[4] = false;
		expandX[4] = true;
		expandX[5] = false;
		expandY[6] = false;
		expandX[3] = true;
		//expand[6] = true;
		mainTable = tableP.obtain();
		navTable = new EditorTable(ed);
		navPane = new ScrollPane(navTable);
		mainPane = new ScrollPane(mainTable);
		navPane.setOverscroll(false, true);
		mainPane.setOverscroll(false, true);
		this.skin = skin;
		Table dtable = (Table) disp.actor; 
		//dtable.add(navPane);
		//dtable.row();
		dtable.add(mainPane);
		//dtable.add(new Label("x", skin)).expand().fill();

		dtable.row();
		//dtable.defaults().height(1000);
		
		//root = new EditorStackActor("Edit", skin, ed);
		//root.set(new TestComponent(), 0, "root");
		
		TestComponent component = new TestComponent();
		//root.index = 0;
		//root.set(root.self, 0, "rt");
		
		//navTable.set(null, root.self, "rooooT");
		
		EditorTable aParentTable = tableP.obtain();
		navTable.set(aParentTable , component, "root");
		navTable.set("");
		
		//stack.add(root);
		
		
		//navTable.addActor(root);
		((Table)(nav.actor)).add(navTable).left();//.colspan(0).left();
		//((Table)(nav.actor)).add(new Actor());
		
		sides[0] = new UITable();
        sides[0].min = new UIElement[1];
        //sides[0].min[0] = disp;
        table.row();

        sides[1] = new UITable();
        sides[1].min = new UIElement[1];
        //sides[1].min[0] = nav;
        //sides[1].max = new UIElement[1];
        table.row();

        sides[2] = new UITable();
        sides[2].min = new UIElement[1];
        //sides[2].min[0] = new ControllerPad();
        table.row();

        sides[3] = new UITable();
        sides[3].vertical = true;
        sides[3].min = new UIElement[1];
       // sides[3].min[0] = new BlockSelector();
       // sides[3].max = new UIElement[1];
        //sides[3].max[0] = new BlockColorSelector();
        table.row();

        sides[4] = new UITable();
        sides[4].min = new UIElement[1];
        sides[4].min[0] = disp;
        
		//sides[4].min[1] = expandingUIElement ;
        table.row();

        sides[5] = new UITable();
        sides[5].min = new UIElement[1];
        //sides[5].min[0] = new ControllerPad();
        table.row();

        sides[6] = new UITable();
        sides[6].min = new UIElement[1];
        //sides[6].min[0] = new ControllerSliderBoolean();
        //sides[6].table.getCells().get(0).expand();
        table.row();

        sides[7] = new UITable();
        sides[7].min = new UIElement[1];
        //sides[7].min[0] = nav;//new ControllerButton("T", Code.STRAFE_RIGHT);
        table.row();

        sides[8] = new UITable();
        sides[8].min = new UIElement[1];
        //ButtonPad btnPad = new ButtonPad();
        //btnPad.send = new String[]{"screen"};
        //sides[8].min[0] = btnPad;
        table.row();
        back = new BackgroundTouchNone();
        
        
        listenerP = new ActorPool<EventListener>(skin, this){
        	@Override
        	protected EventListener newObject() {
        		// TODO Auto-generated method stub
        		return new EventListener() {
        			
        			@Override
        			public boolean handle(Event event) {
        				if (event instanceof InputEvent){
        					InputEvent i = (InputEvent) event;
        					if (i.getType() == Type.keyUp && i.getKeyCode() == Keys.ENTER){
        						//Gdx.app.log("button", "event"+event+event.getClass() + "  from "+event.getListenerActor().getClass());
        						//event.handle();
        						
        						event.getListenerActor().getStage().unfocusAll();
        						((FieldButton)event.getListenerActor()).apply(); 
        					}
        				}
        				return false;
        			}
        		};
        	}
        };
        
        sliderListenerP = new ActorPool<EventListener>(skin, this){
        	@Override
        	protected EventListener newObject() {
        		// TODO Auto-generated method stub
        		return new EventListener() {
        			
        			@Override
        			public boolean handle(Event event) {
        				if (event instanceof ChangeEvent){
        					InputEvent i = (InputEvent) event;
        						
        					event.getListenerActor().getStage().unfocusAll();
        					((FieldButton)event.getListenerActor()).apply(); 
        					
        				}
        				return false;
        			}
        		};
        	}
        };
        
        expanderListenerP = new ActorPool<ExpanderEventListener>(skin, this){
        	@Override
        	protected ExpanderEventListener newObject() {
        		// TODO Auto-generated method stub
        		return new ExpanderEventListener();
        	}
        };
        
        stackActorP = new ActorPool<EditorStackActor>(skin, this){
        	@Override
        	protected EditorStackActor newObject() {
        		// TODO Auto-generated method stub
        		return new EditorStackActor("Edit", skin, ed);
        	}
        };
        
        expanderP = new ActorPool<Expander>(skin, this){
    		@Override
    		protected Expander newObject() {
    			// TODO Auto-generated method stub
    			Expander r = new Expander("+", skin);
    			
    			return r;
    		}
    	};
    	
    	 arrayAddButtonP = new ActorPool<ArrayAddButton>(skin, this){
     		@Override
     		protected ArrayAddButton newObject() {
     			// TODO Auto-generated method stub
     			ArrayAddButton r = new ArrayAddButton("[+]", skin);
     			
     			return r;
     		}
     	};
     	
     	arraySubButtonP = new ActorPool<ArraySubButton>(skin, this){
     		@Override
     		protected ArraySubButton newObject() {
     			
     			ArraySubButton r = new ArraySubButton("[-]", skin);
     			
     			return r;
     		}
     	};
    	
    	arrayExpanderP = new ActorPool<ArrayExpander>(skin, this){
    		@Override
    		protected ArrayExpander newObject() {
    			// TODO Auto-generated method stub
    			ArrayExpander r = new ArrayExpander("+", skin);
    			
    			return r;
    		}
    	};
    	
    	floatP = new ActorPool<FloatButton>(skin, this){
    		@Override
    		protected FloatButton newObject() {
    			FloatButton r = new FloatButton( skin);
    			r.addListener(listenerP.obtain());
    			return r;
    		}
    	};
    	
    	booleanP = new ActorPool<BooleanButton>(skin, this){
    		@Override
    		protected BooleanButton newObject() {
    			BooleanButton r = new BooleanButton( skin);
    			r.addListener(new EventListener() {
        			
        			@Override
        			public boolean handle(Event event) {
        				if (event instanceof InputEvent){
        					InputEvent i = (InputEvent) event;
        					if (i.getType() == Type.touchDown){
        						Gdx.app.log("button", "event"+event+event.getClass() + "  from "+event.getListenerActor().getClass());
        					//if (i.getType() == Type.keyUp && i.getKeyCode() == Keys.ENTER){
        						//event.handle();
        						
        						event.getListenerActor().getStage().unfocusAll();
        						((FieldButton)event.getListenerActor()).apply(); 
        					}
        				}
        				return false;
        			}
        		});
    			return r;
    		}
    	};
    	
    	methodP = new ActorPool<MethodButton>(skin, this){
    		@Override
    		protected MethodButton newObject() {
    			MethodButton r = new MethodButton( skin);
    			r.addListener(expanderListenerP.obtain());
    			return r;
    		}
    	};
    	
    	stringP = new ActorPool<StringButton>(skin, this){
    		@Override
    		protected StringButton newObject() {
    			StringButton r = new StringButton( skin);
    			r.addListener(listenerP.obtain());
    			return r;
    		}
    	};
    	stringArrayElementP = new ActorPool<StringArrayElementButton>(skin, this){
    		@Override
    		protected StringArrayElementButton newObject() {
    			StringArrayElementButton r = new StringArrayElementButton( skin);
    			r.addListener(listenerP.obtain());
    			return r;
    		}
    	};
    	
    	integerP = new ActorPool<IntegerButton>(skin, this){
    		@Override
    		protected IntegerButton newObject() {
    			IntegerButton r = new IntegerButton( skin);
    			r.addListener(listenerP.obtain());
    			return r;
    		}
    	};
    	
    	
    	editorLabelP = new ActorPool<EditorLabel>(skin, this){
    		@Override
    		protected EditorLabel newObject() {
    			EditorLabel r = new EditorLabel("", skin, ed);
    			r.addListener(listenerP.obtain());
    			return r;
    		}
    	};
    	
    	labelP = new ActorPool<Label>(skin, this){
    		@Override
    		protected Label newObject() {
    			Label r = new Label("", skin);
    			//r.addListener(listenerP.obtain());
    			return r;
    		}
    	};
    	
    	actorP = new ActorPool<Actor>(skin, this){
    		@Override
    		protected Actor newObject() {
    			Actor r = new Actor();
    			//r.addListener(listenerP.obtain());
    			return r;
    		}
    	};
    	
    	sliderP = new ActorPool<FloatSlider>(skin, this){
    		@Override
    		protected FloatSlider newObject() {
    			FloatSlider r = new FloatSlider(skin);
    			//r.addListener(listenerP.obtain());
    			return r;
    		}
    	};
    	
    	
    	
    	Pools.set(ExpanderEventListener.class, expanderListenerP);
    	Pools.set(EventListener.class, listenerP);
    	Pools.set(Expander.class, expanderP);
    	Pools.set(ArrayExpander.class, arrayExpanderP);
    	Pools.set(FloatButton.class, floatP);
    	Pools.set(BooleanButton.class, booleanP);
    	Pools.set(IntegerButton.class, integerP);
    	Pools.set(EditorTable.class, tableP);
    	Pools.set(EditorLabel.class, editorLabelP);
    	Pools.set(Label.class, labelP);
    	Pools.set(Actor.class, actorP);
    	Pools.set(StringButton.class, stringP);
    	Pools.set(StringArrayElementButton.class, stringArrayElementP);
    	Pools.set(ArrayAddButton.class, arrayAddButtonP);
    	Pools.set(ArraySubButton.class, arraySubButtonP);
    	Pools.set(EditorStackActor.class, stackActorP);
    	Pools.set(FloatSlider.class, sliderP);
    	Pools.set(MethodButton.class, methodP);
    	
		this.addStackActor(component, "root", navTable);
    	//navTable.addToStack();
		//
		reTableStack();
//		make();

	}
	
	public UIElement getElement(){
		return disp;
	}
	
	public void setObject(Object o, String name){
		this.addStackActor(o, name, navTable);
    	//navTable.addToStack();
		//
		backMethod = null;
		
		reTableStack();
		clear(1);
		Method[] methods = ClassReflection.getMethods(o.getClass());
		for (int i = 0; i < methods.length; i++){
			Method m = methods[i];
			if (m.getName().equals("back")) backMethod = m;
		}
	}
	
	@Override
	public void init(Skin skin, Stage stage, EngineNiz engine) {
		super.init(skin, stage, engine);
		this.theStage = stage;
		//navTable.getCell(nav.actor).colspan(10);
		//((Table)(nav.actor)).getCell(navTable).colspan(10);
		//Array<Cell> cells = ((Table)(disp.actor)).getCells();
		//for (int i = 0; i < cells.size; i++)
		//	cells.get(i).left();
	}

	public void update(){
		//Array<Cell> cells = navTable.getCells();
		//for (Cell c : cells){
		//	c.colspan(1);
		//}
		if (contractQueue.size > 0){
			for (Expander e : contractQueue){
				e.contract();
			}
			contractQueue.clear();
		}
		if (arrayContractQueue.size > 0){
			for (ArrayExpander e : arrayContractQueue){
				e.contract();
			}
			arrayContractQueue.clear();
		}
		if (arrayExpandQueue.size > 0){
			for (ArrayExpander e : arrayExpandQueue){
				
				e.expand();
				
			}
			arrayExpandQueue.clear();
		}
		arrayExpandQueue.clear();
		
		
		
		if (reTable){
			reTable = false;
			mainTable.resetTableCompletely(0);
			expandedStore.put(stack.peek().self, "");
			
			make();
			//mainTable.layout();
		}
		
		expandedStore.clear();
		//if (firstRun )reTableStack();
		//firstRun = false;
	}
	
	@Override
	public void addTo(Stage stage) {
		
		super.addTo(stage);
		on = true;
	}


	public void addToStack(EditorTable tab){
		addStackActor(tab.self, tab.name, tab);
	}
	
	private void addStackActor(Object self, String name, EditorTable tab) {
		EditorStackActor act = stackActorP.obtain();
		act.self = self;
		//act.table = tab;
		act.addListener(expanderListenerP.obtain());
		if (self != null)
			act.setText(name);
		//act.table = tab;
		stack.add(act);
		if (tab.isObject){
			if (self == null) throw new GdxRuntimeException("hhh");
			act.set(self, stack.size-1, tab.labelName);
		} else {
			act.setArray(self, tab.arrayClass, tab.labelName, stack.size-1);
		}
		
		if (self == null)Gdx.app.log("ed screen", "add STACK ACTOR");
		
	}

	public void setStackSize(int pos){
		for (int i = stack.size-1; i > pos; i--){
			EditorStackActor a = stack.pop();
			//a.remove();
			expandedStore.put(a.self, null);
			Pools.free(a);
		}
		reTableStack();
		//make();
	}
	
	public void reTableStack() {
		navTable.clear();
		for (int i = 0; i < stack.size; i++){
			EditorStackActor act = stack.get(i);
			
			navTable.add(act);
		}
		//

		reTable = true;
	}
	
	

	public void make(){
		//mainTable.resetTableCompletely(0);;
		EditorStackActor act = stack.peek();
		//Gdx.app.log("edscr", "MAKE EE " + act.self.getClass());
		if (act.isObject){
			makeObjectButton(act.labelName, act.self, mainTable, null, 0);
		} else {
			makeArrayButton(mainTable, act.labelName, (Array<?>) act.self, act.arrayClass, skin);
			
		}
		//mainTable.add(new Label("x", skin)).expand().fill();
		//mainTable.add(tableP.obtain()).fillX().colspan(10);
		mainTable.row();
		//makeFor(stack.peek().self, mainTable, 0);
	}
	
	public void clear(int stackSize){
		SnapshotArray<Actor> actors = mainTable.getChildren();
		for (Actor act : actors){
			if (act instanceof EditorTable){
				Pools.free(act);
			}
		}
		mainTable.clear();
		setStackSize(stackSize);
		
		
		
	}
	private void makeMethodButton(EditorTable table, String name, Method m, Object o,
			Skin skin) {
		

		EditorTable newTable = tableP.obtain();
		newTable.set(table, null, name);
		newTable.setBackground(Styles.methodBack[(1897+o.hashCode())%Styles.methodBack.length]);
		
		//expander.clear();
		
		
		
		
		//ArraySubButton subber = arraySubButtonP.obtain();
		
		MethodButton lab = methodP.obtain();
		lab.set(name, m, o);
		//lab.setChecked(true);
		//lab.addListener(expanderListenerP.obtain());
		//lab.parentObject = parent;
		newTable.set(name);
		
		newTable.set(table, o, name);
		newTable.add(lab).colspan(3);
		lab.setText(name);
		//newTable.add(subber);
		newTable.row();
		table.add(this.actorP.obtain());
		table.add(newTable).left().pad(PAD);//.colspan(2);;
		
		
	
	}
	private void makeMethodButtonsimple(Table table, String name, Method m, Object o,
			Skin skin) {
		MethodButton b = methodP.obtain();
		Label t = labelP.obtain();

		b.set(name, m, o);
		table.add(actorP.obtain());//.top().left();
		table.add(b).top().left();
		table.row();
		
		//f.set(o, 1);
		//Gdx.app.log("editorscr", "made bool button");
		
	}
	
	public final void makeFor(Object o, EditorTable table, int depth){
		if (depth >= DEPTH_LIMIT) return;
		//Gdx.app.log("editorscr", "making");
		Field[] fields = ClassReflection.getDeclaredFields(o.getClass());
		for (int i = 0; i < fields.length; i++){
			Field f = fields[i];
			if (f.isStatic() || f.isTransient()) continue;
			Class type = f.getType();
			//Gdx.app.log("editorscr", "i"+i);
			if (type.isPrimitive()){
				//Gdx.app.log("editorscr", "prim");
				if (type.isAssignableFrom(Integer.TYPE)){
					makeIntegerButton(table, f.getName().replace("_", " ").replace("_", " "), f, o, skin);
				} else if (type.isAssignableFrom(Float.TYPE)){
					//Gdx.app.log("editorscr", "float!");
					makeFloatButton(table, f.getName().replace("_", " "), f, o, skin);
				} else if (type.isAssignableFrom(Boolean.TYPE)){
					makeBooleanButton(table, f.getName().replace("_", " "), f, o, skin);
				} 
			} else if (type.isAssignableFrom(Array.class)){
			    Class<?> arrayClass = (Class<?>) f.getElementType(0);
			    //Gdx.app.log("editor scr", "TYPE "+arrayClass);
			    try {
					makeArrayButton(table, f.getName().replace("_", " "), (Array<?>) f.get(o), arrayClass, skin);
					table.row();
				} catch (ReflectionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    
			} else if (type.isAssignableFrom(String.class)){
				Gdx.app.log(TAG, "string Button");
				makeStringButton(table, f.getName().replace("_", " "), f, o, skin);
			}
			else {
				
				try {
					Gdx.app.log(TAG, "object Button "+f.get(o).getClass());
					makeObjectButton(f.getName().replace("_", " "), f.get(o), table, o, depth++);
					table.row();
				} catch (ReflectionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		//methods
		Method[] methods = ClassReflection.getMethods(o.getClass());
		methods = ClassReflection.getDeclaredMethods(o.getClass());
		for (int i = 0; i < methods.length; i++){
			Method m = methods[i];
			//if (m.isAnnotationPresent(Executable.class))
			Annotation[] anns = m.getDeclaredAnnotations();
			for (int a = 0; a < anns.length; a++){
				Gdx.app.log(TAG,  "!!!!!!!!!!" + anns[a].toString());
				
				
			}
			String name = m.getName();
			if (m.isPublic() && name.charAt(0) == '_')
				makeMethodButton(table, name.substring(1, name.length()), m, o, skin);
			
			
			
		}
	}

	 

	private void makeForArrayElement(Object object, EditorTable table,
			int depth, int i, Class type, Array<?> arr, ArrayExpander parentExpander) {
		if (type.isAssignableFrom(Array.class)){
		    
		    //Gdx.app.log("editor scr", "TYPE "+arrayClass);
		    
			//makeArrayButton(table, "["+i+"]", arr, type, skin);
			//Class<?> arrayClass = (Class<?>) f.getElementType(0);
			//Class<?> arrayClass = type.getTypeParameters().getClass();
			//this.makeArrayButton(table, "["+i+"]", (Array<?>) arr.get(i), arrayClass, skin, arr);
			
			
			
		}
		else if (type.isAssignableFrom(String.class)){
			Gdx.app.log(TAG, "string Button");
			makeStringArrayElementButton(table, skin, (Array<String>) arr, i);
			ArraySubButton subber = arraySubButtonP.obtain();
			subber.set(arr, table, this, type, parentExpander, i);
			table.add(subber).top().left();
			subber.addListener(expanderListenerP.obtain());
			table.row();
		}  else {
			Gdx.app.log(TAG, "object Button "+type);
			Expander exp = makeObjectButton("["+i+"]", object, table, arr, 1);
			
			
			ArraySubButton subber = arraySubButtonP.obtain();
			subber.set(arr, table, this, type, parentExpander, i);
			
			table.add(subber).top().left().padTop(PAD);
			subber.addListener(expanderListenerP.obtain());
			
			table.row();
		}
		
	}

	private void makeStringArrayElementButton(EditorTable table, Skin skin2,
			Array<String> arr, int i) {
		
		StringArrayElementButton b = stringArrayElementP.obtain();
		Label t = labelP.obtain();
		t.setText("["+i+"]");
		b.set(""+i, arr, i);
		
		//ArraySubButton subber = arraySubButtonP.obtain();
		//subber.set(arr, table, this, type, exp, index);
		table.add(t).top().left();
		table.add(b).top().left().colspan(1);
		//table.add(subber);
		//table.row();
	}

	public void makeForArray(Array<?> o, EditorTable table, int depth, Class type, ArrayExpander expander) {
		for (int i = 0; i < o.size; i++){
			Gdx.app.log(TAG, "writing array element"+o.get(i).getClass());
			makeForArrayElement(o.get(i), table, depth, i, type, o, expander);
			
		}
		
	}

	private void makeArrayButton(EditorTable table, String name, Array<?> o,
			Class<?> arrayClass, Skin skin) {
		
		EditorTable newTable = tableP.obtain();
		newTable.set(table, o, "["+name+"]");
        newTable.setBackground(Styles.tableBack[Math.abs(o.hashCode())%Styles.tableBack.length]);
		ArrayExpander expander = arrayExpanderP.obtain();
		
		boolean shouldExpand = expandedStore.containsKey(o);
		expander.setChecked(!shouldExpand);
		table.add(expander).top().left().padTop(PAD);
		
		
		EditorLabel lab = editorLabelP.obtain();
		newTable.setArray(arrayClass, name);
		newTable.set(table, o, name);
		
		lab.setChecked(true);
		lab.parentObject = o;

		lab.addListener(expanderListenerP.obtain());
		
		
		expander.set(o, newTable, this, arrayClass, lab);
		expander.addListener(expanderListenerP.obtain());
		newTable.add(lab).colspan(3).top();
		lab.setText("["+name+"]");
		
		ArrayAddButton addButton = arrayAddButtonP.obtain();
		addButton.set(o, newTable, this, arrayClass, expander);
		addButton.setChecked(true);
		addButton.addListener(expanderListenerP.obtain());
		//table.add(addButton);
		//addButton.setText();
		newTable.add(addButton).top().left();
		newTable.row();
		table.add(newTable).left().top().pad(PAD);
		//table.row();
		
		
		//if (expandedStore.containsKey(o)){
		//expander.apply();
		//if (shouldExpand) expander.expand();
		if (shouldExpand){
			this.makeForArray(o, newTable, 0, arrayClass, expander);
			expander.setCheckedWithoutChanging(false);
		}
		//}
		///expander.setText();
		
	}

	private void makeStringButton(Table table, String name, Field f, Object o,
			Skin skin) {
		StringButton b = stringP.obtain();
		Label t = labelP.obtain();
		t.setText(name);
		b.set(name, f, o);
		table.add(actorP.obtain()).top().left();
		EditorTable tab = tableP.obtain();
		tab.add(t).top().left();
		
		tab.add(b).top().left();
		table.add(tab).top().left();
		table.row();
		
	}

	private Expander makeObjectButton(String name, Object object, EditorTable table, Object parent, int depth) {
		EditorTable newTable = tableP.obtain();
		newTable.set(table, parent, name);
		newTable.setBackground(Styles.tableBack[object.hashCode()%Styles.tableBack.length]);
		Expander expander = expanderP.obtain();
		//expander.clear();
		
		boolean shouldExpand = expandedStore.containsKey(object);
		//if (shouldExpand)throw new GdxRuntimeException("hh");
		expander.setChecked(!shouldExpand);
		
		expander.addListener(expanderListenerP.obtain());
		
		if (depth != 0) table.add(expander).top().right().padTop(PAD);
		
		//ArraySubButton subber = arraySubButtonP.obtain();
		
		EditorLabel lab = editorLabelP.obtain();
		lab.setChecked(true);
		lab.addListener(expanderListenerP.obtain());
		lab.parentObject = parent;
		newTable.set(name);
		expander.set(object, newTable, this, lab);
		newTable.set(table, object, name);
		if (depth != 0){
			newTable.add(lab).colspan(3);
			lab.setText(name);
			//newTable.add(subber);
			newTable.row();
			
		}
		
		table.add(newTable).left().pad(PAD);//.colspan(2);;
		//table.add(new Label("x", skin)).expand();
		//table.row();
		
		//if (shouldExpand){
		//expander.apply();
			
		if (shouldExpand){
			this.makeFor(object, newTable, 0);
			expander.setCheckedWithoutChanging(false);
		}
		//expander.contract();
		//expander.expand();
		//
		//expander.setChecked(true);
		//expander.setChecked(false);;
		//}
		//expander.setText();
		return expander;
	}

	public void contract(Expander expander) {
		contractQueue.add(expander);
		
	}

	public void contract(ArrayExpander expander) {
		// TODO Auto-generated method stub
		arrayContractQueue.add(expander);
	}

	public void queueRefresh(ArrayExpander expander) {
		arrayExpandQueue.add(expander);
		arrayContractQueue.add(expander);
	}

	private void makeBooleanButton(Table table, String name, Field f, Object o,
			Skin skin) {
		BooleanButton b = booleanP.obtain();
		Label t = labelP.obtain();

		b.set(name, f, o);
		table.add(actorP.obtain());//.top().left();
		table.add(b).top().left();
		table.row();
		
		//f.set(o, 1);
		//Gdx.app.log("editorscr", "made bool button");
		
	}



	private void makeIntegerButton(Table table, String name, Field f, Object o,
			Skin skin) {
		IntegerButton b = integerP.obtain();
		Label t = labelP.obtain();
		t.setText(f.getName().replace("_", " "));

		b.set(name, f, o);
		EditorTable tab = tableP.obtain();
		tab.add(t).top().left();
		
		tab.add(b).top().left();
		table.add(actorP.obtain()).top().left();
		table.add(tab).top().left();
		table.row();
		
		//f.set(o, 1);
		//Gdx.app.log("editorscr", "made int button");
		
	}



	private void makeFloatButton(Table table, String name, Field f, Object o, Skin skin) {
		final FloatButton b = floatP.obtain();
		Label t = labelP.obtain();
		t.setText(f.getName().replace("_", " "));
		b.set(name, f, o);
		EditorTable tab = tableP.obtain();
		//tab.setBackground(Main.tableBack[o.hashCode()%Main.tableBack.length]);
		tab.add(t).top().left();
		
		tab.add(b).top().left();
		table.add(actorP.obtain()).top().left();
		//tab.add(t).top().left();
		//tab.add(b).top().left();
		
		Annotation[] anns = f.getDeclaredAnnotations();
		for (int i = 0; i < anns.length; i++){
			Gdx.app.log(TAG,  "ANNOT "+anns[i]);
		}
		if (f.getDeclaredAnnotation(Range.class) != null){
			Annotation r = f.getDeclaredAnnotation(Range.class);
			Range an = r.getAnnotation(Range.class);
			float min = an.min();
			
			FloatSlider slider = sliderP.obtain();
			slider.addListener(sliderListenerP.obtain());
			slider.set(an.min(), an.max(), b);
			tab.add(slider);
			//tab.add(labelP.obtain());
			Gdx.app.log(TAG, "ANNOTATION");
            tab.row();
			
		}
		
		table.add(tab).left();
		table.row();
		//f.set(o, 1);
		//Gdx.app.log("editorscr", "made float button");
	}

	public void rememberExpanded(Object self) {
		expandedStore.put(self, "");
	}
	
	public void clearExpanded(){
		expandedStore.clear();
	}
	
	@Override
	public void onNotify(Entity e, com.niz.observer.Subject.Event event,
			Object c) {
		BooleanInput b = (BooleanInput) c;
		if (!b.value && on){
			if (backMethod != null)
			try {
				backMethod.invoke(stack.peek().self);
			} catch (ReflectionException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
				Gdx.app.log(TAG, "failed to invoke back()"+e1.getMessage());
			}
			close();
			b.value = true;
			invScreen.on = true;
			
		}
	}


	private void close() {
		theStage.clear();
		invScreen.addTo(theStage);
		on = false;
	}

	

	

	
}
