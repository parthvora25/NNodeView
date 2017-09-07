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

package com.parth.nnodeview.holder;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parth.nnodeview.model.NNode;

/**
 * NNodeHolder class to set the node string in the textview.
 * It can be overridden by creating your own custom NodeHolder
 * class with custom layout to display.
 */
public class NNodeHolder extends NNode.NNodeViewHolder<Object> {

    public NNodeHolder(Context context) {
        super(context);
    }

    @Override
    public View createNNodeView(NNode node, Object value) {
        final TextView tv = new TextView(context);
        tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setPadding(10,10,10,10);
        tv.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        tv.setText(String.valueOf(value));
        return tv;
    }

    @Override
    public void toggle(boolean active) {

    }
}
