package com.niz;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pools;
import com.niz.action.Action;
import com.niz.action.BlankAction;


/** Doubly linked list with nodes of type Action storing strings. */
public class DoublyLinkedList {
  
  protected int size;   // number of elements
  protected Action header, trailer; // sentinels
  
  /** Constructor that creates an empty list */
  public DoublyLinkedList() { 
    size = 0;
    header = new BlankAction(); // create header
    trailer = new BlankAction(); // create trailer
    trailer.setPrev(header);
    header.setNext(trailer); // make header and trailer point to each other
  }
  
  public <T extends Action> T get(Class<T> type){
	  Action a = header;
	  while (a.next != trailer){
		  a = a.next;
		  if (type.isAssignableFrom(a.getClass())){
			  return (T)a;
		  }
	  }
	  
	  return null;
  }
  
  /** Returns the number of elements in the list */
  public int size() { return size; }
  
  /** Returns whether the list is empty */
  public boolean isEmpty() { return (size == 0); }
  /** Returns the first node of the list */
  
  public Action getFirst() throws IllegalStateException {
    if (isEmpty()) throw new IllegalStateException("List is empty");
    return header.getNext();
  }
  
  /** Returns the last node of the list */
  public Action getLast() throws IllegalStateException {
    if (isEmpty()) throw new IllegalStateException("List is empty");
    return trailer.getPrev();
  }
  
  /** Returns the node before the given node v. An error occurs if v
    * is the header */
  public Action getPrev(Action v) throws IllegalArgumentException {
    if (v == header) throw new IllegalArgumentException
      ("Cannot move back past the header of the list");
    return v.getPrev();
  }
  
  /** Returns the node after the given node v. An error occurs if v
    * is the trailer */
  public Action getNext(Action v) throws IllegalArgumentException {
    if (v == trailer) throw new IllegalArgumentException
      ("Cannot move forward past the trailer of the list");
   return v.getNext();
  }
  
  
  /** Inserts the given node z before the given node v. An error
    * occurs if v is the header */
  public void addBefore(Action v, Action z) throws IllegalArgumentException {
    Action u = getPrev(v); // may throw an IllegalArgumentException
    z.setPrev(u);
    z.setNext(v);
    v.setPrev(z);
    u.setNext(z);
    size++;
  }

  /** Inserts the given node z after the given node v. An error occurs
    * if v is the trailer */
  public void addAfter(Action v, Action z) {
    Action w = getNext(v); // may throw an IllegalArgumentException
    z.setPrev(v);
    z.setNext(w);
    w.setPrev(z);
    v.setNext(z);
    size++;
  }
  
  /** Inserts the given node at the head of the list */
  public void addFirst(Action v) {
    addAfter(header, v);
  }
  
  /** Inserts the given node at the tail of the list */
  public void addLast(Action v) {
    addBefore(trailer, v);
  }
  
  /** Removes the given node v from the list. An error occurs if v is
    * the header or trailer */
  public void remove(Action v) {
    Action u = getPrev(v); // may throw an IllegalArgumentException
    Action w = getNext(v); // may throw an IllegalArgumentException
    // unlink the node from the list 
    w.setPrev(u);
    u.setNext(w);
    v.setPrev(null);
    v.setNext(null);
    size--;
  }
  
  /** Returns whether a given node has a previous node */
  public boolean hasPrev(Action v) { return v != header; }
  
  /** Returns whether a given node has a next node */
  public boolean hasNext(Action v) { return v != trailer; }

public void clear() {
	 Action a = header;
	  while (a.next != trailer){
		  a = a.next;
		  this.remove(a);
		  
	  }
}
  
  /** Returns a string representation of the list */
  /*public String toString() {
    String s = "[";
    Action v = header.getNext();
    while (v != trailer) {
      s += v.getElement();
      v = v.getNext();
      if (v != trailer)
        s += ",";
    }
    s += "]";
    return s;
  }*/
  
  /***********************************************************************
    * Insertion-sort for a doubly linked list of class DList.  
    **********************************************************************/
  
  /*public static void sort(DList L) {
    if (L.size() <= 1) 
      return; // L is already sorted in this case
    Action pivot; // pivot node 
    Action ins;  // insertion point 
    Action end = L.getFirst(); // end of run
    
    while (end != L.getLast()) {
      pivot = end.getNext(); // get the next pivot node
      L.remove(pivot);  // remove it
      ins = end;  // start searching from the end of the sorted run
    
      while (L.hasPrev(ins) && ins.getElement().compareTo(pivot.getElement()) > 0)
        ins = ins.getPrev(); // move left
      
      L.addAfter(ins,pivot); // add the pivot back, after insertion point
      if (ins == end)  // we just added pivot after end in this case
        end = end.getNext(); // so increment the end marker
    }
  }
*/

  
  
  
  
}