package com.autoai.adpush.bean;

import com.autoai.adpush.listener.BaseEventInfo;

/**
 * 广告实体类
 *
 * @author zhaozy
 * @date 2018/10/15
 */


public class AdEventInfo extends BaseEventInfo<AdStatus> {
    private String id;
    private String title;
    private String jumpLink;
    private String filePath;
    private String adverType;
    public AdEventInfo() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String var1) {
        this.id = var1;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String var1) {
        this.title = var1;
    }

    public String getJumpLink() {
        return this.jumpLink;
    }

    public void setJumpLink(String var1) {
        this.jumpLink = var1;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String var1) {
        this.filePath = var1;
    }

    public String getAdverType() {
        return this.adverType;
    }

    public void setAdverType(String var1) {
        this.adverType = var1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AdEventInfo that = (AdEventInfo) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) {
            return false;
        }
        if (getTitle() != null ? !getTitle().equals(that.getTitle()) : that.getTitle() != null) {
            return false;
        }
        if (getJumpLink() != null ? !getJumpLink().equals(that.getJumpLink()) : that.getJumpLink() != null) {
            return false;
        }
        if (getFilePath() != null ? !getFilePath().equals(that.getFilePath()) : that.getFilePath() != null) {
            return false;
        }
        return getAdverType() != null ? getAdverType().equals(that.getAdverType()) : that.getAdverType() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getTitle() != null ? getTitle().hashCode() : 0);
        result = 31 * result + (getJumpLink() != null ? getJumpLink().hashCode() : 0);
        result = 31 * result + (getFilePath() != null ? getFilePath().hashCode() : 0);
        result = 31 * result + (getAdverType() != null ? getAdverType().hashCode() : 0);
        return result;
    }
}
