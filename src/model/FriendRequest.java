package model;

import java.util.Date;

public class FriendRequest {
    private Integer id;
    private Date date;
    private Integer fromId;
    private Integer toId;

    public FriendRequest(Integer id, Date date, Integer fromId, Integer toId) {
        this.id = id;
        this.date = date;
        this.fromId = fromId;
        this.toId = toId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getFromId() {
        return fromId;
    }

    public void setFromId(Integer fromId) {
        this.fromId = fromId;
    }

    public Integer getToId() {
        return toId;
    }

    public void setToId(Integer toId) {
        this.toId = toId;
    }
}
