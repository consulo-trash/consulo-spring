// Generated on Thu Nov 09 17:15:14 MSK 2006
// DTD/Schema  :    http://www.springframework.org/schema/util

package com.intellij.spring.model.xml.util;

import com.intellij.psi.PsiClass;
import com.intellij.spring.model.xml.DomSpringBean;
import com.intellij.spring.model.xml.beans.ListOrSet;
import com.intellij.spring.model.xml.beans.TypeHolder;
import com.intellij.spring.model.xml.beans.ScopedElement;
import com.intellij.util.xml.GenericAttributeValue;
import javax.annotation.Nonnull;

/**
 * http://www.springframework.org/schema/util:listElemType interface.
 */
public interface UtilList extends DomSpringBean, SpringUtilElement, ListOrSet, ScopedElement, TypeHolder {
    /**
     * Returns the value of the list-class child.
     * @return the value of the list-class child.
     */
    @Nonnull
    GenericAttributeValue<PsiClass> getListClass();
}
