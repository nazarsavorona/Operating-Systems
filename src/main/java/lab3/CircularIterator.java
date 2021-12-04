package lab3;

import java.util.List;
import java.util.ListIterator;

public class CircularIterator<T> implements ListIterator<T> {
    private final List<T> list;
    private int i;

    public CircularIterator(List<T> list) {
        this.list = list;
        this.i = 0;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public T next() {
        i = nextIndex();
        return list.get(previousIndex());
    }

    @Override
    public boolean hasPrevious() {
        return true;
    }

    @Override
    public T previous() {
        i = i % list.size();
        i = previousIndex();
        return list.get(nextIndex());
    }

    @Override
    public int nextIndex() {
        return (i + 1) % list.size();
    }

    @Override
    public int previousIndex() {
        return (list.size() + i - 1) % list.size();
    }

    @Override
    public void remove() {
        list.remove(i);
    }

    @Override
    public void set(T t) {
        list.set(i, t);
    }

    @Override
    public void add(T t) {
        list.add(i, t);
    }

    public T get() {
        return list.get(i);
    }

    public int index() {
        return i;
    }

    public void setIndex(int index) {
        if (index >= 0 && index < list.size()) {
            i = index;
        }
    }
}

