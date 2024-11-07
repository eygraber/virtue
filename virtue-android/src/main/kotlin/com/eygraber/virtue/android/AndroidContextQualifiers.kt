package com.eygraber.virtue.android

import me.tatarka.inject.annotations.Qualifier

@Qualifier
@Target(
  AnnotationTarget.PROPERTY,
  AnnotationTarget.PROPERTY_GETTER,
  AnnotationTarget.FUNCTION,
  AnnotationTarget.VALUE_PARAMETER,
  AnnotationTarget.TYPE,
)
public annotation class ActivityContext

@Qualifier
@Target(
  AnnotationTarget.PROPERTY,
  AnnotationTarget.PROPERTY_GETTER,
  AnnotationTarget.FUNCTION,
  AnnotationTarget.VALUE_PARAMETER,
  AnnotationTarget.TYPE,
)
public annotation class AppContext
