package net.oschina.gitapp.bean;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

@SuppressWarnings("serial")
public class Namespace extends Entity {
	public static final String URL = "/groups";
	
    private String _name;
    private String _path;
    private String _description;

    @JsonProperty("created_at")
    private Date _createdAt;

    @JsonProperty("updated_at")
    private Date _updatedAt;

    @JsonProperty("owner_id")
    private Integer _ownerId;

    public Date getCreatedAt() {
        return _createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        _createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return _updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        _updatedAt = updatedAt;
    }

    public Integer getOwnerId() {
        return _ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        _ownerId = ownerId;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getPath() {
        return _path;
    }

    public void setPath(String path) {
        _path = path;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }
}
