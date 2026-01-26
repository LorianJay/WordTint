package com.github.lorenj.wordtint.entity.dto;


/**
 * @author sukidayo
 * @date 2024/2/21 14:24
 */
public class PostAbstractVO {

    private PostDTO postDTO;

    private UserProfileDTO postCreateUser;

    public PostAbstractVO() {
    }

    public PostDTO getPostDTO() {
        return postDTO;
    }

    public void setPostDTO(PostDTO postDTO) {
        this.postDTO = postDTO;
    }

    public UserProfileDTO getPostCreateUser() {
        return postCreateUser;
    }

    public void setPostCreateUser(UserProfileDTO postCreateUser) {
        this.postCreateUser = postCreateUser;
    }

}
