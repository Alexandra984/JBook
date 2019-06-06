package model;

import java.util.Date;

public class Message {
    private Integer id;
    private Integer fromId;
    private Integer toId;
    private String content;
    private boolean readStatus;
    private Date date;

    public Message(Integer id, Integer fromId, Integer toId, String content, boolean readStatus, Date date) {
        this.id = id;
        this.fromId = fromId;
        this.toId = toId;
        this.content = content;
        this.readStatus = readStatus;
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isReadStatus() {
        return readStatus;
    }

    public void setReadStatus(boolean readStatus) {
        this.readStatus = readStatus;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
