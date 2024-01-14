package com.quantumforge.quickdial.session;


import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class BackwardNavigableList<E> extends ArrayList<E> {

    private int currentNavigationIndex;

    public BackwardNavigableList(){
        resetCurrentIndex();
    }

    public boolean hasPrevious(){
        if(isEmpty()){ return false; }
        return currentNavigationIndex - 1 >= 0;
    }

    public boolean hasNext(){
        if(isEmpty()){ return false; }
        return currentNavigationIndex < size() - 1;
    }

    public E getPreviousElement(){
        E previousElement;
        if(hasPrevious()){
            previousElement = get(currentNavigationIndex - 1);
            moveCurrentIndexBackward();
        }else {
            previousElement = isEmpty() ? null : get(currentNavigationIndex);
        }
        return previousElement;
    }

    public E getNextElement(){
        E nextElement;
        if(hasNext()){
            nextElement = get(currentNavigationIndex + 1);
            moveCurrentIndexForward();
        }else{
            nextElement = isEmpty() ? null : get(currentNavigationIndex);
        }
        return nextElement;
    }

    public E getLastElement(){
       if(isEmpty()){ return null; }
       return get(size() - 1);
    }

    public E getFirstElement(){
        if(isEmpty()){ return null; }
        return get(0);
    }

    public E getCurrentElement(){
        if(isEmpty()){ return null; }
        return get(currentNavigationIndex);
    }

    @Override
    public boolean add(E element){
        boolean addResult = super.add(element);
        if(addResult)
            moveCurrentIndexForward();
        return addResult;
    }

    @Override
    public void add(int index, E element){
        moveCurrentIndexForward();
        super.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends E> collection){
        AtomicBoolean atomicBoolean = new AtomicBoolean(true);
        collection.forEach(c -> atomicBoolean.set(this.add(c)));
        return atomicBoolean.get();
    }

    public boolean remove(Object element){
        boolean removeResult = super.remove(element);
        if(removeResult)
            resetCurrentIndex();
        return removeResult;
    }

    public E remove(int index){
        E removedElement = super.remove(index);
        resetCurrentIndex();
        return removedElement;
    }

    public boolean addAll(int index, Collection<? extends E> collection){
        AtomicBoolean atomicBoolean = new AtomicBoolean(true);
        AtomicInteger indexCount = new AtomicInteger(index);
        collection.forEach(element -> {
            this.add(indexCount.getAndIncrement(), element);
            atomicBoolean.set(true);
        });
        return atomicBoolean.get();
    }

    @Override
    public E set(int index, E element){
        setCurrentNavigationIndexAtIndex(index);
        return super.set(index, element);
    }

    public void moveCurrentIndexForward(){
        currentNavigationIndex++;
    }

    private void moveCurrentIndexBackward(){
        currentNavigationIndex--;
    }

    private void resetCurrentIndex(){
        currentNavigationIndex = size() - 1;
    }
    public void setCurrentNavigationIndexAtIndex(int index){
        currentNavigationIndex = index;
    }
}
