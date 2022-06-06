package pl.cba.genszu.amcodetranslator.lexer.queue;
import pl.cba.genszu.amcodetranslator.lexer.queue.exceptions.EmptyQueueException;
import pl.cba.genszu.amcodetranslator.lexer.queue.exceptions.FullQueueException;

class Queue
{
    private Object[] queue;
    private int first;
    private int last;
    private int capacity;
    private int counter;

    public Queue(int size)
    {
        queue = new Object[size];
        capacity = size;
        first = 0;
        last = -1;
        counter = 0;
    }
	
	//helper method if needed
    public void resizeQueue(int newSize) {
        Object newQueue[] = new Object[newSize];
		if(newSize < capacity)
			System.out.println("Warning: new size is lower than declared, data loss may occure");
		else if(newSize == capacity) {
			System.out.println("Info: new size equals the old one, nothing to change");
			return;
		}
		
		int index = 0;
		for(int i = first;;index++) {
			newQueue[index] = queue[i];
			if(i == last || index == newSize - 1) break;
			i = (i + 1) % capacity;
		}
        capacity = newSize;
		queue = newQueue;
    }

    public Object dequeue() throws EmptyQueueException
    {
        if (isEmpty())
        {
            throw new EmptyQueueException();
        }

        Object toReturn = queue[first];
        first = (first + 1) % capacity;
        counter--;
        return toReturn;
    }
	
    public void enqueue(Object item) throws FullQueueException
    {
        if (isFull())
        {
            throw new FullQueueException();
        }

        last = (last + 1) % capacity;
        queue[last] = item;
        counter++;
    }

    public Object peek() throws EmptyQueueException
    {
        if (isEmpty())
        {
            throw new EmptyQueueException();
        }
        return queue[first];
    }

    public int size() {
        return counter;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean isFull() {
        return size() == capacity;
    }
}
