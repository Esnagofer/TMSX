package esnagofer.msx.ide.emulator.core.infrastructure.domain.model.project;

import java.io.File;
import java.util.Set;

import org.jboss.weld.exceptions.UnsupportedOperationException;

import esnagofer.msx.ide.emulator.core.domain.model.project.Project;
import esnagofer.msx.ide.emulator.core.domain.model.project.ProjectId;
import esnagofer.msx.ide.emulator.core.domain.model.project.ProjectRepository;

public class DirectoryProjectRepository implements ProjectRepository {

	@Override
	public void add(Project aggregate) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void add(Set<Project> aggregate) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public Project get(ProjectId aggregateId) {
		return Project.builder()
			.withId(aggregateId)
			.withSourceNodes(SourceNodeFactory.valueOf().get(aggregateId.value()))
		.build();
	}

	@Override
	public void remove(ProjectId aggregateId) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public boolean contains(ProjectId aggregateId) {
		File file = new File(aggregateId.value());
		return file.exists();
	}

}
