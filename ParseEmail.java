package com.wt.galaxy;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description:统计文本文件中包括的邮箱地址及其出现次数
 * @date: 2020/4/1 7:45
 * @author: xsz
 */
public class ParseEmail {
    private static Map<String, Long> emailMap = new ConcurrentHashMap<String, Long>();

    public static void main(String[] args) throws IOException, InterruptedException {
        //获取指定路径下该文件对象
        File file = new File("C:/Users/article.txt");
        FileReader fr = new FileReader(file);
        LineNumberReader lnr = new LineNumberReader(fr);

        String readLine = lnr.readLine();
        List<String> list = new ArrayList<>();
        while (readLine != null) {
            if (!"".equals(readLine)) {
                list.add(readLine);
            }
            readLine = lnr.readLine();
        }
        lnr.close();
        System.out.println("共有段落数：" + list.size());
        //使用CountDownLatch计数器
        CountDownLatch latch =
                new CountDownLatch(list.size());
        Executor executor =
                Executors.newFixedThreadPool(list.size());
        Pattern pattern = Pattern.compile("[\\w!#$%&'+/=?^_`{|}~-]+(?:.[\\w!#$%&'+/=?^_`{|}~-]+)@(?:[\\w](?:[\\w-][\\w])?.)+\\w?");
        for (String string : list) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    String[] split = string.split(" ");
                    for (String word : split) {
                        Matcher matcher = pattern.matcher(word);
                        if (matcher.find()) {
                            String email = matcher.group();
                            Long count = emailMap.get(email);
                            synchronized (ParseEmail.class){
                                if (count != null) {
                                    count++;
                                    emailMap.put(email, count);
                                } else {
                                    emailMap.put(email, 1L);
                                }
                            }

                        }
                    }

                    latch.countDown();
                }
            });
        }
        latch.await();
        for (Map.Entry<String, Long> entry : emailMap.entrySet()) {
            System.out.println("邮箱地址为 " + entry.getKey() + ", 出现次数为 " + entry.getValue() + "次");
        }
    }
}
