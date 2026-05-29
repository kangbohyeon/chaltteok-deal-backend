package com.chaltteok.owner.banner.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.domain.Banner
import com.chaltteok.core.repository.banner.BannerRepository
import com.chaltteok.owner.banner.dto.BannerRequest
import com.chaltteok.owner.banner.dto.BannerResponse
import com.chaltteok.owner.banner.enums.BannerErrorCode
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OwnerBannerServiceImpl(
    private val bannerRepository: BannerRepository,
) : OwnerBannerService {

    @Transactional(readOnly = true)
    override fun getAll(): List<BannerResponse> =
        bannerRepository.findAll(Sort.by(Sort.Direction.ASC, "sortOrder").and(Sort.by(Sort.Direction.DESC, "createdAt")))
            .map { BannerResponse.from(it) }

    @Transactional
    override fun create(request: BannerRequest) {
        val banner = Banner(
            title = request.title,
            subtitle = request.subtitle,
            imageUrl = request.imageUrl,
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
    override fun update(bannerUuid: String, request: BannerRequest) {
        val banner = bannerRepository.findByBannerUuid(bannerUuid)
            ?: throw BusinessException(BannerErrorCode.BANNER_NOT_FOUND)
        banner.title = request.title
        banner.subtitle = request.subtitle
        banner.imageUrl = request.imageUrl
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
        bannerRepository.delete(banner)
    }
}
