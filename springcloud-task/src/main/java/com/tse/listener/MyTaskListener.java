package com.tse.listener;

import org.apache.log4j.Logger;
import org.springframework.cloud.task.listener.annotation.AfterTask;
import org.springframework.cloud.task.listener.annotation.BeforeTask;
import org.springframework.cloud.task.listener.annotation.FailedTask;
import org.springframework.cloud.task.repository.TaskExecution;
import org.springframework.stereotype.Component;

@Component
public class MyTaskListener {

    private Logger logger = Logger.getLogger(MyTaskListener.class);

    @BeforeTask
    public void methodA(TaskExecution taskExecution) {
        logger.info("Start training image classification model.");
    }

    @AfterTask
    public void methodB(TaskExecution taskExecution) {
        logger.info("The training image classification model is completed.");
    }

    @FailedTask
    public void methodC(TaskExecution taskExecution, Throwable throwable) {
        logger.error("The training image classification model failed.");
    }
}
