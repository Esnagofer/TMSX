package esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.main;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.inject.Named;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

import esnagofer.msx.ide.emulator.core.domain.model.project.SourceNode;
import esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.project.ProjectDirectorySelectedUIEvent;
import esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.project.ProjectDirectorySelector;
import esnagofer.msx.ide.emulator.core.infrastructure.userinterface.javafx.ide.richtextfx.z80sjasm.Z80SjasmKeywordsFactory;
import esnagofer.msx.ide.lib.Validate;
import esnagofer.msx.ide.lib.userinterface.UIEventManager;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

@Named("ideMainController")
public class IdeMainController implements javafx.fxml.Initializable {
	
	private static IdeMainController instance;
	
	private Stage stage;
	
	private UIEventManager eventManager;
	
	private Image nodeImage;
	
	private Image nodeContainerImage;
	
	@FXML
	private MenuItem menuProjectOpen;
	
	@FXML
	private TabPane tabPaneSource;
	
	@FXML
	private MenuItem menuProjectCompile;
	
	@FXML
	private TreeView treeViewSources;
	
	@FXML
	private MenuItem menuProjectDebug;

	private Scene scene;
	
	public IdeMainController(
		UIEventManager eventManager,
		Stage stage
	) {
		Validate.isNotNull(eventManager);
		Validate.isNotNull(stage);
		this.eventManager = eventManager;
		this.stage = stage;
		this.instance  = this;
	}
	
	public void init(Scene scene) {
		Validate.isNotNull(scene);
		this.scene = scene;
	}

	private Image treeItemNodeImage() {
		if (nodeImage == null) {
			nodeImage = new Image(getClass().getResourceAsStream("/icons/node_16x16.png"));
		}
		return nodeImage;
	}
	
	private Image treeItemNodeContainerImage() {
		if (nodeContainerImage == null) {
			nodeContainerImage = new Image(getClass().getResourceAsStream("/icons/node_container_16x16.png"));
		}
		return nodeContainerImage;
	}
	
	private TreeItemSourceNode createTreeItem(SourceNode sourceNode) {
		TreeItemSourceNode treeItem;
		if (sourceNode.isContainer()) {
			treeItem = new TreeItemSourceNode(sourceNode, new ImageView(treeItemNodeContainerImage()));			
		} else {
			treeItem = new TreeItemSourceNode(sourceNode, new ImageView(treeItemNodeImage()));
		}
		return treeItem;
	}

	private TreeItemSourceNode processThisSourceNode(SourceNode sourceNode) {
		TreeItemSourceNode item = createTreeItem(sourceNode);
		sourceNode.childNodes().stream().forEach(thisSource -> {
			if (thisSource.isContainer()) {
				item.getChildren().add(processThisSourceNode(thisSource));
			} else {
				item.getChildren().add(createTreeItem(thisSource));
			}
		});
		return item;
	} 

	private List<TreeItemSourceNode> processAllSourceNodes(List<SourceNode> sources) {
		return sources.stream().map(this::processThisSourceNode).collect(Collectors.toList());
	}
	
	public static IdeMainController instance() {
		return instance;
	}

	public void assignProjectSource(String projectName, List<SourceNode> sources) {
		TreeItem<String> root = new TreeItem<String>(projectName);
		root.getChildren().addAll(processAllSourceNodes(sources));
		treeViewSources.setRoot(root);
	}
	
	@FXML
	public void menuProjectOpenAction(Event e) {
		Optional<File> projectDirectory = ProjectDirectorySelector.valueOf(stage).select();
		if (projectDirectory.isPresent()) {
			eventManager.publish(
				ProjectDirectorySelectedUIEvent.valueOf(projectDirectory.get())
			);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		menuProjectCompile.setDisable(true);
		menuProjectDebug.setDisable(true);
		treeViewSources.setOnMouseClicked(new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent mouseEvent) {            
		        if(mouseEvent.getClickCount() == 2) {
		        	TreeItemSourceNode item = (TreeItemSourceNode) treeViewSources.getSelectionModel().getSelectedItem();
		            Tab tabdata = new Tab();
		            Label tabLabel = new Label(item.sourceNode().name());
		            tabdata.setGraphic(tabLabel);
		            tabdata.setClosable(true);
		            CodeArea codeArea = Z80SjasmKeywordsFactory.create(scene, item.sourceNode().content().get());
		            StackPane stackPage = new StackPane(new VirtualizedScrollPane<>(codeArea));
//		            AnchorPane codeAreaAnchorPane = new AnchorPane();
//		            codeAreaAnchorPane.getChildren().add(codeArea);
//		            tabdata.setContent(codeAreaAnchorPane);
		            tabdata.setContent(stackPage);
		            tabPaneSource.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
	            	tabPaneSource.getTabs().add(tabdata);
		        }
		    }
		});
	}
	
}
