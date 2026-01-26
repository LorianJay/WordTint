package com.github.lorenj.wordtint.test;

import android.content.Context;

import com.github.lorenj.wordtint.entity.Comment;
import com.github.lorenj.wordtint.entity.PostCover;
import com.github.lorenj.wordtint.entity.UserInfo;
import com.github.lorenj.wordtint.R;

import java.util.Random;
import java.util.UUID;

/**
 * @author cnsukidayo
 * @date 2023/2/2 14:00
 */
public class BeanTest {
    private static Random random = new Random();
    private static UserInfo userInfo;

    public static PostCover createPostCover() {
        PostCover postCover = new PostCover();
        postCover.setPraiseCount(random.nextInt(1000000));
        postCover.setUserName(UUID.randomUUID().toString().substring(0, 8));
        postCover.setTitle(UUID.randomUUID().toString().substring(0, random.nextInt(36)));
        return postCover;
    }

    public static UserInfo createUserInfo() {
        if (userInfo == null) {
            userInfo = new UserInfo();
            int position = random.nextInt(4);
            if (position % 4 == 0) {
                userInfo.setUserFacePathID(R.drawable.post_cover0);
            } else if (position % 4 == 1) {
                userInfo.setUserFacePathID(R.drawable.post_cover1);
            } else if (position % 4 == 2) {
                userInfo.setUserFacePathID(R.drawable.post_cover2);
            } else {
                userInfo.setUserFacePathID(R.drawable.post_cover3);
            }
            userInfo.setUserName(UUID.randomUUID().toString().substring(0, 8));
            userInfo.setUserLevel(random.nextInt(5) + 1);
            userInfo.setVipLevel(random.nextInt(5) + 1);
            userInfo.setMoney(random.nextInt(1000000));
        }
        return userInfo;
    }

    public static Comment createComment(Context context) {
        Comment comment = new Comment();
        int face = random.nextInt(5);
        if (face == 0) {
            comment.setFace(R.drawable.ikun0);
        } else if (face == 1) {
            comment.setFace(R.drawable.ikun1);
        } else if (face == 2) {
            comment.setFace(R.drawable.ikun2);
        } else if (face == 3) {
            comment.setFace(R.drawable.ikun3);
        } else {
            comment.setFace(R.drawable.ikun4);
        }
        int name = random.nextInt(4);
        if (name == 0) {
            comment.setName("一个真正的Man");
        } else if (name == 1) {
            comment.setName("香精煎鱼");
        } else if (name == 2) {
            comment.setName("油饼食不食?");
        } else {
            comment.setName("是故意的还是不小心的?");
        }
        comment.setLevel(random.nextInt(6));
        comment.setPraise(random.nextInt(8000));
        int commentContext = random.nextInt(3);
        if (commentContext == 0) {
            comment.setCommentContext(context.getResources().getString(R.string.comment0));
        } else if (commentContext == 1) {
            comment.setCommentContext(context.getResources().getString(R.string.comment1));
        } else {
            comment.setCommentContext(context.getResources().getString(R.string.comment2));
        }
        return comment;
    }


}
