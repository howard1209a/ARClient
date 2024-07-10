package com.narc.arclient.process.processor;

import com.narc.arclient.entity.RecognizeTask;
import com.narc.arclient.enums.TaskType;
import com.narc.arclient.network.RemoteRecognizeServiceStub;
import com.narc.arclient.process.Processor;

public class LogRemoteProcessor implements Processor<RecognizeTask, RecognizeTask> {
    private static final LogRemoteProcessor LOG_REMOTE_PROCESSOR = new LogRemoteProcessor();

    @Override
    public RecognizeTask process(RecognizeTask recognizeTask) {
        // 打印本次任务各部分耗时
        recognizeTask.timeConsumeLog();

        // 日志上报
        RemoteRecognizeServiceStub.getInstance().logReport(recognizeTask);
        return null;
    }

    public static LogRemoteProcessor getInstance() {
        return LOG_REMOTE_PROCESSOR;
    }
}
