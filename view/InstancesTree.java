package org.jetbrains.debugger.memory.view;

import com.intellij.openapi.project.Project;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import com.intellij.xdebugger.frame.*;
import com.intellij.xdebugger.impl.actions.XDebuggerActions;
import com.intellij.xdebugger.impl.frame.XValueMarkers;
import com.intellij.xdebugger.impl.ui.tree.XDebuggerTree;
import com.intellij.xdebugger.impl.ui.tree.XDebuggerTreeState;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

class InstancesTree extends XDebuggerTree {
  private final XValueNodeImpl myRoot;
  private final Runnable myOnRootExpandAction;
  private List<XValueChildrenList> myChildren;

  InstancesTree(@NotNull Project project,
                @NotNull XDebuggerEditorsProvider editorsProvider,
                @Nullable XValueMarkers<?, ?> valueMarkers,
                @NotNull Runnable onRootExpand) {
    super(project, editorsProvider, null, XDebuggerActions.INSPECT_TREE_POPUP_GROUP, valueMarkers);
    myOnRootExpandAction = onRootExpand;
    myRoot = new XValueNodeImpl(this, null, "root", new MyRootValue());

    myRoot.children();
    setRoot(myRoot, false);
    myRoot.setLeaf(false);
    setSelectionRow(0);
    expandNodesOnLoad(node -> node == myRoot);
  }

  void addChildren(@NotNull XValueChildrenList children, boolean last) {
    if (myChildren == null) {
      myChildren = new ArrayList<>();
    }

    myChildren.add(children);
    myRoot.addChildren(children, last);
  }

  void rebuildTree(@NotNull RebuildPolicy policy, @NotNull XDebuggerTreeState state) {
    if (policy == RebuildPolicy.RELOAD_INSTANCES) {
      myChildren = null;
    }

    rebuildAndRestore(state);
  }

  void rebuildTree(@NotNull RebuildPolicy policy) {
    rebuildTree(policy, XDebuggerTreeState.saveState(this));
  }

  void setMessage(@Nullable Icon icon, @NotNull String text, @NotNull SimpleTextAttributes textAttributes) {
    myRoot.clearChildren();
    myRoot.setMessage(text, icon, textAttributes, null);
  }

  enum RebuildPolicy {
    RELOAD_INSTANCES, ONLY_UPDATE_LABELS
  }

  private class MyRootValue extends XValue {
    @Override
    public void computeChildren(@NotNull XCompositeNode node) {
      if (myChildren == null) {
        myOnRootExpandAction.run();
      } else {
        for (XValueChildrenList children : myChildren) {
          myRoot.addChildren(children, false);
        }

        myRoot.addChildren(XValueChildrenList.EMPTY, true);
      }
    }

    @Override
    public void computePresentation(@NotNull XValueNode node, @NotNull XValuePlace place) {
      node.setPresentation(null, "", "", true);
    }
  }
}
