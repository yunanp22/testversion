package org.tensorflow.lite.examples.poseestimation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private lateinit var practiceListFragment: PracticeListFragment
    private lateinit var gripFragment: GripFragment
    private lateinit var postureVideoFragment: PostureVideoFragment

    companion object {
        fun newInstance() : HomeFragment {
            return HomeFragment()
        }
    }

    private var carouselList = ArrayList<CarouselItem>()
    private lateinit var carouselAdapter: CarouselAdapter

    //뷰가 생성되었을 때
    //프레그먼트와 레이아웃을 연결해주는 파트
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //캐러셀 데이터 배열을 준비
        carouselList.add(CarouselItem(R.raw.sample1, R.drawable.bowling1,"볼링 기초 1", "입문 과정 / 홍길동"))
        carouselList.add(CarouselItem(R.raw.sample2, R.drawable.bowling2,"볼링 기초 2", "입문 과정 / 밤톨이"))
        carouselList.add(CarouselItem(R.raw.sample3, R.drawable.bowling3,"볼링 중급", "심화 과정 / 손흥민"))
        carouselList.add(CarouselItem(R.raw.sample4, R.drawable.bowling4,"볼링 고급", "심화 과정 / 홍길동"))
        carouselList.add(CarouselItem(R.raw.sample5, R.drawable.bowling1,"볼링 고급", "심화 과정 / 홍길동"))
        carouselList.add(CarouselItem(R.raw.sample6, R.drawable.bowling3,"볼링 고급", "심화 과정 / 홍길동"))

        //캐러셀 어뎁터 인스턴스 설정 및 클릭 리스너 설정
        carouselAdapter = CarouselAdapter(carouselList) {
            var videoID: Int = it.videopath
            var title: String = it.text

            postureVideoFragment = PostureVideoFragment.newInstance(videoID, title, "설명 없음.\n")
            parentFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left,R.anim.exit_to_right).add(R.id.fragments_frame, postureVideoFragment).commit()
        }

        //캐러셀 아이템 여백 및 크기 설정
        val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.pageMargin)
        val pagerWidth = resources.getDimensionPixelOffset(R.dimen.pageWidth)
        val screenWidth = resources.displayMetrics.widthPixels
        val offsetPx = screenWidth - pageMarginPx - pagerWidth

        viewpager.setPageTransformer { page, position ->
            page.translationX = position * -offsetPx
        }

        //캐러셀 아이템 하나를 미리 로드
        viewpager.offscreenPageLimit = 1

        viewpager.apply {
            adapter = carouselAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            //불필요한 스크롤 애니메이션 삭제
            (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }

        //두번째 아이템이 가장 먼저 나오도록 설정
        viewpager.currentItem = 1

        //그립 버튼 클릭 리스너 설정
        home_grip_button.setOnClickListener {
            onHomeGripButtonClicked()
        }

        //자세 버튼 클릭 리스너 설정
        home_posture_button.setOnClickListener {
            onHomePostureButtonClicked()
        }

    }

    //그립 버튼 클릭 리스너 정의
    private fun onHomeGripButtonClicked() {
        gripFragment = GripFragment.newInstance()
        parentFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left,R.anim.exit_to_right).add(R.id.fragments_frame, gripFragment).commit()
    }

    //자세 버튼 클릭 리스너 정의
    private fun onHomePostureButtonClicked() {
        practiceListFragment = PracticeListFragment.newInstance()
        parentFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left,R.anim.exit_to_right).add(R.id.fragments_frame, practiceListFragment).addToBackStack(null).commit()
    }

}

