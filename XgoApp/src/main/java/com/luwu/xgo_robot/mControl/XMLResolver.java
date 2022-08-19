package com.luwu.xgo_robot.mControl;

import android.app.Activity;
import android.util.Log;

import com.luwu.xgo_robot.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XMLResolver {

    private String TAG = "XMLResolver";
    private static final long SLEEPTIME = 100;
    private XMLResolverFunction mFunction;
    private Activity activity;
    private Boolean interupt_flag = false;//打断标志量

    public void setinterupt_flag(Boolean interupt_flag) {
        this.interupt_flag = interupt_flag;
    }

    public XMLResolver(Activity activity) {
        this.mFunction = new XMLResolverFunction();
        this.activity = activity;
    }

    public void readXMLString(File file) {//这个函数只是为了把xml文件打印在日志上

        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                Log.d(TAG, "readXMLString: " + line);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            fileReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resolveXML(File xmlFile) {//解析xml文件的入口 启动循环机器

        //1.创建DocumentBuilderFactory对象
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //2.创建DocumentBuilder对象
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            Element xmlRoot = document.getDocumentElement();//得到 XML 文档的根节点

            ArrayList<Element> allRootElements = getElementChildren(xmlRoot);//得到节点的子节点
            Element startElement = null;//先存一下,根element里最后执行startElement
            for (Element element : allRootElements) {
                startElement = element;
                if (startElement != null) {
                    break;
                }
            }
            if (startElement == null) {
                return;
            }
            resloveBlockElement(startElement);//开始解析xml
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            Log.d(TAG, "resolveXML: 有未解析的指令 出现空指针");
            e.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG, "resolveXML: 未处理异常 : ", e);
        }

    }

    private Object resloveBlockNode(Node elementNode) {//把节点转化为元素的中间桥梁 防止报错
        try {
            Element element = (Element) elementNode;
            return resloveBlockElement(element);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private XMLDataRequest resloveBlockElement(Element block) throws InterruptedException {//处理block元素 一堆case
        Element element;//待用
        int direction, speed, time, state, which;
        String returnState;

        if (Thread.interrupted()) {//这个没用
            Log.d(TAG, "xml解析执行被中断:Thread");
            return null;
        }
        if (interupt_flag) {
            Log.d(TAG, "xml解析执行被中断:interupt_flag");
            return null;
        }
        switch (getElementType(block)) {
//            case "start":
//                Log.d(TAG, "startBlock: ");
//                break;
            //singleActivity中的
            case "input_position":
                element = getElementChildrenByName(block, "which");
                which = Integer.parseInt(resloveFieldElement(element).getRequestString());
                element = getElementChildrenByName(block, "top");
                int top = Integer.parseInt(resloveFieldElement(element).getRequestString());
                element = getElementChildrenByName(block, "middle");
                int middle = Integer.parseInt(resloveFieldElement(element).getRequestString());
                element = getElementChildrenByName(block, "bottom");
                int bottom = Integer.parseInt(resloveFieldElement(element).getRequestString());
                mFunction.input_position(which, top, middle, bottom);
                break;
            case "set_motorspeed":
                element = getElementChildrenByName(block, "speed");
                speed = Integer.parseInt(resloveFieldElement(element).getRequestString());
                mFunction.set_motorspeed(speed);
                break;
            case "input_reset":
                mFunction.input_reset();
                break;
            //wholeActivity中的
            //运动
            case "move_direction_speed":
                element = getElementChildrenByName(block, "direction");
                direction = Integer.parseInt(resloveFieldElement(element).getRequestString());
                element = getElementChildrenByName(block, "speed");
                speed = Integer.parseInt(resloveFieldElement(element).getRequestString());
                mFunction.move_direction_speed(direction, speed);
                break;
            case "move_direction_speed_time":
                element = getElementChildrenByName(block, "direction");
                direction = Integer.parseInt(resloveFieldElement(element).getRequestString());
                element = getElementChildrenByName(block, "speed");
                speed = Integer.parseInt(resloveFieldElement(element).getRequestString());
                element = getElementChildrenByName(block, "time");
                time = Integer.parseInt(resloveFieldElement(element).getRequestString());
                mFunction.move_direction_speed_time(direction, speed, time);
                break;
            case "move_zero_speed":
                element = getElementChildrenByName(block, "speed");
                speed = Integer.parseInt(resloveFieldElement(element).getRequestString());
                mFunction.move_zero_speed(speed);
                break;
            case "move_zero_speed_time":
                element = getElementChildrenByName(block, "speed");
                speed = Integer.parseInt(resloveFieldElement(element).getRequestString());
                element = getElementChildrenByName(block, "time");
                time = Integer.parseInt(resloveFieldElement(element).getRequestString());
                mFunction.move_zero_speed_time(speed, time);
                break;
            case "move_stop":
                mFunction.move_stop();
                break;
            case "shake_direction_speed":
                element = getElementChildrenByName(block, "direction");
                direction = Integer.parseInt(resloveFieldElement(element).getRequestString());
                element = getElementChildrenByName(block, "speed");
                speed = Integer.parseInt(resloveFieldElement(element).getRequestString());
                mFunction.shake_direction_speed(direction, speed);
                break;
            case "shake_direction_speed_time":
                element = getElementChildrenByName(block, "direction");
                direction = Integer.parseInt(resloveFieldElement(element).getRequestString());
                element = getElementChildrenByName(block, "speed");
                speed = Integer.parseInt(resloveFieldElement(element).getRequestString());
                element = getElementChildrenByName(block, "time");
                time = Integer.parseInt(resloveFieldElement(element).getRequestString());
                mFunction.shake_direction_speed_time(direction, speed, time);
                break;
            case "shake_stop":
                mFunction.shake_stop();
                break;
            case "all_stop":
                mFunction.all_stop();
                break;
            case "open_imu":
                element = getElementChildrenByName(block, "imu");
                int imu = Integer.parseInt(resloveFieldElement(element).getRequestString());
                mFunction.open_imu(imu);
                break;
            case "state_imu":
                returnState = mFunction.state_imu();
                return new XMLDataRequest(returnState);
            //声光
            case "open_led":
                element = getElementChildrenByName(block, "state");
                state = Integer.parseInt(resloveFieldElement(element).getRequestString());
                element = getElementChildrenByName(block, "which");
                which = Integer.parseInt(resloveFieldElement(element).getRequestString());
                mFunction.open_led(state, which);
                break;
            case "state_led":
                element = getElementChildrenByName(block, "which");
                which = Integer.parseInt(resloveFieldElement(element).getRequestString());
                returnState = mFunction.state_led(which);
                return new XMLDataRequest(returnState);
            case "play_radio":
                mFunction.play_radio();
                break;
            case "open_ledRGB":
                element = getElementChildrenByName(block, "ledR");
                int R = Integer.parseInt(resloveFieldElement(element).getRequestString());
                element = getElementChildrenByName(block, "ledG");
                int G = Integer.parseInt(resloveFieldElement(element).getRequestString());
                element = getElementChildrenByName(block, "ledB");
                int B = Integer.parseInt(resloveFieldElement(element).getRequestString());
                mFunction.open_ledRGB(R,G,B);
                break;
            //探测
            case "sensor_avoid":
                element = getElementChildrenByName(block, "which");
                which = Integer.parseInt(resloveFieldElement(element).getRequestString());
                returnState = mFunction.sensor_avoid(which);
                return new XMLDataRequest(returnState);
            case "sensor_Ultrasonic":
                returnState = String.valueOf(mFunction.sensor_Ultrasonic());
                return new XMLDataRequest(returnState);
            case "sensor_Magnet":
                returnState = mFunction.sensor_Magnet();
                return new XMLDataRequest(returnState);
            //逻辑部分
            case "control_wait":
                element = getElementChildrenByName(block, "time");
                time = Integer.parseInt(resloveFieldElement(element).getRequestString());
                element = getElementChildrenByName(block, "unit");
                which = Integer.parseInt(resloveFieldElement(element).getRequestString());
                mFunction.control_wait(time, which);
                break;
            case "controls_if": {
                Log.d(TAG, "------------------controls_if 模块---------------------");
                Element ifElement = getElementChildrenByName(block, "IF0");
                String ifResult = resloveValueElement(ifElement).getRequestString();//处理if的条件语句并得到字符型结果

                Log.d(TAG, "controls_if判断结果:" + ifResult);
                if (ifResult.equals("TRUE")) {
                    Log.d(TAG, "contrlos_if执行");
                    Element do0Element = getElementChildrenByName(block, "DO0");
                    if (do0Element != null)
                        resloveStatementElement(do0Element);
                } else {
                    Log.d(TAG, "contrlos_if不执行,跳过");
                }
                Log.d(TAG, "------------------controls_if 模块---------------------");
            }
            break;
            case "controls_ifelse": {
                Log.d(TAG, "-----------------controls_ifelse 模块-------------------");
                Element ifElement = getElementChildrenByName(block, "IF0");
                String ifResult = resloveValueElement(ifElement).getRequestString();
                Log.d(TAG, "controls_ifelse判断结果:" + ifResult);
                if (ifResult.equals("TRUE")) {
                    Log.d(TAG, "controls_ifelse执行DO");
                    Element do0Element = getElementChildrenByName(block, "DO0");
                    if (do0Element != null)
                        resloveStatementElement(do0Element);
                } else {
                    Log.d(TAG, "controls_ifelse执行ELSE");
                    Element elseElement = getElementChildrenByName(block, "ELSE");
                    if (elseElement != null)
                        resloveStatementElement(elseElement);
                }
                Log.d(TAG, "-------------------controls_ifelse 模块-----------------");
            }
            break;
            case "controls_repeat_ext": {
                Log.d(TAG, "---------------------controls_repeat_ext 模块---------------------");

                Element timeElement = getElementChildrenByName(block, "TIMES");
                Element doElement = getElementChildrenByName(block, "DO");
                XMLDataRequest repeatTimes = resloveValueElement(timeElement);
                int repeatTime = Integer.parseInt(repeatTimes.getRequestString());
                for (int i = 0; i < repeatTime; i++) {

                    Log.d(TAG, "controls_repeat_ext模块重复" + i + "次");
                    if (doElement != null) {
                        resloveStatementElement(doElement);
                    }

                    Thread.sleep(SLEEPTIME);

                    if(interupt_flag)//打断循环
                        return null;
                }
                Log.d(TAG, "---------------------controls_repeat_ext 模块---------------------");
            }
            break;
            case "controls_whileUntil": {
                Log.d(TAG, "--------------------controls_whileUntil 模块---------------------");
                Element modeElement = getElementChildrenByName(block, "MODE");
                Element boolElement = getElementChildrenByName(block, "BOOL");
                Element doElement = getElementChildrenByName(block, "DO");
                String mode = resloveFieldElement(modeElement).getRequestString();
                String bool = resloveValueElement(boolElement).getRequestString();
                while (mode.equals("WHILE") && bool.equals("TRUE")) {
                    Log.d(TAG, "controls_whileUntil 模块执行 WHILE 功能");

                    if (doElement != null) {
                        resloveStatementElement(doElement);
                    }

                    Thread.sleep(SLEEPTIME);
                    bool = resloveValueElement(boolElement).getRequestString();
                    if(interupt_flag)//打断循环
                        return null;
                }
                while (mode.equals("UNTIL") && !bool.equals("TRUE")) {
                    Log.d(TAG, "controls_whileUntil 模块执行 UNTIL 功能");

                    if (doElement != null) {
                        resloveStatementElement(doElement);
                    }
                    Thread.sleep(SLEEPTIME);
                    bool = resloveValueElement(boolElement).getRequestString();
                    if(interupt_flag)//打断循环
                        return null;
                }
                Log.d(TAG, "--------------------controls_whileUntil 模块---------------------");
            }
            break;
            case "logic_and": {

                Log.d(TAG, "--------------------logic_and 模块-----------------");

                Element aElement = getElementChildrenByName(block, "A");
                Element bElement = getElementChildrenByName(block, "B");

                String valuea = resloveValueElement(aElement).getRequestString();
                String valueb = resloveValueElement(bElement).getRequestString();
                if (valuea.equals("TRUE") && valueb.equals("TRUE")) {
                    return new XMLDataRequest("TRUE");
                }
                Log.d(TAG, "--------------------logic_and 模块-----------------");
                return new XMLDataRequest("FALSE");
            }

            case "logic_or": {

                Log.d(TAG, "--------------------logic_or 模块--------------------");

                Element aElement = getElementChildrenByName(block, "A");
                Element bElement = getElementChildrenByName(block, "B");

                String valuea = resloveValueElement(aElement).getRequestString();
                String valueb = resloveValueElement(bElement).getRequestString();
                if (valuea.equals("TRUE") || valueb.equals("TRUE")) {
                    return new XMLDataRequest("TRUE");
                }
                Log.d(TAG, "--------------------logic_or 模块--------------------");
                return new XMLDataRequest("FALSE");
            }
            case "logic_compare": {

                Log.d(TAG, "--------------------logic_compare 模块--------------------");

                Element aElement = getElementChildrenByName(block, "A");
                Element mElement = getElementChildrenByName(block, "symbol");
                Element bElement = getElementChildrenByName(block, "B");

                int symbol = Integer.parseInt(resloveFieldElement(mElement).getRequestString());
                int valuea = Integer.parseInt(resloveValueElement(aElement).getRequestString());
                int valueb = Integer.parseInt(resloveValueElement(bElement).getRequestString());

                if(symbol==1){
                    if(valuea>valueb) {
                        return new XMLDataRequest("TRUE");
                    }
                }else if(symbol==2){
                    if(valuea<valueb) {
                        return new XMLDataRequest("TRUE");
                    }
                }else if(symbol==3) {
                    if(valuea==valueb) {
                        return new XMLDataRequest("TRUE");
                    }
                }
                Log.d(TAG, "--------------------logic_compare 模块--------------------");
                return new XMLDataRequest("FALSE");
            }
            case "logic_boolean": {
                Element boolElement = getElementChildrenByName(block, "BOOL");
                return resloveFieldElement(boolElement);
            }
            case "logic_number": {
                Element boolElement = getElementChildrenByName(block, "NUM");
                return resloveFieldElement(boolElement);
            }
            //用户自定义
            default:
                String user_defined = getElementType(block);
                if ("user_defined".equals(user_defined.substring(0, 12))) {//说明是用户自定义的 解析对应xml
                    Log.d(TAG, "resloveBlockElement: 进入default" + user_defined.substring(0, 12));
                    JsonControl jsonControl = new JsonControl();
                    XMLResolver xmlResolver = new XMLResolver(activity);
                    File fileB = new File(activity.getFilesDir(), "whole_file_blocks_self.json");
                    if (fileB.exists()) {
                        String selfFile = userToXml(jsonControl.getNameByType(fileB, user_defined));
                        Log.d(TAG, "resloveBlockElement: 开始解析自定义文件" + selfFile);
                        File xmlFile = new File(activity.getFilesDir(), selfFile);
                        if (xmlFile.exists()) {
                            xmlResolver.readXMLString(xmlFile);//打印xml文件
                            try {
                                xmlResolver.resolveXML(xmlFile);//解析xml文件
                            } catch (Exception e) {
                                Log.d("Tag", "解析错误" + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                } else if ("system_defined".equals(user_defined.substring(0, 14))) {//说明是系统自定义的 解析对应xml
                    Log.d(TAG, "resloveBlockElement: 进入default" + user_defined.substring(0, 14));
                    JsonControl jsonControl = new JsonControl();
                    XMLResolver xmlResolver = new XMLResolver(activity);
                    File fileB = new File(activity.getFilesDir(), "whole_file_blocks_self.json");
                    if (fileB.exists()) {
                        String selfFile = userToXmlAuto(jsonControl.getNameByType(fileB, user_defined));
                        Log.d(TAG, "resloveBlockElement: 开始解析自定义文件" + selfFile);
                        File xmlFile = new File(activity.getFilesDir(), selfFile);
                        if (xmlFile.exists()) {
                            xmlResolver.readXMLString(xmlFile);//打印xml文件
                            try {
                                xmlResolver.resolveXML(xmlFile);//解析xml文件
                            } catch (Exception e) {
                                Log.d("Tag", "解析错误" + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                }
                break;
        }

        //处理next模块
        NodeList blockNodeList = block.getChildNodes();
        for (int i = 0; i < blockNodeList.getLength(); i++) {
            Node node = blockNodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (node.getNodeName().equals("next")) {
                    resloveNextElement(node);
                }
            }
        }
        return null;
    }

    private void resloveNextElement(Node block) {//处理next中所有子节点 通常只有一个节点
        NodeList nextNodeList = block.getChildNodes();
        for (int i = 0; i < nextNodeList.getLength(); i++) {
            Node shadowNode = nextNodeList.item(i);
            if (shadowNode.getNodeType() == Node.ELEMENT_NODE) {
                resloveBlockNode(shadowNode);//先要判断是否为element节点
            }
        }
    }

    private XMLDataRequest resloveValueElement(Element block) throws InterruptedException {//处理条件逻辑判断语句 value标签

        ArrayList<Element> elements = getElementChildren(block);

        for (int i = elements.size() - 1; i >= 0; i--) {
            //从后往前循环,第一位可能是默认值
            Element valueNode = elements.get(i);
            switch (valueNode.getNodeName()) {
                case "shadow":
                    return resloveShadowElement(valueNode);

                case "block":
                    return resloveBlockElement(valueNode);//发现if后是一个语句 则回调去执行

                default:
                    Log.d(TAG, "resloveValueElement: 未知的 Value node名称:" + valueNode.getNodeName());
            }
        }

        return new XMLDataRequest();
    }

    private XMLDataRequest resloveShadowElement(Element block) {//处理()逻辑条件判断语句中的shadow类型 shadow标签
        Log.d(TAG, "shadow: " + "   Type:" + getElementType(block));
        switch (getElementType(block)) {
            case "math_number":
                ArrayList<Element> numberElements = getElementChildren(block);
                for (Element element : numberElements) {
                    return resloveFieldElement(element);
                }
                break;
            default:
                Log.d(TAG, "resloveShadowElement: 未知的shadow模块" + getElementType(block));
                break;
        }
        return new XMLDataRequest();
    }

    private XMLDataRequest resloveFieldElement(Element block) {//（）逻辑判断中field中的类型 field标签

        switch (getElementName(block)) {
            case "direction":
            case "speed":
            case "time":
            case "unit":
            case "state":
            case "which":
            case "imu":
            case "top":
            case "middle":
            case "bottom":
            case "symbol":
                return new XMLDataRequest(block.getFirstChild().getNodeValue());//得到文本节点并转化为字符串
            case "NUM":
            case "MODE":
            case "BOOL":
                return new XMLDataRequest(block.getFirstChild().getNodeValue());
            default:
                Log.d(TAG, "resloveFieldElement: 未知field模块 " + getElementName(block));
                return new XMLDataRequest(block.getFirstChild().getNodeValue());
        }
    }

    private void resloveStatementElement(Element block) {//处理逻辑判断后的do语句 statement标签
        ArrayList<Element> elementChildren = getElementChildren(block);
        for (int i = 0; i < elementChildren.size(); i++) {
            Node shadowNode = elementChildren.get(i);
            resloveBlockNode(shadowNode);
        }
    }


    /*
     *自定义了部分DOM操作
     */
    private String getElementType(Element element) {
        return element.getAttribute("type");
    }

    private String getElementType(Node node) {
        Element element = (Element) node;
        try {
            return element.getAttribute("type");
        } catch (ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getElementName(Element element) {
        return element.getAttribute("name");
    }

    private String getElementName(Node node) {
        try {
            Element element = (Element) node;
            return element.getAttribute("name");
        } catch (ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取全部子element
    private ArrayList<Element> getElementChildren(Element block) {

        ArrayList<Element> childrenElements = new ArrayList<>();
        NodeList shadowNodeList = block.getChildNodes();
        for (int i = 0; i < shadowNodeList.getLength(); i++) {
            Node shadowNode = shadowNodeList.item(i);
            if (shadowNode.getNodeType() == Node.ELEMENT_NODE) {
                childrenElements.add((Element) shadowNode);
            }
        }
        return childrenElements;
    }


    private Element getElementChildrenByName(Element block, String name) {//根据name属性 获取子block
        NodeList shadowNodeList = block.getChildNodes();
        for (int i = 0; i < shadowNodeList.getLength(); i++) {
            Node shadowNode = shadowNodeList.item(i);
            if (shadowNode.getNodeType() == Node.ELEMENT_NODE) {
                if (getElementName(shadowNode).equals(name)) {
                    return (Element) shadowNode;
                }
            }
        }
        if (name.equals("DO") || name.equals("DO0") || name.equals("ELSE")) {//为了找blockly里的元素 而不是找逻辑运算中的blockly 所以返回null
            return null;
        }
        return null;
    }

    //将用户输入的名字格式化
    private String userToXml(String user) {
        String singlexml = new String();
        singlexml = activity.getString(R.string.singlexmlHead) + user + activity.getString(R.string.singlexmlTail);
        return singlexml;
    }

    //将系统自带的名字格式化
    private String userToXmlAuto(String user) {
        String autosinglexml = new String();
        if (user == null) {
            user = "";
        }
        autosinglexml = activity.getString(R.string.singlexmlHeadAuto) + user + activity.getString(R.string.singlexmlTail);
        return autosinglexml;
    }
}
