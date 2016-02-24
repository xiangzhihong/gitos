package net.oschina.gitapp.util;

import android.content.Context;
import android.view.View;

import java.util.LinkedList;

/**
 * 关于本类,详情请见http://kymjs.com/code/2015/11/26/01/
 * 
 * @author kymjs (http://www.kymjs.com/) on 12/28/15.
 */
public class RecyclerList extends LinkedList<View> {

    private Context cxt;
    private int layoutId;

    public RecyclerList(Context cxt, int id) {
        this.cxt = cxt;
        this.layoutId = id;
    }

    public View get() {
        View view;
        if (size() > 0) {
            view = getFirst();
            removeFirst();
        } else {
            view = View.inflate(cxt, layoutId, null);
        }
        return view;
    }
}