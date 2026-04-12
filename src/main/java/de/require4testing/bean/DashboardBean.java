package de.require4testing.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class DashboardBean implements Serializable {

    private String activePanel = "home";

    @Inject
    private LoginBean loginBean;

    public void showPanel(String panel) {
        if (panel == null || !canAccessPanel(panel)) {
            this.activePanel = "home";
            return;
        }

        this.activePanel = panel;
    }

    public String getActivePanel() {
        return activePanel;
    }

    public void setActivePanel(String activePanel) {
        this.activePanel = activePanel;
    }

    public boolean canAccessPanel(String panel) {
        if (panel == null || "home".equals(panel)) {
            return true;
        }

        return loginBean.isLoggedIn();
    }
}
