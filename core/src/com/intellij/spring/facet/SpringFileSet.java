package com.intellij.spring.facet;

import com.intellij.ide.util.ElementsChooser;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerManager;
import com.intellij.spring.SpringIcons;
import consulo.spring.module.extension.SpringModuleExtension;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Dmitry Avdeev
 */
public class SpringFileSet implements ElementsChooser.ElementProperties, Disposable {

  @NonNls private static final String ID_PREFIX = "fileset";
  private boolean myAutodetected;
  private Icon myIcon = SpringIcons.FILESET;

  public void setIcon(final Icon icon) {
    myIcon = icon;
  }

  public static String getUniqueId(final Set<SpringFileSet> list) {
    int index = 0;
    for (SpringFileSet fileSet : list) {
      if (fileSet.getId().startsWith(ID_PREFIX)) {
        final String s = fileSet.getId().substring(ID_PREFIX.length());
        try {
          final int i = Integer.parseInt(s);
          index = Math.max(i, index);
        }
        catch(NumberFormatException ignored) {

        }
      }
    }
    return ID_PREFIX + (index + 1);
  }

  public static String getUniqueName(String prefix, final Set<SpringFileSet> list) {
    int index = 0;
    for (SpringFileSet fileSet : list) {
      if (fileSet.getName().startsWith(prefix)) {
        final String s = fileSet.getName().substring(prefix.length());
        int i;
        try {
          i = Integer.parseInt(s);
        } catch(NumberFormatException e) {
          i = 0;
        }
        index = Math.max(i + 1, index);
      }
    }
    return index == 0 ? prefix : prefix + index;
  }

  @NotNull private final String myId;
  private String myName;
  private final List<VirtualFilePointer> myFiles = new ArrayList<>();
  private final List<String> myDependencies = new ArrayList<>();
  private boolean myRemoved;

  public SpringFileSet(@NonNls @NotNull String id, @NotNull String name, @NotNull final Disposable parent) {
    myId = id;
    myName = name;
    Disposer.register(parent, this);
  }

  public SpringFileSet(SpringFileSet original) {
    myId = original.myId;
    myName = original.myName;
    myFiles.addAll(original.myFiles);
    myDependencies.addAll(original.myDependencies);
    myAutodetected = original.isAutodetected();
    myIcon = original.getIcon();
    myRemoved = original.isRemoved();
  }

  public boolean isNew() {
    return false;
  }
  
  public boolean isAutodetected() {
    return myAutodetected;
  }

  public void setAutodetected(final boolean autodetected) {
    myAutodetected = autodetected;
  }

  public boolean isRemoved() {
    return myRemoved;
  }

  @NotNull
  public String getId() {
    return myId;
  }

  public String getName() {
    return myName;
  }

  public void setName(@NotNull final String name) {
    myName = name;
  }

  @NotNull
  public List<VirtualFilePointer> getFiles() {
    return myFiles;
  }

  public List<String> getDependencies() {
    return myDependencies;
  }

  public void removeDependency(String dependency) {
    myDependencies.remove(dependency);
  }

  public void setDependencies(List<String> dependencies) {
    myDependencies.clear();
    myDependencies.addAll(dependencies);
  }

  public void addDependency(final String dep) {
    myDependencies.add(dep);
  }

  public void addFile(@NonNls String url) {
    if (!StringUtil.isEmptyOrSpaces(url)) {
      final VirtualFilePointer filePointer = VirtualFilePointerManager.getInstance().create(url, this, null);
      myFiles.add(filePointer);
    }
  }

  public void addFile(@NotNull final VirtualFile file) {
    addFile(file.getUrl());
  }

  public void removeFile(VirtualFilePointer file) {
    myFiles.remove(file);
  }

  public boolean hasFile(@Nullable VirtualFile file) {
    if (file == null) {
      return false;
    }
    for (VirtualFilePointer pointer: myFiles) {
      final VirtualFile virtualFile = pointer.getFile();
      if (virtualFile != null && file.equals(virtualFile)) {
        return true;
      }
    }
    return false;
  }

  @NotNull
  public SpringFileSet cloneTo(SpringModuleExtension extension) {
    SpringFileSet fileSet = new SpringFileSet(myId, myName, extension);
    fileSet.myFiles.addAll(myFiles);
    fileSet.myDependencies.addAll(myDependencies);
    fileSet.myAutodetected = isAutodetected();
    //fileSet.myIcon = getIcon();
    fileSet.myRemoved = isRemoved();
    for (VirtualFilePointer pointer : myFiles) {
      fileSet.addFile(pointer.getUrl());
    }
    return fileSet;
  }

  public Icon getIcon() {
    return myIcon;
  }

  public Color getColor() {
    return null;
  }

  public String toString() {
    return myName;
  }

  public void setRemoved(final boolean removed) {
    myRemoved = removed;
  }

  public void dispose() {

  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SpringFileSet)) return false;
    SpringFileSet that = (SpringFileSet) o;
    return myAutodetected == that.myAutodetected &&
        myRemoved == that.myRemoved &&
        Objects.equals(myId, that.myId) &&
        Objects.equals(myName, that.myName) &&
        Objects.equals(myFiles, that.myFiles) &&
        Objects.equals(myDependencies, that.myDependencies);
  }

  @Override
  public int hashCode() {
    return Objects.hash(myAutodetected, myId, myName, myFiles, myDependencies, myRemoved);
  }
}
