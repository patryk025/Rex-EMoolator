package pl.cba.genszu.amcodetranslator.utils;

//https://stackoverflow.com/a/1098153

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.*;

public class ReverseListIterator<T> implements Iterable<T>
{

	// to żeby AIDE zamknęło... terminal
	@Override
	public Spliterator<T> spliterator()
	{
		// TODO: Implement this method
		return null;
	}

    private final List<T> original;

    public ReverseListIterator(List<T> original) {
        this.original = original;
    }

    public Iterator<T> iterator() {
        final ListIterator<T> i = original.listIterator(original.size());

        return new Iterator<T>() {
            public boolean hasNext() { return i.hasPrevious(); }
            public T next() { return i.previous(); }
            public void remove() { i.remove(); }
        };
    }

    public static <T> ReverseListIterator<T> reversed(List<T> original) {
        return new ReverseListIterator<T>(original);
    }
}
