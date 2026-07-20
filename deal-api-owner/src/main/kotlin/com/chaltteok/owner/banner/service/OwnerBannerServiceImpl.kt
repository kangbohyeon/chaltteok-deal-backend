package com.chaltteok.owner.banner.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.domain.Banner
import com.chaltteok.core.repository.banner.BannerRepository
import com.chaltteok.owner.banner.dto.BannerRequest
import com.chaltteok.owner.banner.dto.BannerResponse
import com.chaltteok.owner.banner.enums.BannerErrorCode
import com.chaltteok.owner.product.util.LocalFileUploader
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class OwnerBannerServiceImpl(
    private val bannerRepository: BannerRepository,
    private val localFileUploader: LocalFileUploader,
) : OwnerBannerService {

    @Transactional(readOnly = true)
    override fun getAll(): List<BannerResponse> =
        bannerRepository.findAll(Sort.by(Sort.Direction.ASC, "sortOrder").and(Sort.by(Sort.Direction.DESC, "createdAt")))
            .map { BannerResponse.from(it) }

    @Transactional(readOnly = true)
    override fun getBanner(bannerUuid: String): BannerResponse {
        val banner = bannerRepository.findByBannerUuid(bannerUuid)
            ?: throw BusinessException(BannerErrorCode.BANNER_NOT_FOUND)
        return BannerResponse.from(banner)
    }

    @Transactional
    override fun create(request: BannerRequest, image: MultipartFile?) {
        val imageUrl = image?.takeIf { !it.isEmpty }?.let { localFileUploader.uploadFile(it, "banner") }
        val banner = Banner(
            title = request.title,
            subtitle = request.subtitle,
            imageUrl = imageUrl,
            linkUrl = request.linkUrl,
            backgroundColor = request.backgroundColor,
            sortOrder = request.sortOrder,
            isVisible = request.isVisible,
            startDate = request.startDate,
            endDate = request.endDate,
        )
        bannerRepository.save(banner)
    }

    @Transactional
    override fun update(bannerUuid: String, request: BannerRequest, image: MultipartFile?) {
        val banner = bannerRepository.findByBannerUuid(bannerUuid)
            ?: throw BusinessException(BannerErrorCode.BANNER_NOT_FOUND)
        val newImageUrl = image?.takeIf { !it.isEmpty }?.let { newImage ->
            val uploaded = localFileUploader.uploadFile(newImage, "banner")
            banner.imageUrl?.let { localFileUploader.deleteFile(it) }
            uploaded
        }
        banner.title = request.title
        banner.subtitle = request.subtitle
        if (newImageUrl != null) banner.imageUrl = newImageUrl
        banner.linkUrl = request.linkUrl
        banner.backgroundColor = request.backgroundColor
        banner.sortOrder = request.sortOrder
        banner.isVisible = request.isVisible
        banner.startDate = request.startDate
        banner.endDate = request.endDate
    }

    @Transactional
    override fun delete(bannerUuid: String) {
        val banner = bannerRepository.findByBannerUuid(bannerUuid)
            ?: throw BusinessException(BannerErrorCode.BANNER_NOT_FOUND)
        banner.imageUrl?.let { localFileUploader.deleteFile(it) }
        bannerRepository.delete(banner)
    }
}
