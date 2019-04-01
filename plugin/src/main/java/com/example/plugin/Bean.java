package com.example.plugin;

import com.example.mypluginlibrary.IBean;
import com.example.mypluginlibrary.ICallBack;

public class Bean implements IBean {

    private String name = "初始";


    @Override
    public String getName(ICallBack callBack) {
        callBack.doThing(name);
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
