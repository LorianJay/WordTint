package com.github.lorenj.wordtint.handler.impl;

import com.github.lorenj.wordtint.entity.PostCover;
import com.github.lorenj.wordtint.handler.HomeMessageStreamHandler;
import com.github.lorenj.wordtint.test.BeanTest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author cnsukidayo
 * @date 2023/2/2 10:46
 */
public class HomeMessageStreamHandlerImpl implements HomeMessageStreamHandler {

    private final int INITIALIZATION_POST_COUNT = 15;

    private final List<PostCover> allPostCovers = new ArrayList<>(INITIALIZATION_POST_COUNT);

    @Override
    public void refresh() {
        allPostCovers.clear();
        // todo 这里应该去请求后端数据,这里暂且只是简单模拟数据
        for (int i = 0; i < INITIALIZATION_POST_COUNT; i++) {
            PostCover postCover = BeanTest.createPostCover();
            allPostCovers.add(postCover);
        }
    }

    @Override
    public int getPostSize() {
        return allPostCovers.size();
    }

    @Override
    public void addPostCover(PostCover itemPostCover) {
        allPostCovers.add(itemPostCover);
    }

    @Override
    public void addPostCover(PostCover... itemPostCovers) {
        for (PostCover itemPostCover : itemPostCovers) {
            addPostCover(itemPostCover);
        }
    }

    @Override
    public void addPostCover(Collection<PostCover> itemPostCovers) {
        allPostCovers.addAll(itemPostCovers);
    }

    @Override
    public PostCover getPostCoverByPosition(int position) {
        return allPostCovers.get(position);
    }
}
