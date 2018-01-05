package com.jaky.demo.statis;

/**
 * Created by jaky on 2018/1/5 0005.
 */

public class ReadingStatisticsBean {
    private int type ;
    private String md5;
    private String md5short;
    private long eventTime;
    private String path;
    private String name;
    private String author;
    private long durationTime;
    private int score;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getMd5short() {
        return md5short;
    }

    public void setMd5short(String md5short) {
        this.md5short = md5short;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(long durationTime) {
        this.durationTime = durationTime;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
