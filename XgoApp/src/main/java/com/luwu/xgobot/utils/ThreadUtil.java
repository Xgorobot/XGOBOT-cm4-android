package com.luwu.xgobot.utils;

import com.blankj.utilcode.util.ThreadUtils;

public class ThreadUtil {
    String key = "key";
    public static void runIO(Runnable runnable){
        ThreadUtils.executeByIo(new ThreadUtils.Task<Object>() {
            @Override
            public String doInBackground() throws Throwable {
                runnable.run();
                return null;
            }

            @Override
            public void onSuccess(Object o) {

            }


            @Override
            public void onCancel() {

            }

            @Override
            public void onFail(Throwable throwable) {

            }
        });
    }
}
