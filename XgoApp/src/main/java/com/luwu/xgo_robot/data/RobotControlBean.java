package com.luwu.xgo_robot.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RobotControlBean {
    ControlBean data;
    String msg;
    String code;

    public class ControlBean{
        String type;
        Map<String,String> dataMap;

        public ControlBean(String type, Map<String, String> dataMap) {
            this.type = type;
            this.dataMap = dataMap;
        }
    }

    public RobotControlBean(ControlBean data, String msg, String code) {
        this.data = data;
        this.msg = msg;
        this.code = code;

    }
}
