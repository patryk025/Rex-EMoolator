package pl.cba.genszu.amcodetranslator.lexer.stack;

import pl.cba.genszu.amcodetranslator.interpreter.util.Token;
import pl.cba.genszu.amcodetranslator.lexer.stack.exceptions.StackEmptyException;
import pl.cba.genszu.amcodetranslator.lexer.stack.exceptions.StackOverflowException;

import java.util.Arrays;

public class Stack {
    private Token stack[];
    private int index;
    private int size;

    private int nearest2power(int size) {
        int power = 1;
        while(power < size)
            power*=2;
        return power;
    }

    public Stack(int size)
    {
        size = nearest2power(size); //change to 2 power, for better calculations if needed
        stack = new Token[size];
        index = -1;
    }

    //helper method if needed
    public void resizeStack(int newSize) {
        newSize = nearest2power(newSize);
        Token newStack[] = Arrays.copyOf(stack, newSize);
        size = newSize;
        stack = newStack;
    }

    public void push(Token x) throws StackOverflowException {
        if (isFull())
        {
            throw new StackOverflowException();
        }

        stack[++index] = x;
    }

    public Token pop() throws StackEmptyException {
        if (isEmpty())
        {
            throw new StackEmptyException();
        }

        return stack[index--];
    }

    public Token peek() throws StackEmptyException {
        if (!isEmpty()) {
            return stack[index];
        }
        else {
            throw new StackEmptyException();
        }
    }

    public int size() {
        return index + 1;
    }

    public Boolean isEmpty()
    {
        return size() == 0;
    }

    public Boolean isFull() {
        return size() == size;
    }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = index; i >= 0; i--) {
			sb.append(stack[i]).append("\n");
		} 
		return sb.toString();
	}
}
