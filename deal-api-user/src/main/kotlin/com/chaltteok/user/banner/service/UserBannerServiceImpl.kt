package com.chaltteok.user.banner.service

import com.chaltteok.core.repository.banner.BannerRepository
import com.chaltteok.user.banner.dto.BannerResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class UserBannerServiceImpl(
    private val bannerRepository: BannerRepository,
) : UserBannerService {

    @Transactional(readOnly = true)
    override fun getActiveBanners(): List<BannerResponse> =
        bannerRepository.findActiveBanners(LocalDate.now()).map { BannerResponse.from(it) }
}
