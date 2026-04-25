package de.require4testing.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
/**
 * Dieses Bean verwaltet ausschließlich den Zustand der Dashboard-Ansicht.
 * Es hält fest, welches Panel auf der zentralen Seite aktuell geöffnet ist.
 */
public class DashboardBean implements Serializable {

    private String activePanel = "home";

    @Inject
    private LoginBean loginBean;

    /**
     * Bei der Navigation darf ein Panel nur dann gewechselt werden, wenn die entsprechende Ansicht verfügbar ist; andernfalls wird auf das Home-Panel zurückgesetzt.
     */
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

    /**
     * Das Home - Panel ist immer sichtbar, ansonsten muss man einloggen.
     */
    public boolean canAccessPanel(String panel) {
        if (panel == null || "home".equals(panel)) {
            return true;
        }

        return loginBean.isLoggedIn();
    }
}
