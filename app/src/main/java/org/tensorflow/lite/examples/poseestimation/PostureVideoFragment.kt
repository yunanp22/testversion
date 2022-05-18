package org.tensorflow.lite.examples.poseestimation

import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_posture_video.*

class PostureVideoFragment : Fragment() {

    companion object {
        //객체 생성 시, 영상의 경로와 제목을 전달받음
        fun newInstance(video: Int, title: String, description: String): PostureVideoFragment {
            return PostureVideoFragment().apply {
                arguments = Bundle().apply {
                    putInt("video", video)
                    putString("title", title)
                    putString("description", description)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_posture_video, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString("title") ?: "샘플"
        val video = arguments?.getInt("video") ?: R.raw.sample6
        val description = arguments?.getString("description") ?: "설명 없음"

        //뷰가 생성될 때, 툴바의 제목을 설정
        val textView: TextView = view.findViewById(R.id.posturevideo_toolbar_title)
        textView.text = title

        //뷰가 생성될 때, 자세의 설명을 설정
        val descriptionTextView: TextView = view.findViewById(R.id.description_text)
        descriptionTextView.text = description

        //비디오 뷰를 설정
        val videoView: VideoView = view.findViewById(R.id.video)
        val mc = MediaController(activity)
        val path = "android.resource://" + activity?.packageName + "/" + video
        videoView.setVideoURI(Uri.parse(path))
        mc.setAnchorView(videoView)
        videoView.setMediaController(mc)
        videoView.requestFocus()
        videoView.start()

        //뒤로가기 버튼 클릭 리스너 설정
        posturevideo_toolbar.setNavigationOnClickListener {
            parentFragmentManager.beginTransaction().setCustomAnimations(
                R.anim.enter_from_left,
                R.anim.exit_to_right,
                R.anim.enter_from_right,
                R.anim.exit_to_left
            ).remove(this).commit()
        }

        //다음 버튼 클릭 리스너 설정
        nextbutton.setOnClickListener {
            val navigation: BottomNavigationView = view.rootView.findViewById(R.id.bottom_nav)
            navigation.selectedItemId = R.id.menu_record
        }
    }



}
