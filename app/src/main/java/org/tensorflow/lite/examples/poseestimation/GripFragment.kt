package org.tensorflow.lite.examples.poseestimation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_practice_list.toolbar

class GripFragment : Fragment() {

    companion object {
        fun newInstance() : GripFragment {
            return GripFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_grip, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //뒤로가기 버튼 클릭 리스너
        toolbar.setNavigationOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

    }
}

