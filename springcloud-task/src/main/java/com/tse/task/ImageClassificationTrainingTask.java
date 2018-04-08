package com.tse.task;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class ImageClassificationTrainingTask implements CommandLineRunner, Ordered {

    @Value("${batPath}")
    private String batPath;

    @Override
    public void run(String... strings) throws Exception {
        System.out.println("start");
        run_cmd("cmd /c start " + batPath);
        System.out.println("done");
    }

    public void run_cmd(String strcmd) {
        Runtime rt = Runtime.getRuntime(); //Runtime.getRuntime()返回当前应用程序的Runtime对象
        Process ps = null;  //Process可以控制该子进程的执行或获取该子进程的信息。
        try {
            ps = rt.exec(strcmd);   //该对象的exec()方法指示Java虚拟机创建一个子进程执行指定的可执行程序，并返回与该子进程对应的Process对象实例。
            ps.waitFor();  //等待子进程完成再往下执行。
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int i = ps.exitValue();  //接收执行完毕的返回值
        if (i == 0) {
            System.out.println("执行完成.");
        } else {
            System.out.println("执行失败.");
        }
        ps.destroy();  //销毁子进程
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
