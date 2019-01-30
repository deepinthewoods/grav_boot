/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.ashley.core;

import java.util.Comparator;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.Engine.ComponentOperationHandler;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.ReflectionPool;
import com.badlogic.gdx.utils.SnapshotArray;
import com.niz.Data;
import com.niz.component.PathfinderPreLog;
import com.niz.observer.Subject;

/**
 * The heart of the Entity framework. It is responsible for keeping track of {@link Entity} and
 * managing {@link EntitySystem} objects. The Engine should be updated every tick via the {@link #update(float)} method.
 *
 * With the Engine you can:
 *
 * <ul>
 * <li>Add/Remove {@link Entity} objects</li>
 * <li>Add/Remove {@link EntitySystem}s</li>
 * <li>Obtain a list of entities for a specific {@link Family}</li>
 * <li>Update the main loop</li>
 * <li>Register/unregister {@link EntityListener} objects</li>
 * </ul>
 *
 * @author Stefan Bachmann
 */
public class EngineNiz extends Engine{
	private static final String TAG = "engine";

	private static SystemComparator comparator = new SystemComparator();

	private Array<Entity> entities;
	private ImmutableArray<Entity> immutableEntities;
	private LongMap<Entity> entitiesById;

	private Array<EntityOperation> entityOperations;
	private EntityOperationPool entityOperationPool;

	private Array<EntitySystem> systems;
	private ImmutableArray<EntitySystem> immutableSystems;
	private ObjectMap<Class<?>, EntitySystem> systemsByClass;

	private ObjectMap<Family, Array<Entity>> families;
	private ObjectMap<Family, ImmutableArray<Entity>> immutableFamilies;

	private SnapshotArray<EntityListener> entityListeners;
	private ObjectMap<Family,SnapshotArray<EntityListener>> familyListeners;

	private final Listener<Entity> componentAdded;
	private final Listener<Entity> componentRemoved;

	private boolean updating;

	private boolean notifying;
	private long nextEntityId = 1;

	/** Mechanism to delay component addition/removal to avoid affecting system processing */
	private ComponentOperationPool componentOperationsPool;
 	private Array<ComponentOperation> componentOperations;
 	private com.badlogic.ashley.core.Engine.ComponentOperationHandler componentOperationHandler;

 	private EntityPool entityPool;
	private ComponentPools componentPools;
	
	public long tick;
	
	public boolean debug = !true;
	
	public boolean simulating = true;

 	public EngineNiz(){
 		this(10,100,10,100);
 	}
	
	public EngineNiz(int entityPoolInitialSize, int entityPoolMaxSize, int componentPoolInitialSize, int componentPoolMaxSize){
		entityPool = new EntityPool(entityPoolInitialSize, entityPoolMaxSize, this);
		componentPools = new ComponentPools(componentPoolInitialSize, componentPoolMaxSize);
		entities = new Array<Entity>(false, 16);
		immutableEntities = new ImmutableArray<Entity>(entities);
		entitiesById = new LongMap<Entity>();
		entityOperations = new Array<EntityOperation>(false, 16);
		entityOperationPool = new EntityOperationPool();
		systems = new Array<EntitySystem>(false, 16);
		immutableSystems = new ImmutableArray<EntitySystem>(systems);
		systemsByClass = new ObjectMap<Class<?>, EntitySystem>();
		families = new ObjectMap<Family, Array<Entity>>();
		immutableFamilies = new ObjectMap<Family, ImmutableArray<Entity>>();
		entityListeners = new SnapshotArray<EntityListener>(false, 16);
		familyListeners = new ObjectMap<Family,SnapshotArray<EntityListener>>();

		componentAdded = new ComponentListener(this);
		componentRemoved = new ComponentListener(this);

		updating = false;
		notifying = false;

		componentOperationsPool = new ComponentOperationPool();
		componentOperations = new Array<ComponentOperation>();
		componentOperationHandler = new com.badlogic.ashley.core.Engine.ComponentOperationHandler(this);
	}

	private long obtainEntityId() {
		return nextEntityId++;
	}
	
	public void setNextEntityId(long id){
		nextEntityId = id;
	}
	
	public long getNextEntityId(){
		return nextEntityId;
	}

	/**
	 * Adds an entity to this Engine.
	 */
	public void addEntity(Entity entity){
		entity.uuid = obtainEntityId();
		if (updating || notifying) {
			EntityOperation operation = entityOperationPool.obtain();
			operation.entity = entity;
			operation.type = EntityOperation.Type.Add;
			entityOperations.add(operation);
		}
		else {
			addEntityInternal(entity);
		}
	}
	/**
	 * Adds an entity to this Engine, without setting ID
	 */
	public void addEntityNoID(Entity entity){
		//entity.uuid = id;
		if (updating || notifying) {
			EntityOperation operation = entityOperationPool.obtain();
			operation.entity = entity;
			operation.type = EntityOperation.Type.Add;
			entityOperations.add(operation);
		}
		else {
			addEntityInternal(entity);
		}
	}
	
	public void removeEntityNoPool(PooledEntity e){
		e.poolingDisabled = true;
		removeEntity(e);
	}

	/**
	 * Removes an entity from this Engine.
	 */
	public void removeEntity(Entity entity){
		if (updating || notifying) {
			if(entity.scheduledForRemoval) {
				return;
			}
			entity.scheduledForRemoval = true;
			EntityOperation operation = entityOperationPool.obtain();
			operation.entity = entity;
			operation.type = EntityOperation.Type.Remove;
			entityOperations.add(operation);
		}
		else {
			removeEntityInternal(entity);
		}
	}

	/**
	 * Removes all entities registered with this Engine.
	 */
	public void removeAllEntities() {
		if (updating || notifying) {
			for(Entity entity: entities) {
				entity.scheduledForRemoval = true;
			}
			EntityOperation operation = entityOperationPool.obtain();
			operation.type = EntityOperation.Type.RemoveAll;
			entityOperations.add(operation);
		}
		else {
			while(entities.size > 0) {
				removeEntity(entities.first());
			}
		}
	}

	public Entity getEntity(long id) {
		return entitiesById.get(id);
	}

	public ImmutableArray<Entity> getEntities() {
		return immutableEntities;
	}

	/**
	 * Adds the {@link EntitySystem} to this Engine.
	 */
	public void addSystem(EntitySystem system){
		Class<? extends EntitySystem> systemType = system.getClass();

		if (!systemsByClass.containsKey(systemType)) {
			systems.add(system);
			systemsByClass.put(systemType, system);
			system.addedToEngine(this);

			systems.sort(comparator);
		}
	}

	/**
	 * Removes the {@link EntitySystem} from this Engine.
	 */
	public void removeSystem(EntitySystem system){
		if(systems.removeValue(system, true)) {
			systemsByClass.remove(system.getClass());
			system.removedFromEngine(this);
		}
	}

	/**
	 * Quick {@link EntitySystem} retrieval.
	 */
	@SuppressWarnings("unchecked")
	public <T extends EntitySystem> T getSystem(Class<T> systemType) {
		return (T) systemsByClass.get(systemType);
	}

	/**
	 * @return immutable array of all entity systems managed by the {@link EngineNiz}.
	 */
	public ImmutableArray<EntitySystem> getSystems() {
		return immutableSystems;
	}

	/**
	 * Returns immutable collection of entities for the specified {@link Family}. Will return the same instance every time.
	 */
	public ImmutableArray<Entity> getEntitiesFor(Family family){
		return registerFamily(family);
	}

	/**
	 * Adds an {@link EntityListener}.
	 *
	 * The listener will be notified every time an entity is added/removed to/from the engine.
	 */
	public void addEntityListener(EntityListener listener) {
		entityListeners.add(listener);
	}

	/**
	 * Adds an {@link EntityListener} for a specific {@link Family}.
	 *
	 * The listener will be notified every time an entity is added/removed to/from the given family.
	 */
	public void addEntityListener(Family family, EntityListener listener) {
		registerFamily(family);
		SnapshotArray<EntityListener> listeners = familyListeners.get(family);

		if (listeners == null) {
			listeners = new SnapshotArray<EntityListener>(false, 16);
			familyListeners.put(family, listeners);
		}

		listeners.add(listener);
	}

	/**
	 * Removes an {@link EntityListener}
	 */
	public void removeEntityListener(EntityListener listener) {
		entityListeners.removeValue(listener, true);

		for (SnapshotArray<EntityListener> familyListenerArray : familyListeners.values()) {
			familyListenerArray.removeValue(listener, true);
		}
	}

	/**
	 * Updates all the systems in this Engine.
	 * @param deltaTime The time passed since the last frame.
	 */
	public void update(float deltaTime){
		updating = true;
		for(int i=0; i<systems.size; i++){
			EntitySystem system = systems.get(i);
			if (system.checkProcessing()) {
				if (debug)Gdx.app.log(TAG, "updating "+system.getClass());
				if (system instanceof RenderSystem == render)system.update(deltaTime);
			}

			processComponentOperations();
			processPendingEntityOperations();
		}

		updating = false;
	}

	private void updateFamilyMembership(Entity entity){
		for (Entry<Family, Array<Entity>> entry : families.entries()) {
			Family family = entry.key;
			Array<Entity> familyEntities = entry.value;
			int familyIndex = family.getIndex();


			boolean belongsToFamily = entity.getFamilyBits().get(familyIndex);
			boolean matches = family.matches(entity);

			if (!belongsToFamily && matches) {
				familyEntities.add(entity);
				entity.getFamilyBits().set(familyIndex);

				notifyFamilyListenersAdd(family, entity);
			}
			else if (belongsToFamily && !matches) {
				familyEntities.removeValue(entity, true);
				entity.getFamilyBits().clear(familyIndex);

				notifyFamilyListenersRemove(family, entity);
			}
		}
	}


	protected void addEntityInternal(Entity entity) {
		entities.add(entity);
		entitiesById.put(entity.getId(), entity);

		updateFamilyMembership(entity);

		entity.componentAdded.add(componentAdded);
		entity.componentRemoved.add(componentRemoved);
		entity.componentOperationHandler = componentOperationHandler;

		notifying = true;
		Object[] items = entityListeners.begin();
		for (int i = 0, n = entityListeners.size; i < n; i++) {
			EntityListener listener = (EntityListener)items[i];
			listener.entityAdded(entity);
		}
		entityListeners.end();
		notifying = false;
	}

	private void notifyFamilyListenersAdd(Family family, Entity entity) {
		SnapshotArray<EntityListener> listeners = familyListeners.get(family);

		if (listeners != null) {
			notifying = true;
			Object[] items = listeners.begin();
			for (int i = 0, n = listeners.size; i < n; i++) {
				EntityListener listener = (EntityListener)items[i];
				listener.entityAdded(entity);
			}
			listeners.end();
			notifying = false;
		}
	}

	private void notifyFamilyListenersRemove(Family family, Entity entity) {
		SnapshotArray<EntityListener> listeners = familyListeners.get(family);

		if (listeners != null) {
			notifying = true;
			Object[] items = listeners.begin();
			for (int i = 0, n = listeners.size; i < n; i++) {
				EntityListener listener = (EntityListener)items[i];
				listener.entityRemoved(entity);
			}
			listeners.end();
			notifying = false;
		}
	}

	private ImmutableArray<Entity> registerFamily(Family family) {
		ImmutableArray<Entity> immutableEntitiesInFamily = immutableFamilies.get(family);

		if (immutableEntitiesInFamily == null) {
			Array<Entity> familyEntities = new Array<Entity>(false, 16);
			immutableEntitiesInFamily = new ImmutableArray<Entity>(familyEntities);
			families.put(family, familyEntities);
			immutableFamilies.put(family, immutableEntitiesInFamily);

			for(Entity e : this.entities){
				if(family.matches(e)) {
					familyEntities.add(e);
					e.getFamilyBits().set(family.getIndex());
				}
			}
		}

		return immutableEntitiesInFamily;
	}

	private void processPendingEntityOperations() {
		while (entityOperations.size > 0) {
			EntityOperation operation = entityOperations.removeIndex(entityOperations.size - 1);

			switch(operation.type) {
				case Add: addEntityInternal(operation.entity); break;
				case Remove: removeEntityInternal(operation.entity); break;
				case RemoveAll:
					while(entities.size > 0) {
						removeEntityInternal(entities.first());
					}
					break;
				default:
					throw new AssertionError("Unexpected EntityOperation type");
			}

			entityOperationPool.free(operation);
		}

		entityOperations.clear();
	}

	private void processComponentOperations() {
		for (int i = 0; i < componentOperations.size; ++i) {
			ComponentOperation operation = componentOperations.get(i);

			switch(operation.type) {
				case Add: operation.entity.addInternal(operation.component); break;
				case Remove: operation.entity.removeInternal(operation.componentClass); break;
				default: break;
			}

			componentOperationsPool.free(operation);
		}

		componentOperations.clear();
	}

	
	
	/** @return Clean {@link Entity} from the Engine pool. In order to add it to the {@link Engine}, use {@link #addEntity(Entity)}. */
	public PooledEntity createEntity () {
		return entityPool.obtain();
	}

	/**
	 * Retrieves a new {@link Component} from the {@link Engine} pool. It will be placed back in the pool whenever it's removed
	 * from an {@link Entity} or the {@link Entity} itself it's removed.
	 */
	public <T extends Component> T createComponent (Class<T> componentType) {
		return componentPools.obtain(componentType);
	}

	/**
	 * Removes all free entities and components from their pools. Although this will likely result in garbage collection, it will
	 * free up memory.
	 */
	public void clearPools () {
		entityPool.clear();
		componentPools.clear();
	}

	@Override
	protected void removeEntityInternal (Entity entity) {
		// Check if entity is able to be removed (id == 0 means either entity is not used by engine, or already removed/in pool)
		if (entity.getId() == 0) return;
		//Gdx.app.log(TAG, "remove" + entity.getId() + ((PooledEntity)entity).poolingDisabled);

		entity.scheduledForRemoval = false;
		entities.removeValue(entity, true);
		entitiesById.remove(entity.getId());

		if(!entity.getFamilyBits().isEmpty()){
			for (Entry<Family, Array<Entity>> entry : families.entries()) {
				Family family = entry.key;
				Array<Entity> familyEntities = entry.value;

				if(family.matches(entity)){
					familyEntities.removeValue(entity, true);
					entity.getFamilyBits().clear(family.getIndex());
					notifyFamilyListenersRemove(family, entity);
				}
			}
		}

		entity.componentAdded.remove(componentAdded);
		entity.componentRemoved.remove(componentRemoved);
		entity.componentOperationHandler = null;

		notifying = true;
		Object[] items = entityListeners.begin();
		for (int i = 0, n = entityListeners.size; i < n; i++) {
			EntityListener listener = (EntityListener)items[i];
			listener.entityRemoved(entity);
		}
		entityListeners.end();
		notifying = false;
	

		if (entity instanceof PooledEntity && !((PooledEntity) entity).poolingDisabled) {
			entityPool.free((PooledEntity)entity);
		}
	}

	public class PooledEntity extends Entity implements Poolable {
		public int seed;
		private boolean poolingDisabled;
		public EngineNiz engine;
		public PooledEntity(EngineNiz engine) {
			this.engine = engine;
		}

		@Override
		Component removeInternal (Class<? extends Component> componentType) {
			if (poolingDisabled) return getComponent(componentType);
			Component component = super.removeInternal(componentType);

			if (component != null) {
				componentPools.free(component);
			}

			return component;
		}

		@Override
		public void reset () {
			poolingDisabled = false;
			removeAll();
			uuid = 0L;
			flags = 0;
			componentAdded.removeAllListeners();
			componentRemoved.removeAllListeners();
			scheduledForRemoval = false;
		}

		public void setUUID(long eID) {
			uuid = eID;
		}

		public <T extends Component> T add(Class<T> class1) {
			T c = engine.createComponent(class1);
			add(c);
			return c;
		}
	}

	private class EntityPool extends Pool<PooledEntity> {

		public EngineNiz engine;

		public EntityPool (int initialSize, int maxSize, EngineNiz engine) {
			super(initialSize, maxSize);
			this.engine = engine;
		}

		@Override
		protected PooledEntity newObject () {
			return new PooledEntity(engine);
		}
	}

	private class ComponentPools {
		private ObjectMap<Class<?>, ReflectionPool> pools;
		private int initialSize;
		private int maxSize;

		public ComponentPools (int initialSize, int maxSize) {
			this.pools = new ObjectMap<Class<?>, ReflectionPool>();
			this.initialSize = initialSize;
			this.maxSize = maxSize;
		}

		public <T> T obtain (Class<T> type) {
			ReflectionPool pool = pools.get(type);

			if (pool == null) {
				pool = new ReflectionPool(type, initialSize, maxSize);
				pools.put(type, pool);
			}

			return (T)pool.obtain();
		}

		public void free (Object object) {
			if (object == null) {
				throw new IllegalArgumentException("object cannot be null.");
			}

			ReflectionPool pool = pools.get(object.getClass());

			if (pool == null) {
				return; // Ignore freeing an object that was never retained.
			}

			pool.free(object);
		}

		public void freeAll (Array objects) {
			if (objects == null) throw new IllegalArgumentException("objects cannot be null.");

			for (int i = 0, n = objects.size; i < n; i++) {
				Object object = objects.get(i);
				if (object == null) continue;
				free(object);
			}
		}

		public void clear () {
			for (Pool pool : pools.values()) {
				pool.clear();
			}
		}
	}
	
	
	
	
	
	private static class ComponentListener implements Listener<Entity> {
		private EngineNiz engine;

		public ComponentListener(EngineNiz engine) {
			this.engine = engine;
		}

		@Override
		public void receive(Signal<Entity> signal, Entity object) {
			engine.updateFamilyMembership(object);
		}
	}

	static class ComponentOperationHandler {
		private EngineNiz engine;

		public ComponentOperationHandler(EngineNiz engine) {
			this.engine = engine;
		}

		public void add(Entity entity, Component component) {
			if (engine.updating) {
				ComponentOperation operation = engine.componentOperationsPool.obtain();
				operation.makeAdd(entity, component);
				engine.componentOperations.add(operation);
			}
			else {
				entity.addInternal(component);
			}
		}

		public void remove(Entity entity, Class<? extends Component> componentClass) {
			if (engine.updating) {
				ComponentOperation operation = engine.componentOperationsPool.obtain();
				operation.makeRemove(entity, componentClass);
				engine.componentOperations.add(operation);
			}
			else {
				entity.removeInternal(componentClass);
			}
		}
	}

	private static class ComponentOperation implements Pool.Poolable {
		public enum Type {
			Add,
			Remove,
		}

		public Type type;
		public Entity entity;
		public Component component;
		public Class<? extends Component> componentClass;

		public void makeAdd(Entity entity, Component component) {
			this.type = Type.Add;
			this.entity = entity;
			this.component = component;
			this.componentClass = null;
		}

		public void makeRemove(Entity entity, Class<? extends Component> componentClass) {
			this.type = Type.Remove;
			this.entity = entity;
			this.component = null;
			this.componentClass = componentClass;
		}

		@Override
		public void reset() {
			entity = null;
			component = null;
		}
	}

	private static class ComponentOperationPool extends Pool<ComponentOperation> {
		@Override
		protected ComponentOperation newObject() {
			return new ComponentOperation();
		}
	}

	private static class SystemComparator implements Comparator<EntitySystem>{
		@Override
		public int compare(EntitySystem a, EntitySystem b) {
			return a.priority > b.priority ? 1 : (a.priority == b.priority) ? 0 : -1;
		}
	}

	private static class EntityOperation implements Pool.Poolable {
		public enum Type {
			Add,
			Remove,
			RemoveAll
		}

		public Type type;
		public Entity entity;

		@Override
		public void reset() {
			entity = null;
		}
	}

	private static class EntityOperationPool extends Pool<EntityOperation> {
		@Override
		protected EntityOperation newObject() {
			return new EntityOperation();
		}
	}

	//*/
	
	private IntMap<Subject> subjects = new IntMap<Subject>();

	private boolean render;

    public Subject getSubject(String string){
        return getSubject(Data.hash(string));
    }

    public Subject getSubject(int hash){
        if (subjects.containsKey(hash)) return subjects.get(hash);
        Subject sub = new Subject();
        subjects.put(hash, sub);
        return sub;
    }

	public void render(float deltaTime) {
		render = true;
		update(deltaTime);
		render = false;
	}

	public void freeEntity(PooledEntity e) {
		entityPool.free(e);
	}

	public void removeEntity(long id) {
		Entity e = getEntity(id);
		if (e != null)
			removeEntity(e);
	}

	public void dispose() {
		for(int i=0; i<systems.size; i++){
			EntitySystem system = systems.get(i);
			if (system instanceof IDisposeable) ((IDisposeable) system).dispose();
			
		}
	}

	public <T extends Component> Pool<T> getPool(Class<T> cl) {
		
		return componentPools.pools.get(cl);
	}

	
}
