/*
 * Copyright (c) 2000-2007 JetBrains s.r.o. All Rights Reserved.
 */
package com.intellij.spring.impl.model;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.spring.model.converters.SpringBeanFactoryMethodConverter;
import com.intellij.spring.model.xml.CommonSpringBean;
import com.intellij.spring.model.xml.SpringQualifier;
import com.intellij.spring.model.xml.beans.SpringBean;
import com.intellij.spring.model.xml.beans.SpringBeanPointer;
import com.intellij.util.xml.GenericValue;
import com.intellij.util.xml.converters.values.ClassValueConverter;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * @author peter
 */
public abstract class AbstractDomSpringBean implements CommonSpringBean {

  @Nullable
  public GenericValue<PsiMethod> getFactoryMethod() {
    return null;
  }

  @Nullable
  public GenericValue<SpringBeanPointer> getFactoryBean() {
    return null;
  }

  @Nullable public abstract String getClassName();

  @Nullable
  public PsiClass getBeanClass() {
    return getBeanClass(null, true);
  }

  public abstract PsiManager getPsiManager();

  @Nullable
  public PsiClass getBeanClass(boolean considerFactories) {
    return getBeanClass(null, considerFactories);
  }

  @Nullable
  public abstract Module getModule();

  @Nullable
  public abstract PsiFile getContainingFile();

  @Nullable
  public PsiClass getBeanClass(@Nullable Set<AbstractDomSpringBean> visited, boolean considerFactories) {
    if (visited != null && visited.contains(this)) return null;

    if (considerFactories) {
      final GenericValue<PsiMethod> value = getFactoryMethod();
      final PsiMethod factoryMethod = value == null ? null : value.getValue();
      final GenericValue<SpringBeanPointer> factoryBean = getFactoryBean();
      if (factoryMethod != null && SpringBeanFactoryMethodConverter.isValidFactoryMethod(factoryMethod, factoryBean != null && factoryBean.getValue() != null)) {
        final PsiType returnType = factoryMethod.getReturnType();
        if (returnType instanceof PsiClassType) {
          if (!factoryMethod.hasTypeParameters()) {
            return ((PsiClassType)returnType).resolve();
          } else {
            return this instanceof SpringBean ? SpringBeanFactoryMethodConverter.getFactoryClass((SpringBean)this) : null;
          }
        }
      }
    }

    final String className = getClassName();
    if (className != null) {
      final String convertedName = StringUtil.replace(className, "$", ".");
      final PsiClass psiClass = JavaPsiFacade.getInstance(getPsiManager().getProject()).findClass(convertedName, getSearchScope());
      if (psiClass != null) return psiClass;
    }

    return null;
  }

  protected GlobalSearchScope getSearchScope() {
    return ClassValueConverter.getScope(getPsiManager().getProject(), getModule(), getContainingFile());
  }

  public SpringQualifier getSpringQualifier() {
    return null;
  }

  
}
