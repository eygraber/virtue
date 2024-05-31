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

public expect inline fun <reified T : Any> NavGraphBuilder.nestedGraph(
  startDestination: KClass<*>,
  typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
  noinline enterTransition: (
    AnimatedContentTransitionScope<NavBackStackEntry>.() ->
    @JvmSuppressWildcards EnterTransition?
  )? = null,
  noinline exitTransition: (
    AnimatedContentTransitionScope<NavBackStackEntry>.() ->
    @JvmSuppressWildcards ExitTransition?
  )? = null,
  noinline popEnterTransition: (
    AnimatedContentTransitionScope<NavBackStackEntry>.() ->
    @JvmSuppressWildcards EnterTransition?
  )? = enterTransition,
  noinline popExitTransition: (
    AnimatedContentTransitionScope<NavBackStackEntry>.() ->
    @JvmSuppressWildcards ExitTransition?
  )? = exitTransition,
  noinline sizeTransform: (
    AnimatedContentTransitionScope<NavBackStackEntry>.() ->
    @JvmSuppressWildcards SizeTransform?
  )? = null,
  noinline builder: NavGraphBuilder.() -> Unit,
)

public expect inline fun <reified T : Any> NavGraphBuilder.nestedGraph(
  startDestination: Any,
  typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
  noinline enterTransition: (
    AnimatedContentTransitionScope<NavBackStackEntry>.() ->
    @JvmSuppressWildcards EnterTransition?
  )? = null,
  noinline exitTransition: (
    AnimatedContentTransitionScope<NavBackStackEntry>.() ->
    @JvmSuppressWildcards ExitTransition?
  )? = null,
  noinline popEnterTransition: (
    AnimatedContentTransitionScope<NavBackStackEntry>.() ->
    @JvmSuppressWildcards EnterTransition?
  )? = enterTransition,
  noinline popExitTransition: (
    AnimatedContentTransitionScope<NavBackStackEntry>.() ->
    @JvmSuppressWildcards ExitTransition?
  )? = exitTransition,
  noinline sizeTransform: (
    AnimatedContentTransitionScope<NavBackStackEntry>.() ->
    @JvmSuppressWildcards SizeTransform?
  )? = null,
  noinline builder: NavGraphBuilder.() -> Unit,
)
