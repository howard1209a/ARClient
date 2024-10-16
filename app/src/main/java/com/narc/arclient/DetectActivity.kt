package com.narc.arclient

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.narc.arclient.databinding.ActivityDetectBinding
import com.narc.arclient.yolo.biz.ExecutorManager
import com.narc.arclient.yolo.biz.ImageLoadHandler
import com.narc.arclient.yolo.biz.OffloadHandler
import com.narc.arclient.yolo.biz.YoloModelHandler
import com.rayneo.arsdk.android.touch.TempleAction
import com.rayneo.arsdk.android.ui.activity.BaseMirrorActivity
import kotlinx.coroutines.launch


class DetectActivity : BaseMirrorActivity<ActivityDetectBinding>() {
    // 合目更新必须在ui线程执行
    val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImageLoadHandler.init(assets)
        YoloModelHandler.init(assets, cacheDir)
        OffloadHandler.init(this)


        initEvent()
    }

    private fun initEvent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                templeActionViewModel.state.collect {
                    when (it) {
                        is TempleAction.DoubleClick -> {
                            finish()
                        }
                        is TempleAction.Click -> {
                            setPictureVisible(false)
                            ExecutorManager.getInstance().submitImgDetectTask(
                                4,
                                "img" + (128 * Math.random() + 1).toInt() + ".jpg"
                            )

                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    fun showPicture(bitmap: Bitmap) {
        handler.post {
            mBindingPair.updateView {
                fullScreenImage.setImageBitmap(bitmap)
                fullScreenImage.isVisible = true
            }
        }
    }

    private fun setPictureVisible(visible: Boolean) {
        handler.post {
            mBindingPair.updateView {
                fullScreenImage.isVisible = visible
            }
        }
    }

    fun showOutcome(imageLoadTime: String, localComputeTime: String, offloadTime: String) {
        handler.post {
            mBindingPair.updateView {
                imageLoadTimeText.text = imageLoadTime
                imageLoadTimeText.isVisible = true

                localComputeTimeText.text = localComputeTime
                localComputeTimeText.isVisible = true

                offloadTimeText.text = offloadTime
                offloadTimeText.isVisible = true
            }
        }
    }
}

