package org.tensorflow.lite.examples.poseestimation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_practice_list.*


class PracticeListFragment : Fragment() {

    private lateinit var postureVideoFragment: PostureVideoFragment

    companion object {
        fun newInstance() : PracticeListFragment {
            return PracticeListFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_practice_list, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //뒤로가기 버튼 클릭 리스너 설정
        toolbar.setNavigationOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        //자세 별 상세 설명 텍스트
        var addressDescription: String = "볼을 투구하기 전 도움닫기 동작을 말합니다.\n" +
                "어드레스 위치는 파울선에서 자신의 평소 보폭으로 4보 반을 걸어간 자리입니다.\n" +
                "자세는 시선을 에이밍 스폿에 고정하고 무릎을 3~5도 구부립니다.\n" +
                "공을 잡은 팔은 어깨로부터 90도를 유지해야 다음 동작으로 이어질 때 흔들림이 없습니다.\n" +
                "투구 하지 않는 손은 공을 가볍게 받쳐줍니다.\n" +
                "양발은 볼의 진행 방향으로 향하게 하고, 첫 스텝의 발이 10cm정도 앞에 놓아서 몸의 중심을 유지합니다.\n"
        var pushawayDescription: String = "1. 볼을 자신의 주 손으로 가볍게 스윙선 상으로 밀어냅니다.\n" +
                "2. 팔꿈치를 펴고 팔을 뻗어 내립니다.\n" +
                "3. 눈은 계속 선택한 에이밍 스팟만 바라봅니다.\n" +
                "4. 무릎을 구부리며 상체를 약간 앞으로 내밉니다.\n" +
                "5. 호흡을 조정하고 가볍게 첫발을 뒤꿈치를 끌면서 짧게 내딛습니다.\n"
        var downswingDescription: String = "1. 볼은 계속 스윙선상을 벗어나지 않도록 합니다.\n" +
                "2. 볼을 잡은 엄지의 방향은 스탠스때와 같이 합니다.\n" +
                "3. 손목이 돌아가거나 제쳐지지 않도록 합니다.\n" +
                "4. 볼을 팔의 힘으로 끌어 내리지 않도록 합니다.\n" +
                "5. 왼팔은 몸의 밸런스를 위해 옆으로 뻗어내기 시작합니다.\n" +
                "6. 제2스텝도 짧게 발뒤꿈치부터 끌면서 내딛습니다.\n"

        //어드레스 자세 버튼 클릭 리스너 설정
        address_button.setOnClickListener {
            postureVideoFragment = PostureVideoFragment.newInstance(R.raw.sample1, address_name.text.toString(), addressDescription)
            parentFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left,R.anim.exit_to_right).add(R.id.fragments_frame, postureVideoFragment).commit()

        }
        //푸쉬어웨이 자세 버튼 클릭 리스너 설정
        pushaway_button.setOnClickListener {
            postureVideoFragment = PostureVideoFragment.newInstance(R.raw.sample1, pushaway_name.text.toString(), pushawayDescription)
            parentFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left,R.anim.exit_to_right).add(R.id.fragments_frame, postureVideoFragment).commit()

        }
        //다운스윙 자세 버튼 클릭 리스너 설정
        downswing_button.setOnClickListener {
            postureVideoFragment = PostureVideoFragment.newInstance(R.raw.sample1, downswing_name.text.toString(), downswingDescription)
            parentFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left,R.anim.exit_to_right).add(R.id.fragments_frame, postureVideoFragment).commit()

        }
    }
}

