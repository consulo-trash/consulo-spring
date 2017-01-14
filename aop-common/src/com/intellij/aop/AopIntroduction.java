/*
 * Copyright (c) 2000-2007 JetBrains s.r.o. All Rights Reserved.
 */
package com.intellij.aop;

import com.intellij.aop.psi.AopReferenceHolder;
import com.intellij.jam.model.common.CommonModelElement;
import com.intellij.psi.PsiClass;
import com.intellij.util.xml.GenericValue;
import org.jetbrains.annotations.NotNull;

/**
 * @author peter
 */
public interface AopIntroduction extends CommonModelElement {

  @NotNull
  GenericValue<AopReferenceHolder> getTypesMatching();


  @NotNull
  GenericValue<PsiClass> getImplementInterface();


  @NotNull
  GenericValue<PsiClass> getDefaultImpl();
}
