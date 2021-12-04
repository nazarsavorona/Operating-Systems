package lab3;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

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

    public static void main(String[] args) {
        List<Page> circular = new ArrayList<>();
        CircularIterator<Page> iterator = new CircularIterator<>(circular);
        circular.addAll(List.of(new Page(0, 0, (byte) 0, (byte) 0, 0, 0, 0L, 0L)));
        iterator.get().id = 1;
////s.id = 1;
////iterator.set(s);
//        System.out.println(iterator.get());
////        circular.add("new");
//        System.out.println(iterator.next());
//        System.out.println(iterator.next());
//        System.out.println(iterator.next());
////        circular.remove("new");
////        iterator.set("lel");
//        System.out.println(iterator.next());
//        System.out.println(iterator.next());
//        System.out.println(iterator.next());
//        System.out.println(iterator.next());
//        System.out.println(iterator.next());
//        System.out.println(iterator.next());
//        System.out.println(iterator.next());
//        Page page = new Page(0, 0, (byte) 0, (byte) 0, 0, 0, 0L, 0L);
//        List<Page> pages = new ArrayList<>();
//        Vector vector = new Vector<Page>();
//        vector.add(page);
//
//        pages.add((Page) vector.get(0));
//        pages.get(0).id = 1;
//        System.out.println(pages);
    }
}

