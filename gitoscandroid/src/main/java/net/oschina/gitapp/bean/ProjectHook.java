package net.oschina.gitapp.bean;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

@SuppressWarnings("serial")
public class ProjectHook extends Entity {

	public final static String URL = "/hooks";
	
	private String _url;
	
	@JsonProperty("created_at")
    private Date _createdAt;

	public String getUrl() {
		return _url;
	}

	public void setUrl(String url) {
		this._url = url;
	}
    
	public Date getCreatedAt() {
        return _createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        _createdAt = createdAt;
    }
}
