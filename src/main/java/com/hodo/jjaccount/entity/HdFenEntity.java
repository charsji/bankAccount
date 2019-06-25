package com.hodo.jjaccount.entity;

import java.util.List;

public class HdFenEntity {
    String id;
    List<HdFenListEntity> hdFenStr;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<HdFenListEntity> getHdFenStr() {
        return hdFenStr;
    }

    public void setHdFenStr(List<HdFenListEntity> hdFenStr) {
        this.hdFenStr = hdFenStr;
    }
}
