/*
 * Copyright (c) 2000-2007 JetBrains s.r.o. All Rights Reserved.
 */

package com.intellij.spring.perspectives.graph.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.graph.builder.GraphBuilder;
import com.intellij.openapi.graph.builder.GraphDataModel;
import com.intellij.openapi.graph.view.Graph2D;
import com.intellij.openapi.project.Project;
import com.intellij.spring.SpringBundle;
import com.intellij.spring.SpringIcons;
import com.intellij.spring.model.xml.beans.SpringBaseBeanPointer;
import com.intellij.spring.perspectives.graph.SpringBeanDependenciesDataModel;
import com.intellij.spring.perspectives.graph.SpringBeanDependencyInfo;
import org.jetbrains.annotations.NotNull;

public class ShowAutowiredDependencies extends AbstractSpringGraphToggleAction {

  public ShowAutowiredDependencies() {
    super(SpringIcons.SPRING_DEPENDENCIES_GRAPH_SHOW_AUTOWIRED);
  }

  public ShowAutowiredDependencies(final GraphBuilder<SpringBaseBeanPointer, SpringBeanDependencyInfo> builder) {
    super(builder, SpringIcons.SPRING_DEPENDENCIES_GRAPH_SHOW_AUTOWIRED);
  }

  public void update(final AnActionEvent e) {
    super.update(e);
    final GraphBuilder builder = getBuilder(e);

    e.getPresentation().setVisible(builder != null && builder.getGraphDataModel() instanceof SpringBeanDependenciesDataModel);
  }

  protected boolean isSelected(Graph2D graph, final Project project, final AnActionEvent event) {
    final GraphBuilder builder = getBuilder(event);

    if (builder != null) {
      final GraphDataModel dataModel = builder.getGraphDataModel();
      if (dataModel instanceof SpringBeanDependenciesDataModel) {
        return ((SpringBeanDependenciesDataModel)dataModel).getDiagramContext().isShowAutowiredDependencies();
      }
    }
    return false;
  }

  protected void setSelected(Graph2D graph, boolean state, final Project project, final AnActionEvent e) {
    final GraphBuilder builder = getBuilder(e);

    if (builder != null) {
      final GraphDataModel dataModel = builder.getGraphDataModel();
      if (dataModel instanceof SpringBeanDependenciesDataModel) {
        ((SpringBeanDependenciesDataModel)dataModel).getDiagramContext().setShowAutowiredDependencies(state);
        builder.queueUpdate();
      }
    }
  }

  protected String getText(final @NotNull Graph2D graph) {
    return SpringBundle.message("spring.bean.dependency.graph.action.show.autowired");
  }

}