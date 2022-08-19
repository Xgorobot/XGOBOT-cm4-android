package com.luwu.xgo_robot.mView;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

public class SpaceEditText extends AppCompatEditText {

    //上次输入框中的内容
    private String lastString = "";
    //光标的位置
    private int selectPosition;
    //每n个字符后加一个空格
    private final int n = 2;
    private final String hexStr = "0123456789ABCDEF";
    //输入框内容改变监听
    private TextChangeListener listener;


    public SpaceEditText(Context context) {
        super(context);
        initView();
    }

    public SpaceEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();

    }

    public SpaceEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();

    }

    private void initView() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }


            /**
             * 当输入框内容改变时的回调
             * @param s  改变后的字符串
             * @param start 改变之后的光标下标
             * @param before 删除了多少个字符
             * @param count 添加了多少个字符
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //因为重新排序之后setText的存在
                //会导致输入框的内容从0开始输入，这里是为了避免这种情况产生一系列问题
                if (start == 0 && count > 1 && SpaceEditText.this.getSelectionStart() == 0) {
                    return;
                }
                String textTrimAll = SpaceEditText.this.getText().toString().replaceAll(" ", "");
                if (TextUtils.isEmpty(textTrimAll)) {
                    return;
                }

                //如果 before >0 && count == 0,代表此次操作是删除操作
                if (before > 0 && count == 0) {
                    selectPosition = start;
                    if (TextUtils.isEmpty(lastString)) {
                        return;
                    }
                    //将上次的字符串去空格 和 改变之后的字符串去空格 进行比较
                    //如果一致，代表本次操作删除的是空格
                    if (textTrimAll.equals(lastString.replaceAll(" ", ""))) {
                        //帮助用户删除该删除的字符，而不是空格
                        StringBuilder stringBuilder = new StringBuilder(lastString);
                        stringBuilder.deleteCharAt(start - 1);
                        selectPosition = start - 1;
                        SpaceEditText.this.setText(stringBuilder.toString());
                    }
                } else {
                    //此处代表是添加操作
                    String temp = textTrimAll.toUpperCase();
                    for (char a : hexStr.toCharArray()) {
                        temp = temp.replaceAll(String.valueOf(a), "");
                    }
                    if (temp.equals("")) {
                        //当光标位于空格之前，添加字符时，需要让光标跳过空格，再按照之前的逻辑计算光标位置
                        if ((start + count) % (n + 1) == 0) {
                            selectPosition = start + count + 1;
                        } else {
                            selectPosition = start + count;
                        }
                    } else {
                        if (textTrimAll.length() > 1) {
                            SpaceEditText.this.setText(lastString.replaceAll(" ", ""));
                        } else {
                            SpaceEditText.this.setText("");
                        }
                    }
                }

            }


            @Override
            public void afterTextChanged(Editable s) {
                //获取输入框中的内容,不可以去空格
                String nowContent = SpaceEditText.this.getText().toString();
                if (TextUtils.isEmpty(nowContent)) {
                    if (listener != null) {
                        listener.textChange(null);
                    }
                    return;
                }
                //重新拼接字符串
                String newContent = addSpaceByCredit(nowContent).toUpperCase();
                //保存本次字符串数据
                lastString = newContent;

                //如果有改变，则重新填充
                //防止EditText无限setText()产生死循环
                if (!newContent.equals(nowContent)) {
                    SpaceEditText.this.setText(newContent);
                }
                //保证光标的位置
                SpaceEditText.this.setSelection(selectPosition > newContent.length() ? newContent.length() : selectPosition);
                //触发回调内容
                if (listener != null) {
                    listener.textChange(hexToBytes(newContent));
                }

            }
        });
    }

    /**
     * 输入框内容回调，当输入框内容改变时会触发
     */
    public interface TextChangeListener {
        void textChange(byte[] bytes);
    }

    public void setTextChangeListener(TextChangeListener listener) {
        this.listener = listener;

    }

    /**
     * 每4位添加一个空格
     *
     * @param content
     * @return
     */
    private String addSpaceByCredit(String content) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        content = content.replaceAll(" ", "");
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        StringBuilder newString = new StringBuilder();
        for (int i = 1; i <= content.length(); i++) {
            if (i % n == 0 && i != content.length()) {
                newString.append(content.charAt(i - 1) + " ");
            } else {
                newString.append(content.charAt(i - 1));
            }
        }
        return newString.toString();
    }

    //十六进制转换
    private byte[] hexToBytes(String text){
        if(((text.length()+1)%3)!=0) {
            return null;
        }
        byte[] bytes = new byte[(text.length()+1)/3];
        int temp;
        for(int i=0;i<bytes.length;i++){
            temp = hexStr.indexOf(text.charAt(3*i))*16;
            temp += hexStr.indexOf(text.charAt(3*i+1));
            bytes[i]=(byte) temp;
        }
        return bytes;
    }
    public static String bytesToHex(byte[] b)
    {
        String stmp="";
        StringBuilder sb = new StringBuilder("");
        for (int n=0;n<b.length;n++)
        {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length()==1)? "0"+stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }
}
