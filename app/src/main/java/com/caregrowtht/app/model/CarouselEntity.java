package com.caregrowtht.app.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by haoruigang on 2018/5/22 18:55.
 */
public class CarouselEntity implements Serializable {
    /**
     * bannerId : 4
     * image : http://acz.oss-cn-beijing.aliyuncs.com/carousel/23111526984195.jpg
     * text : 家长端轮播图2
     * type : 2
     * content : 富文本<span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本<span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span><span>富文本</span></span>
     * link :
     */

    private String bannerId;
    private String image;
    private String text;
    private String type;
    private String content;
    private String link;

    public String getBannerId() {
        return bannerId;
    }

    public void setBannerId(String bannerId) {
        this.bannerId = bannerId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
