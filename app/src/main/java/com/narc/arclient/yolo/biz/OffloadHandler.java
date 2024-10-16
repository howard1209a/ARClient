package com.narc.arclient.yolo.biz;

import static android.content.ContentValues.TAG;
import static com.narc.arclient.enums.NetworkEnums.HOST;
import static com.narc.arclient.enums.NetworkEnums.PORT;
import static com.narc.arclient.enums.TaskType.COMPUTE_REMOTE;
import static com.narc.arclient.enums.TaskType.TRANSFER_2_LOCAL;
import static com.narc.arclient.enums.TaskType.TRANSFER_2_REMOTE;
import static com.narc.arclient.yolo.constant.constant.OFFLOAD_IP;
import static com.narc.arclient.yolo.constant.constant.OFFLOAD_PORT;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.google.protobuf.ByteString;
import com.narc.arclient.DetectActivity;
import com.narc.arclient.entity.Rectangle;
import com.narc.arclient.entity.RenderData;
import com.narc.arclient.network.Box;
import com.narc.arclient.network.RecognizeRequest;
import com.narc.arclient.network.RecognizeResponse;
import com.narc.arclient.network.RemoteRecognizeServiceGrpc;
import com.narc.arclient.network.TaskOffloadRequest;
import com.narc.arclient.network.TaskOffloadResponse;
import com.narc.arclient.process.processor.RenderProcessor;
import com.narc.arclient.yolo.model.Context;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class OffloadHandler implements Handler {
    private DetectActivity detectActivity;
    private RemoteRecognizeServiceGrpc.RemoteRecognizeServiceStub stub;
    private Map<Integer, String> namesMap;
    private Map<String, Integer> colorMap = new HashMap<>();
    private static OffloadHandler offloadHandler;

    private void initStub() {
        ManagedChannel mChannel = ManagedChannelBuilder.forAddress(OFFLOAD_IP, OFFLOAD_PORT).usePlaintext().build();
        stub = RemoteRecognizeServiceGrpc.newStub(mChannel);
    }

    private void initNamesMap() {
        String[] classes = {
                "person", "bicycle", "car", "motorcycle", "airplane", "bus", "train", "truck", "boat",
                "traffic light", "fire hydrant", "stop sign", "parking meter", "bench", "bird", "cat",
                "dog", "horse", "sheep", "cow", "elephant", "bear", "zebra", "giraffe", "backpack",
                "umbrella", "handbag", "tie", "suitcase", "frisbee", "skis", "snowboard", "sports ball",
                "kite", "baseball bat", "baseball glove", "skateboard", "surfboard", "tennis racket",
                "bottle", "wine glass", "cup", "fork", "knife", "spoon", "bowl", "banana", "apple",
                "sandwich", "orange", "broccoli", "carrot", "hot dog", "pizza", "donut", "cake", "chair",
                "couch", "potted plant", "bed", "dining table", "toilet", "tv", "laptop", "mouse",
                "remote", "keyboard", "cell phone", "microwave", "oven", "toaster", "sink", "refrigerator",
                "book", "clock", "vase", "scissors", "teddy bear", "hair drier", "toothbrush"
        };

        this.namesMap = new HashMap<>();
        for (int i = 0; i < classes.length; i++) {
            this.namesMap.put(i, classes[i]);
        }
    }

    private void initializeColorMap() {
        colorMap.put("person", Color.RED);             // 人
        colorMap.put("bicycle", Color.GREEN);          // 自行车
        colorMap.put("car", Color.BLUE);               // 汽车
        colorMap.put("motorcycle", Color.CYAN);        // 摩托车
        colorMap.put("airplane", Color.MAGENTA);       // 飞机
        colorMap.put("bus", Color.YELLOW);              // 公共汽车
        colorMap.put("train", Color.DKGRAY);           // 火车
        colorMap.put("truck", Color.LTGRAY);           // 卡车
        colorMap.put("boat", Color.GRAY);               // 船
        colorMap.put("traffic light", Color.rgb(255, 165, 0)); // 红绿灯
        colorMap.put("fire hydrant", Color.RED);       // 消防栓
        colorMap.put("stop sign", Color.RED);          // 停止标志
        colorMap.put("parking meter", Color.YELLOW);    // 停车计时器
        colorMap.put("bench", Color.rgb(139, 69, 19)); // 长椅
        colorMap.put("bird", Color.rgb(255, 192, 203)); // 鸟
        colorMap.put("cat", Color.rgb(255, 228, 196)); // 猫
        colorMap.put("dog", Color.rgb(210, 180, 140)); // 狗
        colorMap.put("horse", Color.rgb(205, 133, 63)); // 马
        colorMap.put("sheep", Color.rgb(255, 255, 224)); // 羊
        colorMap.put("cow", Color.WHITE);               // 牛
        colorMap.put("elephant", Color.rgb(128, 128, 128)); // 大象
        colorMap.put("bear", Color.rgb(139, 69, 19));  // 熊
        colorMap.put("zebra", Color.BLACK);             // 斑马
        colorMap.put("giraffe", Color.YELLOW);          // 长颈鹿
        colorMap.put("backpack", Color.rgb(0, 255, 0)); // 背包
        colorMap.put("umbrella", Color.rgb(128, 0, 128)); // 雨伞
        colorMap.put("handbag", Color.rgb(255, 20, 147)); // 手提包
        colorMap.put("tie", Color.rgb(255, 105, 180)); // 领带
        colorMap.put("suitcase", Color.rgb(139, 0, 139)); // 行李箱
        colorMap.put("frisbee", Color.rgb(255, 0, 255)); // 飞盘
        colorMap.put("skis", Color.rgb(240, 230, 140)); // 滑雪板
        colorMap.put("snowboard", Color.rgb(255, 140, 0)); // 单板滑雪
        colorMap.put("sports ball", Color.rgb(255, 215, 0)); // 体育球
        colorMap.put("kite", Color.rgb(173, 216, 230)); // 风筝
        colorMap.put("baseball bat", Color.rgb(160, 82, 45)); // 棒球棒
        colorMap.put("baseball glove", Color.rgb(205, 133, 63)); // 棒球手套
        colorMap.put("skateboard", Color.rgb(127, 255, 0)); // 滑板
        colorMap.put("surfboard", Color.rgb(0, 255, 255)); // 冲浪板
        colorMap.put("tennis racket", Color.rgb(210, 180, 140)); // 网球拍
        colorMap.put("bottle", Color.rgb(0, 128, 0)); // 瓶子
        colorMap.put("wine glass", Color.rgb(255, 0, 0)); // 酒杯
        colorMap.put("cup", Color.rgb(0, 0, 255)); // 杯子
        colorMap.put("fork", Color.rgb(255, 255, 0)); // 叉子
        colorMap.put("knife", Color.rgb(128, 0, 0)); // 刀
        colorMap.put("spoon", Color.rgb(192, 192, 192)); // 勺子
        colorMap.put("bowl", Color.rgb(128, 128, 0)); // 碗
        colorMap.put("banana", Color.rgb(255, 255, 224)); // 香蕉
        colorMap.put("apple", Color.RED);               // 苹果
        colorMap.put("sandwich", Color.rgb(255, 222, 173)); // 三明治
        colorMap.put("orange", Color.rgb(255, 165, 0)); // 橙子
        colorMap.put("broccoli", Color.rgb(0, 255, 0)); // 西兰花
        colorMap.put("carrot", Color.rgb(255, 165, 0)); // 胡萝卜
        colorMap.put("hot dog", Color.rgb(255, 0, 0)); // 热狗
        colorMap.put("pizza", Color.rgb(255, 192, 203)); // 比萨
        colorMap.put("donut", Color.rgb(255, 105, 180)); // 甜甜圈
        colorMap.put("cake", Color.rgb(255, 228, 196)); // 蛋糕
        colorMap.put("chair", Color.rgb(139, 69, 19)); // 椅子
        colorMap.put("couch", Color.rgb(255, 228, 196)); // 沙发
        colorMap.put("potted plant", Color.rgb(0, 128, 0)); // 盆栽
        colorMap.put("bed", Color.rgb(255, 228, 196)); // 床
        colorMap.put("dining table", Color.rgb(139, 69, 19)); // 餐桌
        colorMap.put("toilet", Color.rgb(255, 255, 0)); // 厕所
        colorMap.put("tv", Color.BLACK);                // 电视
        colorMap.put("laptop", Color.rgb(192, 192, 192)); // 笔记本电脑
        colorMap.put("mouse", Color.rgb(128, 128, 128)); // 鼠标
        colorMap.put("remote", Color.rgb(255, 20, 147)); // 遥控器
        colorMap.put("keyboard", Color.rgb(0, 0, 255)); // 键盘
        colorMap.put("cell phone", Color.rgb(255, 105, 180)); // 手机
        colorMap.put("microwave", Color.rgb(128, 128, 128)); // 微波炉
        colorMap.put("oven", Color.rgb(255, 165, 0)); // 烤箱
        colorMap.put("toaster", Color.rgb(255, 222, 173)); // 烤面包机
        colorMap.put("sink", Color.rgb(0, 128, 0)); // 水槽
        colorMap.put("refrigerator", Color.rgb(0, 0, 255)); // 冰箱
        colorMap.put("book", Color.rgb(255, 228, 196)); // 书
        colorMap.put("clock", Color.rgb(255, 255, 0)); // 时钟
        colorMap.put("vase", Color.rgb(255, 0, 255)); // 花瓶
        colorMap.put("scissors", Color.rgb(192, 192, 192)); // 剪刀
        colorMap.put("teddy bear", Color.rgb(139, 69, 19)); // 泰迪熊
        colorMap.put("hair drier", Color.rgb(255, 192, 203)); // 吹风机
        colorMap.put("toothbrush", Color.rgb(255, 105, 180)); // 牙刷
    }

    public static void init(DetectActivity detectActivity) {
        offloadHandler = new OffloadHandler(detectActivity);
    }

    public static OffloadHandler getInstance() {
        return offloadHandler;
    }

    private OffloadHandler(DetectActivity detectActivity) {
        this.detectActivity = detectActivity;
        initStub();
        initNamesMap();
        initializeColorMap();
    }

    @Override
    public Context handle(Context ctx) {
        long startTime = System.currentTimeMillis();

        float[] floatArray = ctx.outputTensor.getDataAsFloatArray();
        List<Float> floatList = new ArrayList<>(floatArray.length);
        for (float f : floatArray) {
            floatList.add(f);
        }

        TaskOffloadRequest req = TaskOffloadRequest.newBuilder()
                .setTaskID(ctx.taskID)
                .setIndex(ctx.localComputeCount)
                .addAllTensorData(floatList)
                .build();

        // 所有回调在 gRPC 库管理的线程池中执行
        stub.taskOffload(req, new StreamObserver<TaskOffloadResponse>() {
            @Override
            public void onNext(TaskOffloadResponse value) {
                List<Box> boxes = value.getBoxesList();
                Bitmap finalBitmap = drawBoxesOnBitmap(ctx.originBitmap, boxes);
                ctx.finalBitmap = finalBitmap;

                detectActivity.showPicture(finalBitmap);

                long endTime = System.currentTimeMillis();
                ctx.outcomeAssessment.offloadTime = endTime - startTime;

                String imageLoadTime = "图像加载" + ctx.outcomeAssessment.imageLoadTime + "ms";
                String localComputeTime = "本地计算" + ctx.outcomeAssessment.localComputeTime + "ms";
                String offloadTime = "卸载" + ctx.outcomeAssessment.offloadTime + "ms";
                detectActivity.showOutcome(imageLoadTime, localComputeTime, offloadTime);
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, t.toString());
            }

            @Override
            public void onCompleted() {

            }
        });


        return ctx;
    }

    private Bitmap drawBoxesOnBitmap(Bitmap originBitmap, List<Box> boxes) {
        // 创建一个可变的 Bitmap
        Bitmap bitmapWithBoxes = originBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmapWithBoxes);
        Paint paint = new Paint();

        // 设置绘制矩形框的颜色和宽度
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        // 设置绘制文本的颜色和大小
        paint.setTextSize(40);

        // 遍历 Box 列表，绘制矩形框和文本
        for (Box box : boxes) {
            String cls = this.namesMap.get((int) box.getCls());
            paint.setColor(colorMap.get(cls));

            // 绘制矩形框
            canvas.drawRect(box.getX1(), box.getY1(), box.getX2(), box.getY2(), paint);

            // 绘制置信度和类别信息
            String label = String.format("%.2f %s", box.getConf(), cls);
            canvas.drawText(label, box.getX1(), box.getY1() - 10, paint); // 在矩形框上方绘制文本
        }

        return bitmapWithBoxes;
    }
}
