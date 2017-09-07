package com.parth.nlevelnodeview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import com.google.gson.reflect.TypeToken;
import com.parth.nnodeview.holder.NNodeHolder;
import com.parth.nnodeview.model.NNode;
import com.parth.nnodeview.view.NNodeView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);

            RelativeLayout containerView = (RelativeLayout) findViewById(R.id.containView);

            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray jsonArray = obj.getJSONArray("array");

            Type type = new TypeToken<List<Category>>() {
            }.getType();

            List<Category> categoryList = App.getGson().fromJson(jsonArray.toString(), type);

            NNode rootNode = NNode.root();

            // todo add recusrsive method inside this callback to extend levels of the children node
            // level 0
            getAllChildNodes(rootNode, categoryList, new nChildCallback() {
                @Override
                public void setListNode(List<Category> list, NNode levels) {
                    if(list != null && list.size() > 0) {
                        // level 1
                        getAllChildNodes(levels, list, new nChildCallback() {
                            @Override
                            public void setListNode(List<Category> list, NNode levels) {
                                if(list != null && list.size() > 0) {
                                    // level 2
                                    getAllChildNodes(levels, list, new nChildCallback() {
                                        @Override
                                        public void setListNode(List<Category> list, NNode levels) {
                                            if(list != null && list.size() > 0) {
                                                // level 3
                                                getAllChildNodes(levels, list, new nChildCallback() {
                                                    @Override
                                                    public void setListNode(List<Category> list, NNode levels) {
                                                        if(list != null && list.size() > 0) {
                                                            // level 4
                                                            getAllChildNodes(levels, list, new nChildCallback() {
                                                                @Override
                                                                public void setListNode(List<Category> list, NNode levels) {

                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });

            NNodeView tViews = new NNodeView(this, rootNode);
            tViews.setDefaultAnimation(true);
            tViews.setDefaultViewHolder(NNodeHolder.class);
            containerView.addView(tViews.getView());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method Name :- getAllChildNodes
     * <p>
     * Description :- get all parent child nodes from category list and set its levels in root using
     * child callback interface
     *
     * @param root          List node for creating a leveled parent or child
     * @param categoryList  categorylist from server
     * @param childCallback interface callback for passing this parents child list and its parent node
     * @author Parth
     * @date 2/8/17
     */
    private void getAllChildNodes(NNode root, List<Category> categoryList, nChildCallback childCallback) {
        for(Category comment : categoryList) {
            NNode bodes = new NNode(comment.getString());
            childCallback.setListNode(comment.getArray(), bodes);
            root.addChild(bodes);

            // todo remove this custom adapter code from library at publishing it
//            NNode levels = new NNode(new NodeHolderAdapter.NodeHolder(comment.getString())).setViewHolder(new NodeHolderAdapter(this));
//            childCallback.setListNode(comment.getArray(), levels);
//            root.addChild(levels);
        }
    }

    private interface nChildCallback {
        void setListNode(List<Category> list, NNode levels);
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("category.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}