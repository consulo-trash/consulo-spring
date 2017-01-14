/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.jam.reflect;

import com.intellij.jam.JamService;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PsiAnnotationPattern;
import com.intellij.patterns.PsiJavaPatterns;
import com.intellij.psi.*;
import com.intellij.psi.ref.AnnotationChildLink;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.semantic.SemElement;
import com.intellij.semantic.SemKey;
import com.intellij.semantic.SemRegistrar;
import com.intellij.semantic.SemService;
import com.intellij.util.NullableFunction;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.patterns.PsiJavaPatterns.psiAnnotation;

/**
 * @author peter
 */
public class JamAnnotationMeta extends JamAnnotationArchetype implements SemElement{
  private final AnnotationChildLink myAnnotationChildLink;
  private final SemKey<JamAnnotationMeta> myMetaKey;

  public JamAnnotationMeta(@NotNull @NonNls String annoName) {
    this(annoName, null);
  }

  public JamAnnotationMeta(@NotNull @NonNls String annoName, @Nullable JamAnnotationArchetype archetype) {
    this(annoName, archetype, JamService.ANNO_META_KEY.subKey("AnnoMeta:" + annoName));
  }

  public JamAnnotationMeta(@NotNull @NonNls String annoName, @Nullable JamAnnotationArchetype archetype,
                           final SemKey<JamAnnotationMeta> metaKey) {
    super(archetype);
    myAnnotationChildLink = new AnnotationChildLink(annoName);
    myMetaKey = metaKey;
  }

  public SemKey<JamAnnotationMeta> getMetaKey() {
    return myMetaKey;
  }

  @Override
  public JamAnnotationMeta addAttribute(JamAttributeMeta<?> attributeMeta) {
    super.addAttribute(attributeMeta);
    return this;
  }

  public String getAnnoName() {
    return myAnnotationChildLink.getAnnotationQualifiedName();
  }

  @NotNull
  public <T> T getAttribute(PsiModifierListOwner member, JamAttributeMeta<T> meta) {
    return getAttribute(PsiElementRef.real(member), meta);
  }
  
  @NotNull
  public <T> T getAttribute(PsiElementRef<? extends PsiModifierListOwner> member, JamAttributeMeta<T> meta) {
    return meta.getJam(getAnnotationRef(member));
  }

  @Nullable
  public PsiAnnotation getAnnotation(PsiModifierListOwner owner) {
    return myAnnotationChildLink.findLinkedChild(owner);
  }

  @NotNull
  public PsiElementRef<PsiAnnotation> getAnnotationRef(PsiModifierListOwner owner) {
    return myAnnotationChildLink.createChildRef(owner);
  }

  @NotNull
  public PsiElementRef<PsiAnnotation> getAnnotationRef(PsiElementRef<? extends PsiModifierListOwner> member) {
    return myAnnotationChildLink.createChildRef(member);
  }

  @Override
  public String toString() {
    return "JamAnnotationMeta:" + myAnnotationChildLink.getAnnotationQualifiedName();
  }

  public <T extends PsiModifierListOwner> void registerTopLevelSem(SemRegistrar registrar, final ElementPattern<? extends T> parentPattern, final JamMemberMeta<T,?> parentMeta) {
    final boolean isPackage = parentMeta instanceof JamPackageMeta;
    final PsiAnnotationPattern annoPattern =
      psiAnnotation().qName(myAnnotationChildLink.getAnnotationQualifiedName()).withSuperParent(
        2, isPackage ? PsiJavaPatterns.psiJavaElement(PsiPackageStatement.class).with(new PatternCondition<PsiPackageStatement>("package") {
        @Override
        public boolean accepts(@NotNull PsiPackageStatement psiPackageStatement, ProcessingContext context) {
          return parentPattern.accepts(psiPackageStatement.getPackageReference().resolve(), context);
        }
      }) : parentPattern);
    registrar.registerSemElementProvider(myMetaKey, annoPattern, new NullableFunction<PsiAnnotation, JamAnnotationMeta>() {
      public JamAnnotationMeta fun(PsiAnnotation annotation) {
        final PsiElement parent = annotation.getParent().getParent();
        final T element = (isPackage && parent instanceof PsiPackageStatement? (T)((PsiPackageStatement)parent).getPackageReference().resolve() : (T)parent);
        if (SemService.getSemService(element.getProject()).getSemElement(parentMeta.getMetaKey(), element) == parentMeta) {
          return JamAnnotationMeta.this;
        }
        return null;
      }
    });

    registerChildren(registrar, annoPattern);
  }

  public void registerNestedSem(SemRegistrar registrar, ElementPattern<PsiAnnotation> annoPattern, final JamAnnotationMeta parentMeta) {
    registrar.registerSemElementProvider(myMetaKey, annoPattern, new NullableFunction<PsiAnnotation, JamAnnotationMeta>() {
      public JamAnnotationMeta fun(PsiAnnotation annotation) {
        final PsiAnnotation parentAnno = PsiTreeUtil.getParentOfType(annotation, PsiAnnotation.class, true);
        assert parentAnno != null;
        if (SemService.getSemService(annotation.getProject()).getSemElement(parentMeta.getMetaKey(), parentAnno) == parentMeta) {
          return JamAnnotationMeta.this;
        }
        return null;
      }
    });

    registerChildren(registrar, annoPattern);
  }

  private void registerChildren(SemRegistrar registrar, ElementPattern<PsiAnnotation> annoPattern) {
    for (JamAnnotationArchetype cur = this; cur != null; cur = cur.getArchetype()) {
      for (JamAttributeMeta<?> attributeMeta : cur.getAttributes()) {
        if (attributeMeta instanceof JamAnnotationAttributeMeta<?, ?>) {
          ((JamAnnotationAttributeMeta<?, ?>)attributeMeta).registerSem(registrar, annoPattern, this);
        }
      }
    }
  }


}
