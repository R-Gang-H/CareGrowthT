package com.caregrowtht.app.user;

public class EventData<T> {
    private TAGTYPE tag;
    private String content;
    private T par;

    public enum TAGTYPE {
        ESERACH,//搜索
        INITIATIVE_STEP,
    }

    public TAGTYPE getTag() {
        return tag;
    }

    public EventData setTag(TAGTYPE tag) {
        this.tag = tag;
        return this;
    }

    public String getContent() {
        return content;
    }

    public EventData setContent(String content) {
        this.content = content;
        return this;

    }

    public T getPar() {
        return par;
    }

    public EventData setPar(T par) {
        this.par = par;
        return this;

    }
}
