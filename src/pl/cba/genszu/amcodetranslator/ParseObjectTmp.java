package pl.cba.genszu.amcodetranslator;

import java.util.*;

public class ParseObjectTmp
{
	public String typeName;
	public List<String> fields;
	public List<String> methods;
	
	public ParseObjectTmp(String name) {
		this.typeName = name;
		this.fields = new ArrayList<>();
		this.methods = new ArrayList<>();
	}
	
	public void removeDuplicates() {
		this.fields = new ArrayList<String>(new HashSet<String>(this.fields));
		this.methods = new ArrayList<String>(new HashSet<String>(this.methods));
	}
}
