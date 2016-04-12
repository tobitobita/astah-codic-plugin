package dsk.codic.astah.plugin.ui.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "settings")
public class SettingDialogData {

    private String accessToken;
    private String selectedProjectName;
    private List<ProjectData> projects = new ArrayList<>();

    @XmlElement
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @XmlElement
    public String getSelectedProjectName() {
        return selectedProjectName;
    }

    public void setSelectedProjectName(String selectedProjectName) {
        this.selectedProjectName = selectedProjectName;
    }

    @XmlElementWrapper(name = "projects")
    @XmlElement(name = "project")
    public List<ProjectData> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectData> projects) {
        this.projects = projects;
    }

    public void addProject(ProjectData project) {
        this.projects.add(project);
    }

    @XmlTransient
    public String getSelectedProjectId() {
        final Optional<ProjectData> project = this.projects.stream()
                .filter(p -> p.getName().equals(this.selectedProjectName))
                .findFirst();
        if (project.isPresent()) {
            return project.get().getId();
        }
        return null;
    }

    public static void main(String[] args) {
        final SettingDialogData data = new SettingDialogData();
        data.addProject(new ProjectData("アイデー1", "名前1"));
        data.addProject(new ProjectData("アイデー2", "名前2"));
        data.addProject(new ProjectData("アイデー3", "名前3"));
        data.addProject(new ProjectData("アイデー4", "名前4"));
        data.addProject(new ProjectData("アイデー5", "名前5"));
        data.setAccessToken("access_token");
        data.setSelectedProjectName("アイデー4");

        JAXB.marshal(data, System.out);
    }
}
