package team.perfect.fresh_air.DAO;

import java.io.Serializable;

import javax.persistence.Column;

public class DustPK implements Serializable {
    private static final long serialVersionUID = 1L;
	@Column(nullable=false)
	private String userId;
	@Column(nullable=false)
	private long time;
	
	public DustPK(){}
	
	public DustPK(String userId, long time) {
		super();
		this.userId = userId;
		this.time = time;
    }

    public DustPK(long time) {
		super();
		this.userId = userId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }    
}