package org.tensorflow.lite.examples.poseestimation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() : SettingsFragment {
            return SettingsFragment()
        }
    }

    //메모리에 올라갔을 때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    //메인 액티비티에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    //뷰가 생성되었을 때
    //프레그먼트와 레이아웃을 연결해주는 파트
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        return view
    }
}