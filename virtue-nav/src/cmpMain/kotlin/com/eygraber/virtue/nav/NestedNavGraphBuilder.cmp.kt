package com.eygraber.virtue.nav

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KClass
import kotlin.reflect.KType

public actual inline fun <reified T : Any> NavGraphBuilder.nestedGraph(
  startDestination: KClass<*>,
  typeMap: Map<KType, @JvmSuppressWildcards NavType<*>>,
  noinline enterTransition: (
    AnimatedContentTransitionScope<NavBackStackEntry>.() ->
    @JvmSuppressWildcards EnterTransition?
  )?,
  noinline exitTransition: (
    AnimatedContentTransitionScope<NavBackStackEntry>.() ->
    @JvmSuppressWildcards ExitTransition?
  )?,
  noinline popEnterTransition: (
    AnimatedContentTransitionScope<NavBackStackEntry>.() ->
    @JvmSuppressWildcards EnterTransition?
  )?,
  noinline popExitTransition: (
    AnimatedContentTransitionScope<NavBackStackEntry>.() ->
    @JvmSuppressWildcards ExitTransition?
  )?,
  noinline sizeTransform: (
    AnimatedContentTransitionScope<NavBackStackEntry>.() ->
    @JvmSuppressWildcards SizeTransform?
  )?,
  noinline builder: NavGraphBuilder.() -> Unit,
) {
  @Suppress("NotImplementedDeclaration")
  TODO("Not implemented yet; waiting for CMP navigation to support type safe APIs")
}

public actual inline fun <reified T : Any> NavGraphBuilder.nestedGraph(
  startDestination: Any,
  typeMap: Map<KType, @JvmSuppressWildcards NavType<*>>,
  noinline enterTransition: (
    AnimatedContentTransitionScope<NavBackStackEntry>.() ->
    @JvmSuppressWildcards EnterTransition?
  )?,
  noinline exitTransition: (
    AnimatedContentTransitionScope<NavBackStackEntry>.() ->
    @JvmSuppressWildcards ExitTransition?
  )?,
  noinline popEnterTransition: (
    AnimatedContentTransitionScope<NavBackStackEntry>.() ->
    @JvmSuppressWildcards EnterTransition?
  )?,
  noinline popExitTransition: (
    AnimatedContentTransitionScope<NavBackStackEntry>.() ->
    @JvmSuppressWildcards ExitTransition?
  )?,
  noinline sizeTransform: (
    AnimatedContentTransitionScope<NavBackStackEntry>.() ->
    @JvmSuppressWildcards SizeTransform?
  )?,
  noinline builder: NavGraphBuilder.() -> Unit,
) {
  @Suppress("NotImplementedDeclaration")
  TODO("Not implemented yet; waiting for CMP navigation to support type safe APIs")
}
