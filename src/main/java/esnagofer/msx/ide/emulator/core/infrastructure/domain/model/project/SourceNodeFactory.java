package esnagofer.msx.ide.emulator.core.infrastructure.domain.model.project;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import esnagofer.msx.ide.emulator.core.domain.model.project.SourceNode;
import esnagofer.msx.ide.lib.Validate;

public class SourceNodeFactory {

	protected String getFileContent(File file) {
        String content = "";
        try{
            content = new String (Files.readAllBytes(Paths.get(file.getCanonicalPath())));
        } catch (Exception e) {
        	throw new RuntimeException(e);
        }
        return content;		
	}
	
   protected String fileExtension(File file) {
      String name = file.getName();
      if(name.lastIndexOf(".") != -1 && name.lastIndexOf(".") != 0)
         return name.substring(name.lastIndexOf(".") + 1);
      else
         return "";
   }
	
	protected List<SourceNode> get(File node) {
		List<SourceNode> nodes = new ArrayList<>();
		try {
			File[] files = node.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					List<SourceNode> dirContent = get(file);
					if (!dirContent.isEmpty()) {
						System.out.println(file.getCanonicalPath());
						nodes.add(
							SourceNode.containerOf(
								file.getCanonicalPath(), 
								file.getName(), 
								dirContent
							)
						);
						
					}
				} else {
					if ("asm".equals(fileExtension(file))) {
						System.out.println(file.getCanonicalPath());
						nodes.add(
							SourceNode.contentOf(
								file.getCanonicalPath(), 
								file.getName(), 
								getFileContent(file), 
								new ArrayList<>()
							)
						);
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return nodes;
	}
	
	public List<SourceNode> get(String sourcePath) {
		Validate.isNotEmptyString(sourcePath);
		return get(new File(sourcePath));
	}
	
	public static SourceNodeFactory valueOf() {
		return new SourceNodeFactory();
	}

}
