package com.parth.nlevelnodeview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.parth.nnodeview.model.NNode;

public class NodeHolderAdapter extends NNode.NNodeViewHolder<NodeHolderAdapter.NodeHolder> {
    private TextView childName;
    private Context context;

    public NodeHolderAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public View createNNodeView(final NNode node, NodeHolder value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.child_node_item_layout, null, false);
        childName = (TextView) view.findViewById(R.id.title);
        childName.setText(value.text);

        return view;
    }

    @Override
    public void toggle(boolean active) {
        // switch between icons or other logic for example
        // view.setImage(context.getResources().getString(active ? R.string.icon_selected : R.string.icon_unselected));
    }

    public static class NodeHolder {
        public String text;

        public NodeHolder(String text) {
            this.text = text;
        }
    }
}