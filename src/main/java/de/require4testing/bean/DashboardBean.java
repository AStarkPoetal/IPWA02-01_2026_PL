package de.require4testing.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class DashboardBean implements Serializable {

    private String activePanel = "home";

    public void showPanel(String panel) {
        this.activePanel = panel;
    }

    public String getActivePanel() {
        return activePanel;
    }

    public void setActivePanel(String activePanel) {
        this.activePanel = activePanel;
    }
}
