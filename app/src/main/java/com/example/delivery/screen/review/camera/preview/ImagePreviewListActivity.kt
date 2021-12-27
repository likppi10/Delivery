package com.example.delivery.screen.review.camera.preview

import android.app.Activity
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.example.delivery.R
import com.example.delivery.databinding.ActivityImagePreviewListBinding
import com.example.delivery.util.path.PathUtil
import com.example.delivery.widget.adapter.ImageViewPagerAdapter
import java.io.File
import java.io.FileNotFoundException

// 찍은 사진들 뷰페이져로
class ImagePreviewListActivity : AppCompatActivity() {

    companion object {
        private const val URI_LIST_KEY = "uriList"

        fun newIntent(activity: Activity, uriList: List<Uri>) =
            Intent(activity, ImagePreviewListActivity::class.java).apply {
                putExtra(URI_LIST_KEY, ArrayList<Uri>().apply { uriList.forEach { add(it) } })
            }
    }

    private lateinit var binding: ActivityImagePreviewListBinding
    private val uriList by lazy<List<Uri>> { intent.getParcelableArrayListExtra(URI_LIST_KEY)!! }
    private lateinit var imageViewPagerAdapter: ImageViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImagePreviewListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        setSupportActionBar(binding.toolbar)
        setupImageList(uriList)
    }

    /* 3. 내정보 탭
    3-3-2. 갤러리, 카메라
    *  촬영한 이미지들로 리스트를 만든다.
    */
    private fun setupImageList(uriList: List<Uri>) = with(binding) {
        if (::imageViewPagerAdapter.isInitialized.not()) {
            imageViewPagerAdapter = ImageViewPagerAdapter(uriList)
        }
        imageViewPager.adapter = imageViewPagerAdapter
        indicator.setViewPager(imageViewPager)
        imageViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                toolbar.title = getString(R.string.images_page, position + 1, imageViewPagerAdapter.uriList.size)
            }
        })
        deleteButton.setOnClickListener {
            removeImage(uriList[imageViewPager.currentItem])
        }
        confirmButton.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra(URI_LIST_KEY, ArrayList<Uri>().apply { imageViewPagerAdapter.uriList.forEach { add(it) } })
            })
            finish()
        }
    }

    /* 3. 내정보 탭
    3-3-2. 갤러리, 카메라
    *  찍은 사진들 중 클릭하여 삭제
    */
    private fun removeImage(uri: Uri) {
        val file = File(PathUtil.getPath(this, uri) ?: throw FileNotFoundException())
        file.delete()
        imageViewPagerAdapter.uriList.let {
            val imageList = it.toMutableList()
            imageList.remove(uri)
            imageViewPagerAdapter.uriList = imageList
            imageViewPagerAdapter.notifyDataSetChanged()
        }
        binding.indicator.setViewPager(binding.imageViewPager)
        MediaScannerConnection.scanFile(this, arrayOf(file.path), arrayOf("image/jpeg"), null)
        if (imageViewPagerAdapter.uriList.isEmpty()) {
            Toast.makeText(this, "삭제할 수 있는 이미지가 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

}
