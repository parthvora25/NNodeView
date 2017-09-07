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
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.parth.nnodeview.R;

/**
 * Created by Parth Vora on 2/8/17.
 */
public class NNodeContainerView extends LinearLayout {
    private LinearLayout nNodesContainer;
    private ViewGroup nodeContainer;
    private final int containerStyle;

    public NNodeContainerView(Context context, int containerStyle) {
        super(context);
        this.containerStyle = containerStyle;
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);

        nodeContainer = new RelativeLayout(getContext());
        nodeContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        nodeContainer.setId(R.id.node_head);

        ContextThemeWrapper newContext = new ContextThemeWrapper(getContext(), containerStyle);
        nNodesContainer = new LinearLayout(newContext, null, containerStyle);
        nNodesContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        nNodesContainer.setId(R.id.node_views);
        nNodesContainer.setOrientation(LinearLayout.VERTICAL);
        nNodesContainer.setVisibility(View.GONE);

        addView(nodeContainer);
        addView(nNodesContainer);
    }

    public void insertNodeView(View nodeView) {
        nodeContainer.addView(nodeView);
    }

    public ViewGroup getNodeContainer() {
        return nodeContainer;
    }
}
