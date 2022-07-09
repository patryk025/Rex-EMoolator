package pl.cba.genszu.amcodetranslator;

import java.io.*;
import java.util.*;

public class VariableClassToMarkdown
{
	public static void parse() {
		HashMap<String, List<String>> found = new HashMap<>();
		HashMap<String, List<String>> markdown = new HashMap<>();
		
		try
        {
            FileReader reader = new FileReader("/storage/emulated/0/AppProjects/AidemMediaInterpreter/src/pl/cba/genszu/amcodetranslator/interpreter/util/Variable.java");
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;
			//StringBuilder content = new StringBuilder();
            String className = "";
			boolean startParse = false;
            try
            {
                while ((line = bufferedReader.readLine()) != null)
				{
					//content = content.append(line).append("\n");
					if(startParse) {
						if(line.startsWith("\t\t\tcase ")) className = line.split("case ")[1].replace("\"", "").replace(":", "");
						if(line.startsWith("\t\t\t\t\tcase")){
							String methodName = line.split("case ")[1].replace("\"", "").replace(":", "");
							if(!found.containsKey(className)) found.put(className, new ArrayList<String>());
							found.get(className).add(methodName);
						}
						if(line.startsWith("\t\t\tdefault")) startParse = false;
					}
					else {
						if(line.startsWith("\tpublic void setProperty")) startParse = true;
					}
                }
				
				FileReader reader2 = new FileReader("/storage/emulated/0/AppProjects/AidemMediaInterpreter/Parser.md");
				BufferedReader bufferedReader2 = new BufferedReader(reader2);

				
				startParse = false;
				try
				{
					while ((line = bufferedReader2.readLine()) != null)
					{
						//content = content.append(line).append("\n");
						if(startParse) {
							if(line.startsWith("###")) className = line.split("###")[1];
							if(line.startsWith("- [")){
								String methodName = line.split("\\] ")[1];
								if(!markdown.containsKey(className)) markdown.put(className, new ArrayList<String>());
								markdown.get(className).add(methodName);
							}
						}
						else {
							if(line.startsWith("###Animo")) {
								className = "Animo";
								startParse = true;
							}
						}
					}

					//no i zaczynamy konsolidować listy i hashmapy
					//najpierw klucze
					Set<String> foundKeys = found.keySet();
					Set<String> markdownKeys = markdown.keySet();
					
					String[] foundKeysArr = foundKeys.toArray(new String[0]);
					String[] markdownKeysArr = markdownKeys.toArray(new String[0]);
					
					List<String> allKeys = new ArrayList<String>(Arrays.asList(foundKeysArr));
					allKeys.addAll(Arrays.asList(markdownKeysArr));
					
					allKeys = new ArrayList<String>(new HashSet<String>(allKeys));
					
					Collections.sort(allKeys);
					
					//zaczynamy iterowanie po kluczach
					for(String key : allKeys) {
						List<String> fields = new ArrayList<>();
						
						if(found.containsKey(key)) {
							fields.addAll(found.get(key));
						}
						if(markdown.containsKey(key)) {
							fields.addAll(markdown.get(key));
						}
						
						fields = new ArrayList<String>(new HashSet<String>(fields));
						
						Collections.sort(fields);
						
						String lastField = "";
						System.out.println("###"+key);
						for(String field : fields) {
							if(field.endsWith("^param")) 
								continue;
							if(field.endsWith("*")) {
								if(field.substring(0, field.length()-1).equals(lastField))
									continue;
							}
							if(found.containsKey(key) && markdown.containsKey(key)) {
								if(found.get(key).contains(field.replace("*", ""))) {
									System.out.println("- [x] "+field);
								}
								else {
									System.out.println("- [ ] "+field);
								}
							}
							else if(found.containsKey(key)) {
								System.out.println("- [x] "+field);
							}
							else {
								System.out.println("- [ ] "+field);
							}
							lastField = field;
						}
						System.out.println();
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
            }
            catch (IOException e)
			{
                e.printStackTrace();
            }
        }
        catch (FileNotFoundException e)
		{
            e.printStackTrace();
        }
	}
	
	/*public static void main(String[] args) {
		parse();
	}*/
}
