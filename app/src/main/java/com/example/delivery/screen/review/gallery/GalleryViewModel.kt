package com.example.delivery.screen.review.gallery

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class GalleryViewModel(
    private val galleryPhotoRepository: GalleryPhotoRepository
) : ViewModel() {

    private lateinit var photoList: MutableList<GalleryPhoto>

    val galleryStateLiveData = MutableLiveData<GalleryState>(GalleryState.Uninitialized)

    fun fetchData() = viewModelScope.launch {
        setState(
            GalleryState.Loading
        )
        photoList = galleryPhotoRepository.getAllPhotos()
        setState(
            GalleryState.Success(
                photoList = photoList
            )
        )
    }

    /* 3. 내정보 탭
    3-3-2. 갤러리, 카메라
    *  이미지 선택 or 취소
    */
    fun selectPhoto(galleryPhoto: GalleryPhoto) {
        val findGalleryPhoto = photoList.find { it.id == galleryPhoto.id }
        findGalleryPhoto?.let { photo ->
            photoList[photoList.indexOf(photo)] =
                photo.copy(
                    isSelected = photo.isSelected.not()
                )
            setState(
                GalleryState.Success(
                    photoList = photoList
                )
            )
        }
    }

    /* 3. 내정보 탭
    3-3-2. 갤러리, 카메라
    *  선택한 이미지들로 결정
    */
    fun confirmCheckedPhotos() {
        setState(
            GalleryState.Loading
        )
        setState(
            GalleryState.Confirm(
                photoList = photoList.filter { it.isSelected }
            )
        )
    }

    private fun setState(state: GalleryState) {
        galleryStateLiveData.postValue(state)
    }

}
