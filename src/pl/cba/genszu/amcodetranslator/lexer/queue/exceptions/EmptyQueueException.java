package pl.cba.genszu.amcodetranslator.lexer.queue.exceptions;

public class EmptyQueueException extends Exception
{
	public EmptyQueueException() {
		super("Kolejka jest pusta");
	}
}
