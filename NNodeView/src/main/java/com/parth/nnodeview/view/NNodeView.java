/*
 * Copyright (C) 2017 PARTH VORA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.parth.nnodeview.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.parth.nnodeview.R;
import com.parth.nnodeview.holder.NNodeHolder;
import com.parth.nnodeview.model.NNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Parth Vora on 2/8/17.
 */
public class NNodeView {
    private static final String NODE_SEPERATOR = ";";

    protected NNode mRootNode;
    private Context mContext;
    private boolean styleForRoot;
    private int containerStyle = 0;
    private Class<? extends NNode.NNodeViewHolder> defaultViewHolderClass = NNodeHolder.class;
    private NNode.NNodeClickListener nodeClickListener;
    private NNode.NNodeLongClickListener nodeLongClickListener;
    private boolean mSelectionModeEnabled;
    private boolean mUseDefaultAnimation = false;
    private boolean useNodeScrollView = false;
    private boolean enableAutoToggle = true;

    public NNodeView(Context context) {
        mContext = context;
    }

    public void setRoot(NNode mRoot) {
        this.mRootNode = mRoot;
    }

    public NNodeView(Context context, NNode root) {
        mRootNode = root;
        mContext = context;
    }

    public void setDefaultAnimation(boolean defaultAnimation) {
        this.mUseDefaultAnimation = defaultAnimation;
    }

    public void setDefaultContainerStyle(int style) {
        setDefaultContainerStyle(style, false);
    }

    public void setDefaultContainerStyle(int style, boolean applyForRoot) {
        containerStyle = style;
        this.styleForRoot = applyForRoot;
    }

    public void setUseNodeScrollView(boolean useNodeScrollView) {
        this.useNodeScrollView = useNodeScrollView;
    }

    public boolean isNodeScrollViewEnabled() {
        return useNodeScrollView;
    }

    public void setUseAutoToggle(boolean enableAutoToggle) {
        this.enableAutoToggle = enableAutoToggle;
    }

    public boolean isAutoToggleEnabled() {
        return enableAutoToggle;
    }

    public void setDefaultViewHolder(Class<? extends NNode.NNodeViewHolder> viewHolder) {
        defaultViewHolderClass = viewHolder;
    }

    public void setDefaultNodeClickListener(NNode.NNodeClickListener listener) {
        nodeClickListener = listener;
    }

    public void setDefaultNodeLongClickListener(NNode.NNodeLongClickListener listener) {
        nodeLongClickListener = listener;
    }

    public void expandAll() {
        expandNodes(mRootNode, true);
    }

    public void collapseAll() {
        for(NNode n : mRootNode.getChildren()) {
            collapseNodes(n, true);
        }
    }

    public View getView(int style) {
        final ViewGroup view;
        if(style > 0) {
            ContextThemeWrapper newContext = new ContextThemeWrapper(mContext, style);
            view = useNodeScrollView ? new NodeScrollView(newContext) : new ScrollView(newContext);
        } else {
            view = useNodeScrollView ? new NodeScrollView(mContext) : new ScrollView(mContext);
        }
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        Context containerContext = mContext;
        if(containerStyle != 0 && styleForRoot) {
            containerContext = new ContextThemeWrapper(mContext, containerStyle);
        }
        final LinearLayout viewNNodeItems = new LinearLayout(containerContext, null, containerStyle);

        viewNNodeItems.setId(R.id.child_item);
        viewNNodeItems.setOrientation(LinearLayout.VERTICAL);
        view.addView(viewNNodeItems);

        mRootNode.setViewHolder(new NNode.NNodeViewHolder(mContext) {
            @Override
            public View createNNodeView(NNode node, Object value) {
                return null;
            }

            @Override
            public ViewGroup getNodeItemsView() {
                return viewNNodeItems;
            }
        });

        expandNodes(mRootNode, false);
        return view;
    }

    public View getView() {
        return getView(-1);
    }


    public void expandLevels(int level) {
        for(NNode n : mRootNode.getChildren()) {
            expandLevels(n, level);
        }
    }

    private void expandLevels(NNode node, int level) {
        if(node.getLevel() <= level) {
            expandNodes(node, false);
        }
        for(NNode n : node.getChildren()) {
            expandLevels(n, level);
        }
    }

    public void expandNodes(NNode node) {
        expandNodes(node, false);
    }

    public void collapseNodes(NNode node) {
        collapseNodes(node, false);
    }

    public void collapseNodesWithSubnodes(NNode node) {
        collapseNodes(node, true);
    }

    public String getSaveState() {
        final StringBuilder builder = new StringBuilder();
        getSaveState(mRootNode, builder);
        if(builder.length() > 0) {
            builder.setLength(builder.length() - 1);
        }
        return builder.toString();
    }

    public void restoreState(String saveState) {
        if(!TextUtils.isEmpty(saveState)) {
            collapseAll();
            final String[] openNodesArray = saveState.split(NODE_SEPERATOR);
            final Set<String> openNodes = new HashSet<>(Arrays.asList(openNodesArray));
            restoreNodeState(mRootNode, openNodes);
        }
    }

    private void restoreNodeState(NNode node, Set<String> openNodes) {
        for(NNode n : node.getChildren()) {
            if(openNodes.contains(n.getPath())) {
                expandNodes(n);
                restoreNodeState(n, openNodes);
            }
        }
    }

    private void getSaveState(NNode root, StringBuilder sBuilder) {
        for(NNode node : root.getChildren()) {
            if(node.isExpanded()) {
                sBuilder.append(node.getPath());
                sBuilder.append(NODE_SEPERATOR);
                getSaveState(node, sBuilder);
            }
        }
    }

    public void toggleNodes(NNode node) {
        if(node.isExpanded()) {
            collapseNodes(node, false);
        } else {
            expandNodes(node, false);
        }
    }

    /**
     * Method Name :- collapseNodes
     * <p>
     * Description :- collapse nnode views to clicked node without expanding its child again on expanding
     *
     * @param node            position of this address to remove from the list
     * @param includeSubnodes include subnodes if need to expand all of them
     * @author Parth
     * @date 2/8/17
     */
    private void collapseNodes(NNode node, final boolean includeSubnodes) {
        node.setExpanded(false);
        NNode.NNodeViewHolder nodeViewHolder = getViewHolderForNNode(node);

        if(mUseDefaultAnimation) {
            collapse(nodeViewHolder.getNodeItemsView());
        } else {
            nodeViewHolder.getNodeItemsView().setVisibility(View.GONE);
        }
        nodeViewHolder.toggle(false);

        for(NNode n : node.getChildren()) {
            collapseNodes(n, includeSubnodes);
        }
    }

    private void expandNodes(final NNode node, boolean includeSubnodes) {
        node.setExpanded(true);
        final NNode.NNodeViewHolder parentViewHolder = getViewHolderForNNode(node);
        parentViewHolder.getNodeItemsView().removeAllViews();

        parentViewHolder.toggle(true);

        for(final NNode n : node.getChildren()) {
            addNode(parentViewHolder.getNodeItemsView(), n);

            if(n.isExpanded() || includeSubnodes) {
                expandNodes(n, includeSubnodes);
            }
        }
        if(mUseDefaultAnimation) {
            expand(parentViewHolder.getNodeItemsView());
        } else {
            parentViewHolder.getNodeItemsView().setVisibility(View.VISIBLE);
        }
    }

    private void addNode(ViewGroup container, final NNode nNode) {
        final NNode.NNodeViewHolder viewHolder = getViewHolderForNNode(nNode);
        final View nodeView = viewHolder.getView();
        container.addView(nodeView);
        if(mSelectionModeEnabled) {
            viewHolder.toggleSelectionMode(true);
        }

        nodeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nNode.getClickListener() != null) {
                    nNode.getClickListener().onClick(nNode, nNode.getValue());
                } else if(nodeClickListener != null) {
                    nodeClickListener.onClick(nNode, nNode.getValue());
                }
                if(enableAutoToggle) {
                    toggleNodes(nNode);
                }
            }
        });

        nodeView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(nNode.getLongClickListener() != null) {
                    return nNode.getLongClickListener().onLongClick(nNode, nNode.getValue());
                } else if(nodeLongClickListener != null) {
                    return nodeLongClickListener.onLongClick(nNode, nNode.getValue());
                }
                if(enableAutoToggle) {
                    toggleNodes(nNode);
                }
                return false;
            }
        });
    }

    //------------------------------------------------------------
    //  Selection methods
    public void setSelectionModeEnabled(boolean selectionModeEnabled) {
        if(!selectionModeEnabled) {
            // TODO fix double iteration over node
            deselectAll();
        }
        mSelectionModeEnabled = selectionModeEnabled;

        for(NNode node : mRootNode.getChildren()) {
            toggleSelectionMode(node, selectionModeEnabled);
        }
    }

    public <E> List<E> getSelectedValues(Class<E> clazz) {
        List<E> result = new ArrayList<>();
        List<NNode> selected = getSelected();
        for(NNode n : selected) {
            Object value = n.getValue();
            if(value != null && value.getClass().equals(clazz)) {
                result.add((E) value);
            }
        }
        return result;
    }

    public boolean isSelectionModeEnabled() {
        return mSelectionModeEnabled;
    }

    private void toggleSelectionMode(NNode parent, boolean mSelectionModeEnabled) {
        toogleSelectionForNode(parent, mSelectionModeEnabled);
        if(parent.isExpanded()) {
            for(NNode node : parent.getChildren()) {
                toggleSelectionMode(node, mSelectionModeEnabled);
            }
        }
    }

    public List<NNode> getSelected() {
        if(mSelectionModeEnabled) {
            return getSelected(mRootNode);
        } else {
            return new ArrayList<>();
        }
    }

    // TODO Do we need to loop through all nodes? Save references or consider collapsed nodes as not selected
    private List<NNode> getSelected(NNode parent) {
        List<NNode> result = new ArrayList<>();
        for(NNode n : parent.getChildren()) {
            if(n.isSelected()) {
                result.add(n);
            }
            result.addAll(getSelected(n));
        }
        return result;
    }

    public void selectAll(boolean skipCollapsed) {
        selectAllNodes(true, skipCollapsed);
    }

    public void deselectAll() {
        selectAllNodes(false, false);
    }

    private void selectAllNodes(boolean selected, boolean skipCollapsed) {
        if(mSelectionModeEnabled) {
            for(NNode node : mRootNode.getChildren()) {
                selectNode(node, selected, skipCollapsed);
            }
        }
    }

    public void selectNode(NNode nNode, boolean selected) {
        if(mSelectionModeEnabled) {
            nNode.setSelected(selected);
            toogleSelectionForNode(nNode, true);
        }
    }

    private void selectNode(NNode parentNode, boolean selected, boolean skipCollapsed) {
        parentNode.setSelected(selected);
        toogleSelectionForNode(parentNode, true);
        boolean continueSelection = !skipCollapsed || parentNode.isExpanded();
        if(continueSelection) {
            for(NNode node : parentNode.getChildren()) {
                selectNode(node, selected, skipCollapsed);
            }
        }
    }

    private void toogleSelectionForNode(NNode nNode, boolean makeSelectable) {
        NNode.NNodeViewHolder holder = getViewHolderForNNode(nNode);
        if(holder.isInitialized()) {
            getViewHolderForNNode(nNode).toggleSelectionMode(makeSelectable);
        }
    }

    private NNode.NNodeViewHolder getViewHolderForNNode(NNode nNode) {
        NNode.NNodeViewHolder viewHolder = nNode.getViewHolder();
        if(viewHolder == null) {
            try {
                final Object object = defaultViewHolderClass.getConstructor(Context.class).newInstance(mContext);
                viewHolder = (NNode.NNodeViewHolder) object;
                nNode.setViewHolder(viewHolder);
            } catch(Exception e) {
                throw new RuntimeException("Could not instantiate class " + defaultViewHolderClass);
            }
        }
        if(viewHolder.getContainerStyle() <= 0) {
            viewHolder.setContainerStyle(containerStyle);
        }
        if(viewHolder.getNNodeView() == null) {
            viewHolder.setNNodeViev(this);
        }
        return viewHolder;
    }

    private static void expand(final View view) {
        view.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = view.getMeasuredHeight();

        view.getLayoutParams().height = 0;
        view.setVisibility(View.VISIBLE);
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                view.getLayoutParams().height = interpolatedTime == 1 ?
                        LinearLayout.LayoutParams.WRAP_CONTENT :
                        (int) (targetHeight * interpolatedTime);
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        animation.setDuration((int) (targetHeight / view.getContext().getResources().getDisplayMetrics().density));
        view.startAnimation(animation);
    }

    private static void collapse(final View view) {
        final int initialHeight = view.getMeasuredHeight();

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1) {
                    view.setVisibility(View.GONE);
                } else {
                    view.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    view.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        animation.setDuration((int) (initialHeight / view.getContext().getResources().getDisplayMetrics().density));
        view.startAnimation(animation);
    }

    //-----------------------------------------------------------------
    //Add / Remove
    public void addNode(NNode parentNode, final NNode childNodeToAdd) {
        parentNode.addChild(childNodeToAdd);
        if(parentNode.isExpanded()) {
            final NNode.NNodeViewHolder parentViewHolder = getViewHolderForNNode(parentNode);
            addNode(parentViewHolder.getNodeItemsView(), childNodeToAdd);
        }
    }

    public void removeNode(NNode nNode) {
        if(nNode.getParent() != null) {
            NNode parent = nNode.getParent();
            int index = parent.deleteChild(nNode);
            if(parent.isExpanded() && index >= 0) {
                final NNode.NNodeViewHolder parentViewHolder = getViewHolderForNNode(parent);
                parentViewHolder.getNodeItemsView().removeViewAt(index);
            }
        }
    }
}
