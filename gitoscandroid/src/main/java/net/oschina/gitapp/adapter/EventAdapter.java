package net.oschina.gitapp.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Commit;
import net.oschina.gitapp.bean.Event;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.common.HtmlRegexpUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.util.RecyclerList;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * 个人动态列表适配器
 *
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-05-20 下午15：28
 */
public class EventAdapter extends CommonAdapter<Event> {

    //有关本类请查看 http://kymjs.com/code/2015/11/26/01/
    private RecyclerList recyclerList;

    private static final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
            Locale.US);

    public EventAdapter(Context context, int resource) {
        super(context, resource);
        recyclerList = new RecyclerList(context, R.layout.list_item_event_commits);
    }

    @Override
    public void convert(ViewHolder vh, final Event event) {
        displayContent(vh, event);
    }

    private void displayContent(ViewHolder vh, final Event event) {
        // 1.加载头像
        vh.setImageForUrl(R.id.iv_portrait, event.getAuthor().getNew_portrait());
        vh.getView(R.id.iv_portrait).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = event.getAuthor();
                if (user != null) {
                    UIHelper.showUserInfoDetail(mContext, user, null);
                }
            }
        });

        // 2.显示相关信息
        SpannableString title = UIHelper.parseEventTitle(event
                .getAuthor().getName(), event.getProject().getOwner().getName()
                + " / " + event.getProject().getName(), event);
        vh.setText(R.id.tv_name, title);

        // commits信息的显示
        LinearLayout commitLists = vh.getView(R.id.ll_commits_list);
        commitLists.setVisibility(View.GONE);
        int count = commitLists.getChildCount();
        while (count-- > 0) {
            recyclerList.add(commitLists.getChildAt(0));
            commitLists.removeViewAt(0);
        }
        if (event.getData() != null) {
            List<Commit> commits = event.getData().getCommits();
            if (commits != null && commits.size() > 0) {
                showCommitInfo(commitLists, commits);
                commitLists.setVisibility(View.VISIBLE);
            }
        }

        TextView content = vh.getView(R.id.tv_content);
        content.setVisibility(View.GONE);
        // 评论的内容
        if (event.getEvents().getNote() != null
                && event.getEvents().getNote().getNote() != null) {
            content.setText(HtmlRegexpUtils.filterHtml(event
                    .getEvents().getNote().getNote()));
            content.setVisibility(View.VISIBLE);
        }

        // issue的title
        if (event.getEvents().getIssue() != null
                && event.getEvents().getNote() == null) {
            content.setText(event.getEvents().getIssue()
                    .getTitle());
            content.setVisibility(View.VISIBLE);
        }

        // pr的title
        if (event.getEvents().getPull_request() != null
                && event.getEvents().getNote() == null) {
            content.setText(event.getEvents().getPull_request()
                    .getTitle());
            content.setVisibility(View.VISIBLE);
        }
        try {
            vh.setText(R.id.tv_date, f.format(event.getUpdated_at()).substring(5, 10));
        } catch (Exception e) {
        }
    }

    private void showCommitInfo(LinearLayout layout, List<Commit> commits) {
        for (int i = 0; i < commits.size() && i < 2; i++) {
            addCommitItem(layout, commits.get(i));
        }
    }

    /**
     * 添加commit项
     */
    private void addCommitItem(LinearLayout layout, Commit commit) {
        LinearLayout v = (LinearLayout) recyclerList.get();
        if (commit != null) {
            ((TextView) v.getChildAt(0)).setText(commit.getId());
            if (commit.getAuthor() != null) {
                ((TextView) v.getChildAt(1)).setText(commit.getAuthor().getName());
            }
            ((TextView) v.getChildAt(3)).setText(commit.getMessage());
        }
        layout.addView(v);
    }
}
