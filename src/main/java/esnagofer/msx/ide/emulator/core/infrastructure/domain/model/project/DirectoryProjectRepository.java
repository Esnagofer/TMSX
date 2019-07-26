package esnagofer.msx.ide.emulator.core.infrastructure.domain.model.project;

import java.util.Set;

import esnagofer.msx.ide.emulator.core.domain.model.project.Project;
import esnagofer.msx.ide.emulator.core.domain.model.project.ProjectId;
import esnagofer.msx.ide.emulator.core.domain.model.project.ProjectRepository;

public class DirectoryProjectRepository implements ProjectRepository {

	public DirectoryProjectRepository() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void add(Project aggregate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void add(Set<Project> aggregate) {
		// TODO Auto-generated method stub

	}

	@Override
	public Project get(ProjectId aggregateId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(ProjectId aggregateId) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean contains(ProjectId aggregateId) {
		// TODO Auto-generated method stub
		return false;
	}

}
