package org.tensorflow.lite.examples.poseestimation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_history.view.*

class HistoryFragment : Fragment() {

    companion object {
        fun newInstance() : HistoryFragment {
            return HistoryFragment()
        }
    }

    //메모리에 올라갔을 때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    //뷰가 생성되었을 때
    //프레그먼트와 레이아웃을 연결해주는 파트
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        val list_array = arrayListOf<Posture>(
            Posture("bowling", "자세1", "2022년 4월 22일", "정확도 91%"),
            Posture("bowling", "자세2", "2022년 4월 23일", "정확도 77%"),
            Posture("bowling", "자세3", "2022년 4월 24일", "정확도 80%"),
            Posture("bowling", "자세4", "2022년 4월 24일", "정확도 88%"),
            Posture("bowling", "자세5", "2022년 4월 27일", "정확도 90%"),
            Posture("bowling", "자세6", "2022년 4월 28일", "정확도 97%"),
            Posture("bowling", "자세7", "2022년 5월 1일", "정확도 33%"),
            Posture("bowling", "자세8", "2022년 5월 5일", "정확도 71%")
        )
        val list_adapter = HistoryListAdapter(requireContext(), list_array)
        view.listview_history.adapter = list_adapter


        return view
    }
}