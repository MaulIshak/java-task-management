package com.taskmanager.view;

import javafx.scene.Node;
import java.util.EnumMap;
import java.util.Map;
import com.taskmanager.model.Project;

public class ViewFactory {
    private static ViewFactory instance;
    private final Map<ViewName, Node> viewCache = new EnumMap<>(ViewName.class);

    // Services
    private final com.taskmanager.service.TaskService taskService;
    private final com.taskmanager.service.ProjectService projectService;
    private final com.taskmanager.service.OrganizationService organizationService;
    private final com.taskmanager.service.AuthService authService;

    private ViewFactory() {
        this.taskService = com.taskmanager.service.TaskService.getInstance();
        this.projectService = com.taskmanager.service.ProjectService.getInstance();
        this.organizationService = com.taskmanager.service.OrganizationService.getInstance();
        this.authService = new com.taskmanager.service.AuthService();
    }

    public static synchronized ViewFactory getInstance() {
        if (instance == null) {
            instance = new ViewFactory();
        }
        return instance;
    }

    public Node getView(ViewName view) {
        if (view != ViewName.PROJECT_DETAIL && view != ViewName.PROJECTS && viewCache.containsKey(view)) {
            return viewCache.get(view);
        }

        Node loadedView = switch (view) {
            case DASHBOARD -> new DashboardView(organizationService, projectService, taskService);
            case PROJECTS -> new ProjectView(projectService, organizationService, taskService);
            case PROJECT_DETAIL -> {
                Project currentProject = AppState.getInstance().getCurrentProject();
                if (currentProject != null) {
                    ProjectDetailView detailView = new ProjectDetailView(taskService);
                    detailView.setProject(currentProject);
                    yield detailView;
                } else {
                    yield new ProjectView(projectService, organizationService, taskService);
                }
            }
            case LOGIN -> new LoginView(authService);
            case REGISTER -> new RegisterView(authService);
        };

        if (view != ViewName.PROJECT_DETAIL && view != ViewName.PROJECTS && view != ViewName.LOGIN && view != ViewName.REGISTER) {
            viewCache.put(view, loadedView);
        }
        return loadedView;
    }

    public Sidebar createSidebar() {
        return new Sidebar(organizationService, projectService);
    }
}
