package org.tensorflow.lite.examples.poseestimation

import android.media.MediaPlayer
import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.carousel_item.view.*

class CarouselHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val itemImage = itemView.item_imageview
    private val itemText = itemView.item_text
    private val itemSubText = itemView.item_subtext
    private val itemDuration = itemView.duration

    fun bindWithView(carouselItem: CarouselItem) {
        //이미지뷰에 이미지 로드(Glide API 사용)
        Glide.with(itemView).load(carouselItem.imageSrc).into(itemImage)
        itemText.text = carouselItem.text
        itemSubText.text = carouselItem.subtext

        //비디오 파일의 재생시간을 구함
        val mp: MediaPlayer = MediaPlayer.create(
            itemView.context,
            Uri.parse(
                "android.resource://" + itemView.context.packageName + "/" + carouselItem.videopath
            )
        )
        val duration = mp.duration / 1000
        val minutes = duration / 60
        val seconds = duration - (minutes * 60)
        mp.release()

        //비디오 재생시간을 설정
        itemDuration.text = "${"%d".format(minutes)}:${"%02d".format(seconds)}"

    }
}