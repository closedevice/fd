package sbbic.com.sharepaneldemo;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by God on 2016/5/20.
 */
public class ShareModeList {

    private final static List<ShareMode> shareList = new ArrayList<ShareMode>() {
        {
            add(new ShareMode("1", "微信好友", R.drawable.icon_share_weixin, R.drawable.icon_default_share_weixin));
            add(new ShareMode("2", "QQ好友", R.drawable.icon_share_qq, R.drawable.icon_default_share_qq));
            add(new ShareMode("3", "微信朋友圈", R.drawable.icon_share_weixin_friend, R.drawable.icon_default_share_weixin_friends));
            add(new ShareMode("4", "QQ空间", R.drawable.icon_share_qq_space, R.drawable.icon_default_share_qq_space));
            add(new ShareMode("5", "新浪微博", R.drawable.icon_share_weibo, R.drawable.icon_default_share_weibo));
            add(new ShareMode("6", "腾讯微博", R.drawable.icon_share_qq_weibo, R.drawable.icon_default_share_qq_weibo));
            add(new ShareMode("7", "复制链接", R.drawable.icon_share_copy, R.drawable.icon_default_share_copy));
            add(new ShareMode("8", "分享", R.drawable.icon_share, R.drawable.icon_share));
        }
    };

    private List<ShareMode> using = new ArrayList<>();

    public List<ShareMode> need(String... names) {
        for (String name : names) {
            ShareMode shareMode = getSpecial(name);
            if (shareMode != null) {
                using.add(shareMode);
            } else {
                Log.d("ShareModeList", "not exist");
            }
        }
        return using;
    }

    private ShareMode getSpecial(String name) {
        for (ShareMode shareMode : shareList) {
            if (shareMode.packageName.equals(name)) {
                return shareMode;
            }
        }
        return null;
    }

    public static class ShareMode {
        String packageName;
        String tabName;
        int tabIcon;
        int defaultIcon;

        public ShareMode(String packageName, String tabName, int tabIcon, int defaultIcon) {
            this.packageName = packageName;
            this.tabName = tabName;
            this.tabIcon = tabIcon;
            this.defaultIcon = defaultIcon;
        }
    }


}
