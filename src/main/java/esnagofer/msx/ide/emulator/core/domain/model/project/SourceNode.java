package esnagofer.msx.ide.emulator.core.domain.model.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import esnagofer.msx.ide.lib.Validate;

public class SourceNode {

	private String id;
	
	private String name;
	
	private String content;
	
	private Boolean isContainer;
	
	private List<SourceNode> childNodes;
	
	protected SourceNode( 
		Boolean isContainer, 
		String id, 
		String name, 
		String content,
		List<SourceNode> childNodes
	) {
		Validate.isNotNull(isContainer);
		Validate.isNotEmptyString(id);
		Validate.isNotEmptyString(name);
		this.isContainer = isContainer;
		this.id = id;
		this.name = name;
		this.content = content;
		this.childNodes = childNodes;
	}
	
	public String name() {
		return name;
	}

	public Optional<String> content() {
		return Optional.ofNullable(content);
	}
	
	public boolean isContainer() {
		return isContainer;
	}
	
	public String id() {
		return id;
	}
	
	public List<SourceNode> childNodes() {
		if (childNodes == null) {
			return new ArrayList<>();
		} else {
			return childNodes;
		}
	}

	public static SourceNode containerOf(String id, String name, List<SourceNode> childNodes) {
		return new SourceNode(true, id, name, null, childNodes);
	}

	public static SourceNode contentOf(String id, String name, String content, List<SourceNode> childNodes) {
		return new SourceNode(false, id, name, content, childNodes);
	}

}
