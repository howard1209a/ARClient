package com.narc.arclient.process.processor;

import com.narc.arclient.entity.RecognizeTask;
import com.narc.arclient.enums.TaskType;
import com.narc.arclient.network.RemoteRecognizeServiceStub;
import com.narc.arclient.process.Processor;

public class SendRemoteProcessor implements Processor<RecognizeTask, RecognizeTask> {
    private static final SendRemoteProcessor SEND_REMOTE_PROCESSOR = new SendRemoteProcessor();

    private SendRemoteProcessor() {
    }

    @Override
    public RecognizeTask process(RecognizeTask recognizeTask) {
        recognizeTask.recordTimeConsumeStart(TaskType.REMOTE);
        RemoteRecognizeServiceStub.getInstance().recognize(recognizeTask);
        return null;
    }

    public static SendRemoteProcessor getInstance() {
        return SEND_REMOTE_PROCESSOR;
    }
}
