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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((childNodes == null) ? 0 : childNodes.hashCode());
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((isContainer == null) ? 0 : isContainer.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SourceNode other = (SourceNode) obj;
		if (childNodes == null) {
			if (other.childNodes != null)
				return false;
		} else if (!childNodes.equals(other.childNodes))
			return false;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isContainer == null) {
			if (other.isContainer != null)
				return false;
		} else if (!isContainer.equals(other.isContainer))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
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
