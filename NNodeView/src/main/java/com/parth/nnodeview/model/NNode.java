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

package com.parth.nnodeview.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.parth.nnodeview.R;
import com.parth.nnodeview.view.NNodeContainerView;
import com.parth.nnodeview.view.NNodeView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Parth Vora on 2/8/17.
 */
public class NNode {
    public static final String NODE_SEPERATOR = ":";

    private int mId;
    private int mLastId;
    private NNode mParent;
    private boolean mSelected;
    private boolean mSelectable = true;
    private final List<NNode> children;
    private NNodeViewHolder mViewHolder;
    private NNodeClickListener mClickListener;
    private NNodeLongClickListener mLongClickListener;
    private Object mValue;
    private boolean mExpanded;

    public static NNode root() {
        NNode root = new NNode(null);
        root.setSelectable(false);
        return root;
    }

    private int generateId() {
        return ++mLastId;
    }

    public NNode(Object value) {
        children = new ArrayList<>();
        mValue = value;
    }

    public NNode addChild(NNode childNode) {
        childNode.mParent = this;
        childNode.mId = generateId();
        children.add(childNode);
        return this;
    }

    public NNode addChildren(NNode... nodes) {
        for (NNode n : nodes) {
            addChild(n);
        }
        return this;
    }

    public NNode addChildren(Collection<NNode> nodes) {
        for (NNode n : nodes) {
            addChild(n);
        }
        return this;
    }

    public int deleteChild(NNode child) {
        for (int i = 0; i < children.size(); i++) {
            if (child.mId == children.get(i).mId) {
                children.remove(i);
                return i;
            }
        }
        return -1;
    }

    public List<NNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public int size() {
        return children.size();
    }

    public NNode getParent() {
        return mParent;
    }

    public int getId() {
        return mId;
    }

    public boolean isLeaf() {
        return size() == 0;
    }

    public Object getValue() {
        return mValue;
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    public NNode setExpanded(boolean expanded) {
        mExpanded = expanded;
        return this;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }

    public boolean isSelected() {
        return mSelectable && mSelected;
    }

    public void setSelectable(boolean selectable) {
        mSelectable = selectable;
    }

    public boolean isSelectable() {
        return mSelectable;
    }

    public String getPath() {
        final StringBuilder path = new StringBuilder();
        NNode node = this;
        while (node.mParent != null) {
            path.append(node.getId());
            node = node.mParent;
            if (node.mParent != null) {
                path.append(NODE_SEPERATOR);
            }
        }
        return path.toString();
    }


    public int getLevel() {
        int level = 0;
        NNode root = this;
        while (root.mParent != null) {
            root = root.mParent;
            level++;
        }
        return level;
    }

    public boolean isLastChild() {
        if (!isRoot()) {
            int parentSize = mParent.children.size();
            if (parentSize > 0) {
                return mParent.children.get(parentSize - 1).mId == mId;
            }
        }
        return false;
    }

    public NNode setViewHolder(NNodeViewHolder viewHolder) {
        mViewHolder = viewHolder;
        if (viewHolder != null) {
            viewHolder.mNode = this;
        }
        return this;
    }

    public NNode setClickListener(NNodeClickListener listener) {
        mClickListener = listener;
        return this;
    }

    public NNodeClickListener getClickListener() {
        return this.mClickListener;
    }

    public NNode setLongClickListener(NNodeLongClickListener listener) {
        mLongClickListener = listener;
        return this;
    }

    public NNodeLongClickListener getLongClickListener() {
        return mLongClickListener;
    }

    public NNodeViewHolder getViewHolder() {
        return mViewHolder;
    }

    public boolean isFirstChild() {
        return !isRoot() && mParent.children.get(0).mId == mId;
    }

    public boolean isRoot() {
        return mParent == null;
    }

    public NNode getRoot() {
        NNode root = this;
        while (root.mParent != null) {
            root = root.mParent;
        }
        return root;
    }

    public interface NNodeClickListener {
        void onClick(NNode node, Object value);
    }

    public interface NNodeLongClickListener {
        boolean onLongClick(NNode node, Object value);
    }

    public static abstract class NNodeViewHolder<E> {
        protected NNodeView nNodeView;
        protected NNode mNode;
        private View mView;
        protected int containerStyle;
        protected Context context;

        public NNodeViewHolder(Context context) {
            this.context = context;
        }

        public View getView() {
            if (mView != null) {
                return mView;
            }
            final View nodeView = getNodeView();
            final NNodeContainerView nodeContainerView = new NNodeContainerView(nodeView.getContext(), getContainerStyle());
            nodeContainerView.insertNodeView(nodeView);
            mView = nodeContainerView;

            return mView;
        }

        public void setNNodeViev(NNodeView nodeViev) {
            this.nNodeView = nodeViev;
        }

        public NNodeView getNNodeView() {
            return nNodeView;
        }

        public void setContainerStyle(int style) {
            containerStyle = style;
        }

        public View getNodeView() {
            return createNNodeView(mNode, (E) mNode.getValue());
        }

        public ViewGroup getNodeItemsView() {
            return (ViewGroup) getView().findViewById(R.id.node_views);
        }

        public boolean isInitialized() {
            return mView != null;
        }

        public int getContainerStyle() {
            return containerStyle;
        }

        public abstract View createNNodeView(NNode node, E value);

        public void toggle(boolean active) {
            // empty
        }

        public void toggleSelectionMode(boolean editModeEnabled) {
            // empty
        }
    }

}
