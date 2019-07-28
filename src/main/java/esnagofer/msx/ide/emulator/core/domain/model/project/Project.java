package esnagofer.msx.ide.emulator.core.domain.model.project;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

import esnagofer.msx.ide.lib.Validate;
import esnagofer.msx.ide.lib.domain.model.core.Aggregate;

public class Project extends Aggregate<ProjectId> {

	public static final String VERSION = "v1";

	public List<SourceNode> sourceNodes;
	
	@Generated("SparkTools")
	private Project(Builder builder) {
		super(builder.id);
		Validate.isNotNull(builder.sourceNodes);
	}
	
	/**
	 * Creates builder to build {@link Project}.
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link Project}.
	 */
	@Generated("SparkTools")
	public static final class Builder {
		
		private ProjectId id;
		
		private List<SourceNode> sourceNodes = new ArrayList<>();

		private Builder() {}

		public Builder withId(ProjectId id) {
			this.id = id;
			return this;
		}

		public Builder withSourceNodes(List<SourceNode> sourceNodes) {
			this.sourceNodes.clear();
			this.sourceNodes.addAll(sourceNodes);
			return this;
		}

		public Project build() {
			return new Project(this);
		}
	}
	
}
