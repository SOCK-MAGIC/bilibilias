package com.imcys.bilibilias.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.imcys.bilibilias.core.datastore.AsCookieStoreDataSource
import com.imcys.bilibilias.core.datastore.UsersDataSource
import com.imcys.bilibilias.core.model.bilibilias.HomeBanner
import com.imcys.bilibilias.core.model.bilibilias.UpdateNotice
import com.imcys.bilibilias.core.network.repository.BiliBiliAsRepository
import com.imcys.bilibilias.core.network.repository.LoginRepository
import com.imcys.bilibilias.core.network.utils.TokenUtil
import com.imcys.bilibilias.feature.common.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DefaultHomeComponent @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    private val asRepository: BiliBiliAsRepository,
    private val loginRepository: LoginRepository,
    private val asCookieStoreDataSource: AsCookieStoreDataSource,
    private val usersDataSource: UsersDataSource,
) : HomeComponent, BaseViewModel<HomeEvent, HomeComponent.Model>(componentContext) {

    @Composable
    override fun models(events: Flow<HomeEvent>): HomeComponent.Model {
        var notice by remember { mutableStateOf(UpdateNotice()) }
        var banner by remember { mutableStateOf(HomeBanner()) }
        LaunchedEffect(Unit) {
            launch {
                banner = asRepository.getHomeBanner()
            }
            launch {
                notice = asRepository.getUpdateNotice()
            }
        }
        LaunchedEffect(Unit) {
            loginRepository.getBilibiliHome()
            val bar = loginRepository.导航栏用户信息()
            usersDataSource.setUserId(bar.mid)
            usersDataSource.setMixKey(TokenUtil.getBiliMixin(bar.imgKey, bar.subKey))
        }

        LaunchedEffect(Unit) {
            events.collect { event ->
                when (event) {
                    else -> Unit
                }
            }
        }
        return HomeComponent.Model(notice, banner)
    }

    @AssistedFactory
    interface Factory : HomeComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
        ): DefaultHomeComponent
    }
}
