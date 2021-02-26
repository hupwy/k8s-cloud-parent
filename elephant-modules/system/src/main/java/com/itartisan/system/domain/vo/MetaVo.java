package com.itartisan.system.domain.vo;

/**
 * 路由显示信息
 */
public class MetaVo {
    /**
     * 设置该路由在侧边栏和面包屑中展示的名字
     */
    private String title;

    /**
     * 设置该路由的图标，对应路径src/assets/icons/svg
     */
    private String icon;

    /**
     * 是否隐藏路由，当设置 true 的时候该路由不会再侧边栏出现
     */
    private boolean hidden;

    public MetaVo() {
    }

    public MetaVo(String title, String icon, boolean hidden) {
        this.title = title;
        this.icon = icon;
        this.hidden = hidden;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
