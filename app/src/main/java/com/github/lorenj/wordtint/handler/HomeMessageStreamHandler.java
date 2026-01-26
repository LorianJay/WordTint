package com.github.lorenj.wordtint.handler;

import com.github.lorenj.wordtint.entity.PostCover;

import java.util.Collection;

/**
 * 处理主页文章信息流的Handler接口
 *
 * @author cnsukidayo
 * @date 2023/2/2 10:45
 */
public interface HomeMessageStreamHandler {
    /**
     * 刷新主页信息流
     */
    void refresh();

    /**
     * 从主页列表中,得到当前主页显示的帖子数量
     *
     * @return 帖子的数量, int类型
     */
    int getPostSize();

    /**
     * 在主页添加一个文章封面进入列表
     *
     * @param itemPostCover 待添加的文章
     */
    void addPostCover(PostCover itemPostCover);


    /**
     * 在主页添加多个文章封面进入列表
     *
     * @param itemPostCovers 待添加的文章
     */
    void addPostCover(PostCover... itemPostCovers);

    /**
     * 在主页添加多个文章封面进入列表
     *
     * @param itemPostCovers 待添加的文章
     */
    void addPostCover(Collection<PostCover> itemPostCovers);


    /**
     * 根据文章的位置获取具体的某个文章
     *
     * @param position 文章在列表中的位置
     * @return 返回代表文章信息的封面实体
     */
    PostCover getPostCoverByPosition(int position);


}
