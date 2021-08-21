package pl.cba.genszu.amcodetranslator.lexer.queue.exceptions;

public class FullQueueException extends Exception
{
	public FullQueueException() {
		super("Kolejka jest pełna");
	}
}
