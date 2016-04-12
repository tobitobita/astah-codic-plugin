package dsk.codic.astah.plugin.ui.entity;

import javax.xml.bind.annotation.XmlAttribute;

public class ProjectData {

    private String id;
    private String name;

    public ProjectData() {
    }

    public ProjectData(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @XmlAttribute
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
