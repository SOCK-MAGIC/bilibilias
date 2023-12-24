package com.imcys.bilibilias

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.bilias.feature.download.navigation.downloadRoute
import com.imcys.authentication.login.navigation.loginAuthRoute
import com.imcys.authentication.login.navigation.navigateToLoginAuth
import com.imcys.authentication.method.navigation.authMethodRoute
import com.imcys.authentication.method.navigation.navigateToAuthMethod
import com.imcys.bilias.feature.merge.navigation.mergeRoute
import com.imcys.bilias.feature.merge.navigation.navigateToMerge
import com.imcys.bilibilias.tool.navigation.bangumiFollowRoute
import com.imcys.bilibilias.tool.navigation.navigateToBangumiFollow
import com.imcys.bilibilias.tool.navigation.toolScreen
import com.imcys.bilibilias.ui.splash.navigation.ROUTE_SPLASH
import com.imcys.bilibilias.ui.splash.navigation.splashRoute
import com.imcys.home.navigation.contributeScreen
import com.imcys.home.navigation.donationScreen
import com.imcys.home.navigation.homeScreen
import com.imcys.home.navigation.navigateToContribute
import com.imcys.home.navigation.navigateToDonation
import com.imcys.home.navigation.navigateToHome
import com.imcys.player.download.danmaku.danmakuRoute
import com.imcys.player.download.danmaku.navigateToDanmaku
import com.imcys.player.navigation.navigateToPlayer
import com.imcys.player.navigation.playerScreen
import com.imcys.setting.navigation.navigateToSettings
import com.imcys.setting.navigation.settingsRoute
import com.imcys.space.navigation.collectionDownloadRoute
import com.imcys.space.navigation.navigateToCollectionDownload
import com.imcys.space.navigation.navigateToUserSpace
import com.imcys.space.navigation.userSpaceRoute
import com.imcys.user.navigation.userRoute

const val ROUTE_MAIN_SCREEN = "main_screen"

@Composable
fun MainScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    BILIBILIASAnimatedNavHost(
        modifier = modifier,
        navController = navController,
        startDestination = ROUTE_SPLASH,
        route = ROUTE_MAIN_SCREEN,
    ) {
        // 启动页
        // -----------------------------------------------------------------------------------------
        splashRoute(
            navigateToAuthMethod = navController::navigateToAuthMethod,
            navigateToHome = navController::navigateToHome
        )
        // region 登录认证
        // -----------------------------------------------------------------------------------------
        authMethodRoute(navigateToLoginAuth = navController::navigateToLoginAuth)
        loginAuthRoute(navigateToHome = navController::navigateToHome)
        // endregion

        // -----------------------------------------------------------------------------------------
        homeScreen(
            navigationToDonation = navController::navigateToDonation,
            navigateToContribute = navController::navigateToContribute
        )
        donationScreen()
        contributeScreen()

        // -----------------------------------------------------------------------------------------
        toolScreen(
            navigateToPlayer = navController::navigateToPlayer,
            navigateToSetting = navController::navigateToSettings,
            navigateToExportBangumiFollowList = navController::navigateToBangumiFollow,
            navigationToMerge = navController::navigateToMerge
        )
        settingsRoute()
        bangumiFollowRoute()
        mergeRoute()

        playerScreen(
            navigateToDownloadAanmaku = navController::navigateToDanmaku,
            navigateToUserSpace = navController::navigateToUserSpace
        )
        danmakuRoute(navController = navController)
        userSpaceRoute(navigateToCollectionDownload = navController::navigateToCollectionDownload)
        collectionDownloadRoute()

        // -----------------------------------------------------------------------------------------
        downloadRoute()

        // -----------------------------------------------------------------------------------------
        userRoute(onNavigateTo = {}, onBack = navController::navigateUp)
    }
}

@Composable
fun BILIBILIASAnimatedNavHost(
    modifier: Modifier,
    navController: NavHostController,
    startDestination: String,
    route: String,
    builder: NavGraphBuilder.() -> Unit
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            activityEnterTransition()
        },
        exitTransition = {
            activityExitTransition()
        },
        popEnterTransition = {
            activityPopEnterTransition()
        },
        popExitTransition = {
            activityPopExitTransition()
        },
        route = route,
        builder = builder
    )
}

// region 动画配置

private fun AnimatedContentTransitionScope<NavBackStackEntry>.activityEnterTransition(): EnterTransition {
    return slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Start,
        animationSpec = tween(DEFAULT_ENTER_DURATION, easing = LinearOutSlowInEasing),
        initialOffset = { it }
    )
}

@Suppress("UnusedReceiverParameter")
private fun AnimatedContentTransitionScope<NavBackStackEntry>.activityExitTransition(): ExitTransition {
    return scaleOut(
        animationSpec = tween(DEFAULT_ENTER_DURATION),
        targetScale = 0.96F
    )
}

@Suppress("UnusedReceiverParameter")
private fun AnimatedContentTransitionScope<NavBackStackEntry>.activityPopEnterTransition(): EnterTransition {
    return scaleIn(
        animationSpec = tween(DEFAULT_EXIT_DURATION),
        initialScale = 0.96F
    )
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.activityPopExitTransition(): ExitTransition {
    return slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.End,
        animationSpec = tween(DEFAULT_EXIT_DURATION, easing = FastOutLinearInEasing),
        targetOffset = { it }
    )
}
// endregion

private const val DEFAULT_ENTER_DURATION = 300
private const val DEFAULT_EXIT_DURATION = 220
